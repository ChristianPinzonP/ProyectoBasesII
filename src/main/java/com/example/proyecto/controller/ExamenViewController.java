package com.example.proyecto.controller;
import com.example.proyecto.*;
import com.example.proyecto.dao.ExamenDAO;
import com.example.proyecto.dao.ExamenPreguntaDAO;
import com.example.proyecto.dao.PreguntaDAO;
import com.example.proyecto.dao.TemaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.cell.PropertyValueFactory;

public class ExamenViewController {

    @FXML private TableView<Examen> tablaExamenes;
    @FXML private TableColumn<Examen, Integer> colId;
    @FXML private TableColumn<Examen, String> colNombre;
    @FXML private TableColumn<Examen, String> colDescripcion;
    @FXML private TableColumn<Examen, Date> colFechaInicio;
    @FXML private TableColumn<Examen, Date> colFechaFin;
    @FXML private TableColumn<Examen, Integer> colTiempoLimite;
    @FXML private TableColumn<Examen, Integer> colIdDocente;
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
    private List<Tema> listaTemas;
    private ObservableList<Examen> listaExamenes;
    private FilteredList<Examen> filtroExamenes;


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
        int idDocente = Integer.parseInt(txtIdDocente.getText());

        try {
            if (!validarFormulario()) return;

            // Obtener tema seleccionado
            Tema temaSeleccionado = cbTema.getSelectionModel().getSelectedItem();
            if (temaSeleccionado == null) {
                mostrarAlerta("Error", "⚠️ Debes seleccionar un tema.", Alert.AlertType.WARNING);
                return;
            }

            // Crear examen con ID_TEMA incluido
            Examen nuevoExamen = new Examen(
                    0,
                    nombreExamen,
                    descripcionExamen,
                    fechaInicio,
                    fechaFin,
                    tiempoLimite,
                    idDocente,
                    temaSeleccionado.getId()
            );
            nuevoExamen.setIdTema(temaSeleccionado.getId());

            if (ExamenDAO.agregarExamen(nuevoExamen)) {
                mostrarAlerta("Éxito", "✅ Examen agregado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes(); // 🔥 Refrescar la tabla
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "❌ No se pudo agregar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "❌ Error al agregar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Metodo para limpiar los campos al agregar el examen
    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        txtTiempoLimite.clear();
        txtIdDocente.clear();
    }

    private boolean validarFormulario() {
        String mensajeError = "";

        // Validar que los campos no estén vacíos
        if (txtNombre.getText().isEmpty()) mensajeError += "⚠️ El nombre es obligatorio.\n";
        if (txtDescripcion.getText().isEmpty()) mensajeError += "⚠️ La descripción es obligatoria.\n";
        if (dpFechaInicio.getValue() == null) mensajeError += "⚠️ Debes seleccionar una fecha de inicio.\n";
        if (dpFechaFin.getValue() == null) mensajeError += "⚠️ Debes seleccionar una fecha de fin.\n";
        if (txtTiempoLimite.getText().isEmpty()) mensajeError += "⚠️ El tiempo límite es obligatorio.\n";
        if (txtIdDocente.getText().isEmpty()) mensajeError += "⚠️ El ID del docente es obligatorio.\n";

        // Validar que el tiempo límite sea un número entero positivo
        try {
            int tiempoLimite = Integer.parseInt(txtTiempoLimite.getText());
            if (tiempoLimite <= 0) mensajeError += "⚠️ El tiempo límite debe ser un número positivo.\n";
        } catch (NumberFormatException e) {
            mensajeError += "⚠️ El tiempo límite debe ser un número entero.\n";
        }

        // Validar que el ID del docente sea un número entero positivo
        try {
            int idDocente = Integer.parseInt(txtIdDocente.getText());
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

    // MÉTODO PARA MOSTRAR ALERTAS ⚠️
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void editarExamen() {
        try {
            Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
            Tema temaSeleccionado = cbTema.getSelectionModel().getSelectedItem();
            if (examenSeleccionado == null) {
                mostrarAlerta("Error", "⚠️ Selecciona un examen para editar.", Alert.AlertType.WARNING);
                return;
            }

            if (!validarFormulario()) return;

            if (temaSeleccionado == null) {
                mostrarAlerta("Error", "⚠️ Debes seleccionar un tema.", Alert.AlertType.WARNING);
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
            examenSeleccionado.setIdDocente(Integer.parseInt(txtIdDocente.getText()));
            examenSeleccionado.setIdTema(temaSeleccionado.getId());

            if (ExamenDAO.editarExamen(examenSeleccionado)) {
                mostrarAlerta("Éxito", "✅ Examen actualizado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes(); // 🔥 Refrescar la tabla
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "❌ No se pudo actualizar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "❌ Error al editar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void seleccionarExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();

        if (examenSeleccionado != null) {
            txtNombre.setText(examenSeleccionado.getNombre());
            txtDescripcion.setText(examenSeleccionado.getDescripcion());
            txtTiempoLimite.setText(String.valueOf(examenSeleccionado.getTiempoLimite()));
            txtIdDocente.setText(String.valueOf(examenSeleccionado.getIdDocente()));
            dpFechaInicio.setValue(examenSeleccionado.getFechaInicio().toLocalDate());
            dpFechaFin.setValue(examenSeleccionado.getFechaFin().toLocalDate());

            // Seleccionar el tema en el ComboBox
            for (Tema tema : listaTemas) {
                if (tema.getId() == examenSeleccionado.getIdTema()) {
                    cbTema.getSelectionModel().select(tema);
                    break;
                }
            }


            // 🔥 Aquí cargas las preguntas filtradas por el tema del examen
            cargarPreguntasDisponiblesPorTema(examenSeleccionado.getIdTema());
            cargarPreguntasDeExamen(); // También actualiza la lista de asignadas
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
            } else {
                mostrarAlerta("Error", "❌ No se pudo eliminar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "❌ Error al eliminar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void cargarPreguntasDisponiblesPorTema(int idTema) {
        List<Pregunta> preguntasFiltradas = PreguntaDAO.obtenerPreguntasPorTema(idTema);
        System.out.println("Preguntas cargadas para tema ID " + idTema + ": " + preguntasFiltradas.size());

        if (preguntasFiltradas.isEmpty()) {
            mostrarAlerta("Información", "No hay preguntas disponibles para este tema.", Alert.AlertType.INFORMATION);
        }

        listPreguntasDisponibles.setItems(FXCollections.observableArrayList(preguntasFiltradas));
    }

    public void cargarPreguntasDeExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examenSeleccionado != null) {
            List<Pregunta> preguntas = ExamenPreguntaDAO.obtenerPreguntasDeExamen(examenSeleccionado.getId());
            listPreguntasAsignadas.setItems(FXCollections.observableArrayList(preguntas));
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

}