package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class ExamenViewController {

    @FXML private TableView<Examen> tablaExamenes;
    @FXML private TableColumn<Examen, Integer> colId;
    @FXML private TableColumn<Examen, String> colNombre;
    @FXML private TableColumn<Examen, String> colDescripcion;
    @FXML private TableColumn<Examen, Date> colFechaInicio;
    @FXML private TableColumn<Examen, Date> colFechaFin;
    @FXML private TableColumn<Examen, Integer> colTiempoLimite;
    @FXML private TableColumn<Examen, Integer> colIdDocente;
    @FXML private TableColumn<Examen, String> colTema, colGrupo;
    @FXML private ListView<Pregunta> listPreguntasDisponibles;
    @FXML private ListView<Pregunta> listPreguntasAsignadas;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtTiempoLimite;
    @FXML private TextField txtIdDocente;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Tema> cbTema;
    @FXML private ComboBox<Grupo> cbGrupo;

    private List<Tema> listaTemas;
    private ObservableList<Examen> listaExamenes;
    private FilteredList<Examen> filtroExamenes;
    private Docente docenteActual;

    @FXML
    public void initialize() {
        // Vincular columnas con atributos de la clase Examen
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colTiempoLimite.setCellValueFactory(new PropertyValueFactory<>("tiempoLimite"));
        colIdDocente.setCellValueFactory(new PropertyValueFactory<>("idDocente"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("nombreTema"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("nombreGrupo"));

        // Cargar exámenes de la BD al abrir la vista
        cargarExamenes();

        // Cargar temas en el combobox
        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));

        // Agregar listener para el combobox de temas
        cbTema.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Tema seleccionado: " + newVal.getNombre() + " (ID: " + newVal.getId() + ")");
                cargarPreguntasDisponiblesPorTema(newVal.getId());
            }
        });

        // Agregar listener para la selección de exámenes en la tabla
        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarExamen();
            }
        });

        // Configurar filtro de búsqueda
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroExamenes.setPredicate(examen -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return examen.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                        examen.getDescripcion().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(examen.getIdDocente()).contains(lowerCaseFilter);
            });
        });

        // Inicializar la lista filtrada de exámenes
        listaExamenes = FXCollections.observableArrayList(ExamenDAO.obtenerTodosLosExamenes());
        filtroExamenes = new FilteredList<>(listaExamenes, p -> true);

        // Configurar la lista ordenada
        SortedList<Examen> sortedData = new SortedList<>(filtroExamenes);
        sortedData.comparatorProperty().bind(tablaExamenes.comparatorProperty());
        tablaExamenes.setItems(sortedData);
    }

    public void inicializarDocente(Docente docente) {
        this.docenteActual = docente;
        txtIdDocente.setText(String.valueOf(docente.getIdDocente()));
        txtIdDocente.setDisable(true);

        List<Grupo> grupos = GrupoDAO.obtenerGruposPorDocente(docente.getIdDocente());
        cbGrupo.setItems(FXCollections.observableArrayList(grupos));
    }

    @FXML
    public void cargarExamenes() {
        listaExamenes = FXCollections.observableArrayList(ExamenDAO.obtenerTodosLosExamenes());
        filtroExamenes = new FilteredList<>(listaExamenes, p -> true);

        // Asegurar que la tabla tenga los datos iniciales
        tablaExamenes.setItems(listaExamenes);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroExamenes.setPredicate(examen -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return examen.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                        examen.getDescripcion().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(examen.getIdDocente()).contains(lowerCaseFilter);
            });
        });

        SortedList<Examen> sortedData = new SortedList<>(filtroExamenes);
        sortedData.comparatorProperty().bind(tablaExamenes.comparatorProperty());
        tablaExamenes.setItems(sortedData);
    }

    @FXML
    public void agregarExamen() {
        String nombreExamen = txtNombre.getText();
        String descripcionExamen = txtDescripcion.getText();// Convertir DatePicker a java.sql.Date
        Date fechaInicio = Date.valueOf(dpFechaInicio.getValue());
        Date fechaFin = Date.valueOf(dpFechaFin.getValue());
        int tiempoLimite = Integer.parseInt(txtTiempoLimite.getText());
        int idDocente = docenteActual.getIdDocente();

        try {
            if (!validarFormulario()) return;

            Tema temaSeleccionado = cbTema.getValue();
            Grupo grupoSeleccionado = cbGrupo.getValue();

            if (temaSeleccionado == null || grupoSeleccionado == null) {
                mostrarAlerta("Error", "Debes seleccionar un tema y un grupo.", Alert.AlertType.WARNING);
                return;
            }

            Examen nuevoExamen = new Examen(
                    0,
                    nombreExamen,
                    descripcionExamen,
                    fechaInicio,
                    fechaFin,
                    tiempoLimite,
                    idDocente,
                    temaSeleccionado.getId(),
                    grupoSeleccionado.getIdGrupo()
            );
            nuevoExamen.setIdTema(temaSeleccionado.getId());

            int idExamen = ExamenDAO.agregarExamenYRetornarId(nuevoExamen);
            if (idExamen > 0) {
                mostrarAlerta("Éxito", "Examen agregado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarExamen() {
        try {
            Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
            Tema temaSeleccionado = cbTema.getSelectionModel().getSelectedItem();
            Grupo grupoSeleccionado = cbGrupo.getSelectionModel().getSelectedItem();
            if (examenSeleccionado == null) {
                mostrarAlerta("Error", "Debes seleccionar un examen para editar.", Alert.AlertType.WARNING);
                return;
            }

            if (!validarFormulario()) return;

            if (temaSeleccionado == null || grupoSeleccionado == null) {
                mostrarAlerta("Error", "Debes seleccionar un tema y un grupo.", Alert.AlertType.WARNING);
                return;
            }

            // Confirmar edición
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de editar este examen?");
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

            // Actualizar los datos del examen
            examenSeleccionado.setNombre(txtNombre.getText());
            examenSeleccionado.setDescripcion(txtDescripcion.getText());
            examenSeleccionado.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
            examenSeleccionado.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
            examenSeleccionado.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText()));
            examenSeleccionado.setIdTema(temaSeleccionado.getId());
            examenSeleccionado.setIdDocente(docenteActual.getIdDocente());
            examenSeleccionado.setIdGrupo(grupoSeleccionado.getIdGrupo());

            boolean actualizado = ExamenDAO.editarExamen(examenSeleccionado);

            if (actualizado) {
                mostrarAlerta("Éxito", "Examen actualizado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "Error al actualizar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "❌ Error al editar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarExamen() {
        try {
            Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
            if (examenSeleccionado == null) {
                mostrarAlerta("Error", "⚠️ Selecciona un examen para eliminar.", Alert.AlertType.WARNING);
                return;
            }

            // Confirmar eliminación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de eliminar este examen?");
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

            if (ExamenDAO.eliminarExamen(examenSeleccionado.getId())) {
                mostrarAlerta("Éxito", "✅ Examen eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes(); // 🔥 Refrescar la tabla
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "❌ No se pudo eliminar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "❌ Error al eliminar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void asignarPregunta() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
        Pregunta preguntaSeleccionada = listPreguntasDisponibles.getSelectionModel().getSelectedItem();

        if (examenSeleccionado == null || preguntaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar un examen y una pregunta.", Alert.AlertType.ERROR);
            return;
        }

        if (ExamenPreguntaDAO.asignarPreguntaAExamen(examenSeleccionado.getId(), preguntaSeleccionada.getId())) {
            mostrarAlerta("Éxito", "Pregunta asignada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntasDeExamen();
        } else {
            mostrarAlerta("Error", "No se pudo asignar la pregunta.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarPreguntaAsignada() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
        Pregunta preguntaSeleccionada = listPreguntasAsignadas.getSelectionModel().getSelectedItem();

        if (examenSeleccionado == null || preguntaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar un examen y una pregunta asignada.", Alert.AlertType.ERROR);
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas eliminar esta pregunta del examen?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

        if (ExamenPreguntaDAO.eliminarPreguntaDeExamen(examenSeleccionado.getId(), preguntaSeleccionada.getId())) {
            mostrarAlerta("Éxito", "Pregunta eliminada del examen correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntasDeExamen(); // 🔥 Refrescar la lista de asignadas
        } else {
            mostrarAlerta("Error", "No se pudo eliminar la pregunta del examen.", Alert.AlertType.ERROR);
        }
    }

    private void cargarPreguntasDeExamen() {
        Examen examen = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examen != null) {
            List<Pregunta> preguntas = ExamenPreguntaDAO.obtenerPreguntasDeExamen(examen.getId());
            listPreguntasAsignadas.setItems(FXCollections.observableArrayList(preguntas));
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        txtTiempoLimite.clear();
        cbTema.getSelectionModel().clearSelection();
        cbGrupo.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        String mensajeError = "";
        if (txtNombre.getText().isEmpty()) mensajeError += "Nombre requerido.\n";
        if (txtDescripcion.getText().isEmpty()) mensajeError += "Descripción requerida.\n";
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) mensajeError += "Fechas requeridas.\n";
        if (txtTiempoLimite.getText().isEmpty()) mensajeError += "Tiempo requerido.\n";
        if (cbTema.getValue() == null) mensajeError += "Tema requerido.\n";
        if (cbGrupo.getValue() == null) mensajeError += "Grupo requerido.\n";

        // Validar que el tiempo límite sea un número entero positivo
        try {
            int tiempoLimite = Integer.parseInt(txtTiempoLimite.getText());
            if (tiempoLimite <= 0) mensajeError += "⚠️ El tiempo límite debe ser un número positivo.\n";
        } catch (NumberFormatException e) {
            mensajeError += "⚠️ El tiempo límite debe ser un número entero.\n";
        }

        // Validar que el ID del docente sea un número entero positivo
        try {
            int idDocente = docenteActual.getIdDocente();
            if (idDocente <= 0) mensajeError += "⚠️ El ID del docente debe ser un número positivo.\n";
        } catch (NumberFormatException e) {
            mensajeError += "⚠️ El ID del docente debe ser un número entero.\n";
        }

        // Validar que la fecha de inicio sea antes que la fecha de fin
        if (dpFechaInicio.getValue() != null && dpFechaFin.getValue() != null) {
            if (dpFechaInicio.getValue().isAfter(dpFechaFin.getValue())) {
                mensajeError += "⚠️ La fecha de inicio no puede ser después de la fecha de fin.\n";
            }
        }

        // Si hay errores, mostrar mensaje y retornar falso
        if (!mensajeError.isEmpty()) {
            mostrarAlerta("Error en el formulario", mensajeError, Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void cargarPreguntasDisponiblesPorTema(int idTema) {
        List<Pregunta> preguntasFiltradas = PreguntaDAO.obtenerPreguntasPorTema(idTema);
        System.out.println("Preguntas cargadas para tema ID " + idTema + ": " + preguntasFiltradas.size());

        if (preguntasFiltradas.isEmpty()) {
            mostrarAlerta("Información", "No hay preguntas disponibles para este tema.", Alert.AlertType.INFORMATION);
        }

        listPreguntasDisponibles.setItems(FXCollections.observableArrayList(preguntasFiltradas));
    }

    private void seleccionarExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();

        if (examenSeleccionado != null) {
            txtNombre.setText(examenSeleccionado.getNombre());
            txtDescripcion.setText(examenSeleccionado.getDescripcion());
            dpFechaInicio.setValue(examenSeleccionado.getFechaInicio().toLocalDate());
            dpFechaFin.setValue(examenSeleccionado.getFechaFin().toLocalDate());
            txtTiempoLimite.setText(String.valueOf(examenSeleccionado.getTiempoLimite()));
            txtIdDocente.setText(String.valueOf(examenSeleccionado.getIdDocente()));

            // Seleccionar el tema en el ComboBox
            for (Tema tema : listaTemas) {
                if (tema.getId() == examenSeleccionado.getIdTema()) {
                    cbTema.getSelectionModel().select(tema);
                    break;
                }
            }

            for (Grupo g : cbGrupo.getItems()) {
                if (g.getIdGrupo() == examenSeleccionado.getIdGrupo()) {
                    cbGrupo.getSelectionModel().select(g);
                    break;
                }
            }
            // 🔥 Aquí cargas las preguntas filtradas por el tema del examen
            cargarPreguntasDisponiblesPorTema(examenSeleccionado.getIdTema());
            cargarPreguntasDeExamen(); // También actualiza la lista de asignadas
        }
    }
}
