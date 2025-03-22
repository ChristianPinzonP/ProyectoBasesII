package com.example.proyecto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.proyecto.ExamenPreguntaDAO;
import java.sql.*;
import java.util.ArrayList;
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
    @FXML private Button btnAsignarPregunta;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtTiempoLimite;
    @FXML private TextField txtIdDocente;
    @FXML private TextField txtBuscar;
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

        // Asociar la lista de exámenes con la tabla
        tablaExamenes.setItems(listaExamenes);

        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarExamen();
            }
        });

        // Cargar exámenes de la BD al abrir la vista
        cargarExamenes();
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
        try {
            if (!validarFormulario()) return;

            // Convertir DatePicker a java.sql.Date
            Date fechaInicio = Date.valueOf(dpFechaInicio.getValue());
            Date fechaFin = Date.valueOf(dpFechaFin.getValue());

            Examen nuevoExamen = new Examen(0, txtNombre.getText(), txtDescripcion.getText(),
                    fechaInicio, fechaFin,
                    Integer.parseInt(txtTiempoLimite.getText()),
                    Integer.parseInt(txtIdDocente.getText()));

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
            if (examenSeleccionado == null) {
                mostrarAlerta("Error", "⚠️ Selecciona un examen para editar.", Alert.AlertType.WARNING);
                return;
            }

            if (!validarFormulario()) return;

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
            dpFechaInicio.setValue(examenSeleccionado.getFechaInicio().toLocalDate());
            dpFechaFin.setValue(examenSeleccionado.getFechaFin().toLocalDate());
            txtTiempoLimite.setText(String.valueOf(examenSeleccionado.getTiempoLimite()));
            txtIdDocente.setText(String.valueOf(examenSeleccionado.getIdDocente()));
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


    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        txtTiempoLimite.clear();
        txtIdDocente.clear();
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


}
