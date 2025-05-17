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
    @FXML private TableColumn<Examen, String> colTema;
    @FXML private TableColumn<Examen, String> colGrupo;

    @FXML private ListView<Pregunta> listPreguntasDisponibles;
    @FXML private ListView<Pregunta> listPreguntasAsignadas;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtTiempoLimite;
    @FXML private TextField txtIdDocente;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtNumPreguntas;
    @FXML private ComboBox<String> cbModoSeleccion; // <-- Cambiado a ComboBox
    @FXML private TextField txtTiempoPorPregunta;
    @FXML private ComboBox<Tema> cbTema;
    @FXML private ComboBox<Grupo> cbGrupo;
    @FXML private TextField txtNotaAsignacion;

    private List<Tema> listaTemas;
    private ObservableList<Examen> listaExamenes;
    private FilteredList<Examen> filtroExamenes;
    private Docente docenteActual;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colTiempoLimite.setCellValueFactory(new PropertyValueFactory<>("tiempoLimite"));
        colIdDocente.setCellValueFactory(new PropertyValueFactory<>("idDocente"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("nombreTema"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("nombreGrupo"));

        cargarExamenes();

        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));

        // Opciones para Modo Selección
        cbModoSeleccion.setItems(FXCollections.observableArrayList("Manual", "Aleatorio"));

        cbTema.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarPreguntasDisponiblesPorTema(newVal.getId());
            }
        });

        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarExamen();
            }
        });

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroExamenes.setPredicate(examen -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return examen.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                        examen.getDescripcion().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(examen.getIdDocente()).contains(lowerCaseFilter);
            });
        });

        listaExamenes = FXCollections.observableArrayList(ExamenDAO.obtenerTodosLosExamenes());
        filtroExamenes = new FilteredList<>(listaExamenes, p -> true);
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
        tablaExamenes.setItems(listaExamenes);
    }

    @FXML
    public void agregarExamen() {
        if (!validarFormulario()) return;

        try {
            String modoSeleccion = cbModoSeleccion.getValue();
            if (modoSeleccion == null) {
                mostrarAlerta("Error", "Debes seleccionar un modo de selección.", Alert.AlertType.WARNING);
                return;
            }

            String nombreExamen = txtNombre.getText();
            String descripcionExamen = txtDescripcion.getText();
            Date fechaInicio = Date.valueOf(dpFechaInicio.getValue());
            Date fechaFin = Date.valueOf(dpFechaFin.getValue());
            int tiempoLimite = Integer.parseInt(txtTiempoLimite.getText());
            int idDocente = docenteActual.getIdDocente();
            int numeroPreguntas = Integer.parseInt(txtNumPreguntas.getText());
            int tiempoPorPregunta = Integer.parseInt(txtTiempoPorPregunta.getText());
            Tema temaSeleccionado = cbTema.getValue();
            Grupo grupoSeleccionado = cbGrupo.getValue();

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
            nuevoExamen.setNumeroPreguntas(numeroPreguntas);
            nuevoExamen.setModoSeleccion(modoSeleccion);
            nuevoExamen.setTiempoPorPregunta(tiempoPorPregunta);

            int idExamen = ExamenDAO.agregarExamenYRetornarId(nuevoExamen);
            if (idExamen > 0) {
                mostrarAlerta("Éxito", "Examen agregado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo agregar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void editarExamen() {
        if (!validarFormulario()) return;

        try {
            Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
            if (examenSeleccionado == null) {
                mostrarAlerta("Error", "Debes seleccionar un examen para editar.", Alert.AlertType.WARNING);
                return;
            }

            String modoSeleccion = cbModoSeleccion.getValue();
            if (modoSeleccion == null) {
                mostrarAlerta("Error", "Debes seleccionar un modo de selección.", Alert.AlertType.WARNING);
                return;
            }

            Tema temaSeleccionado = cbTema.getSelectionModel().getSelectedItem();
            Grupo grupoSeleccionado = cbGrupo.getSelectionModel().getSelectedItem();

            if (temaSeleccionado == null || grupoSeleccionado == null) {
                mostrarAlerta("Error", "Debes seleccionar un tema y un grupo.", Alert.AlertType.WARNING);
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de editar este examen?");
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

            examenSeleccionado.setNombre(txtNombre.getText());
            examenSeleccionado.setDescripcion(txtDescripcion.getText());
            examenSeleccionado.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
            examenSeleccionado.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
            examenSeleccionado.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText()));
            examenSeleccionado.setIdTema(temaSeleccionado.getId());
            examenSeleccionado.setIdDocente(docenteActual.getIdDocente());
            examenSeleccionado.setIdGrupo(grupoSeleccionado.getIdGrupo());
            examenSeleccionado.setNumeroPreguntas(Integer.parseInt(txtNumPreguntas.getText()));
            examenSeleccionado.setModoSeleccion(modoSeleccion);
            examenSeleccionado.setTiempoPorPregunta(Integer.parseInt(txtTiempoPorPregunta.getText()));

            boolean actualizado = ExamenDAO.editarExamen(examenSeleccionado);
            if (actualizado) {
                mostrarAlerta("Éxito", "Examen actualizado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "Error al actualizar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al editar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarExamen() {
        try {
            Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
            if (examenSeleccionado == null) {
                mostrarAlerta("Error", "Selecciona un examen para eliminar.", Alert.AlertType.WARNING);
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de eliminar este examen?");
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

            if (ExamenDAO.eliminarExamen(examenSeleccionado.getId())) {
                mostrarAlerta("Éxito", "Examen eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el examen.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar el examen: " + e.getMessage(), Alert.AlertType.ERROR);
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

        double valorNota;
        try {
            valorNota = Double.parseDouble(txtNotaAsignacion.getText().trim());
            if (valorNota <= 0 || valorNota > 5) {
                mostrarAlerta("Error", "La nota debe estar entre 0.1 y 5.", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La nota debe ser un número válido.", Alert.AlertType.WARNING);
            return;
        }

        if (ExamenPreguntaDAO.asignarPreguntaAExamen(examenSeleccionado.getId(), preguntaSeleccionada.getId(), valorNota)) {
            mostrarAlerta("Éxito", "Pregunta asignada correctamente con nota.", Alert.AlertType.INFORMATION);
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

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas eliminar esta pregunta del examen?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

        if (ExamenPreguntaDAO.eliminarPreguntaDeExamen(examenSeleccionado.getId(), preguntaSeleccionada.getId())) {
            mostrarAlerta("Éxito", "Pregunta eliminada del examen correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntasDeExamen();
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
        txtNumPreguntas.clear();
        cbModoSeleccion.getSelectionModel().clearSelection();
        txtTiempoPorPregunta.clear();
        txtNotaAsignacion.clear();
        cbTema.getSelectionModel().clearSelection();
        cbGrupo.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        String mensajeError = "";

        if (txtNombre.getText().isEmpty()) mensajeError += "Nombre requerido.\n";
        if (txtDescripcion.getText().isEmpty()) mensajeError += "Descripción requerida.\n";
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) mensajeError += "Fechas requeridas.\n";
        if (txtTiempoLimite.getText().isEmpty()) mensajeError += "Tiempo requerido.\n";
        if (txtNumPreguntas.getText().isEmpty()) mensajeError += "Número de preguntas requerido.\n";
        if (cbModoSeleccion.getValue() == null) mensajeError += "Modo de selección requerido.\n";
        if (txtTiempoPorPregunta.getText().isEmpty()) mensajeError += "Tiempo por pregunta requerido.\n";
        if (cbTema.getValue() == null) mensajeError += "Tema requerido.\n";
        if (cbGrupo.getValue() == null) mensajeError += "Grupo requerido.\n";

        try {
            int tiempoLimite = Integer.parseInt(txtTiempoLimite.getText());
            if (tiempoLimite <= 0) mensajeError += "️ El tiempo límite debe ser un número positivo.\n";
        } catch (NumberFormatException e) {
            mensajeError += "️ El tiempo límite debe ser un número entero.\n";
        }

        try {
            int preguntas = Integer.parseInt(txtNumPreguntas.getText());
            if (preguntas <= 0) mensajeError += " El número de preguntas debe ser mayor a 0.\n";
        } catch (NumberFormatException e) {
            mensajeError += " El número de preguntas debe ser un número entero.\n";
        }

        try {
            int tiempoPregunta = Integer.parseInt(txtTiempoPorPregunta.getText());
            if (tiempoPregunta <= 0) mensajeError += " El tiempo por pregunta debe ser mayor a 0.\n";
        } catch (NumberFormatException e) {
            mensajeError += " El tiempo por pregunta debe ser un número entero.\n";
        }

        if (dpFechaInicio.getValue() != null && dpFechaFin.getValue() != null) {
            if (dpFechaInicio.getValue().isAfter(dpFechaFin.getValue())) {
                mensajeError += " La fecha de inicio no puede ser después de la fecha de fin.\n";
            }
        }

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
            txtNumPreguntas.setText(String.valueOf(examenSeleccionado.getNumeroPreguntas()));
            cbModoSeleccion.setValue(examenSeleccionado.getModoSeleccion());
            txtTiempoPorPregunta.setText(String.valueOf(examenSeleccionado.getTiempoPorPregunta()));

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

            cargarPreguntasDisponiblesPorTema(examenSeleccionado.getIdTema());
            cargarPreguntasDeExamen();
            }
        }
    }

