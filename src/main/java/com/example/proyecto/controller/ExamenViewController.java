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
import java.sql.SQLException;
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
    @FXML private ComboBox<String> cbModoSeleccion;
    @FXML private TextField txtTiempoPorPregunta;
    @FXML private ComboBox<Tema> cbTema;
    @FXML private ComboBox<Grupo> cbGrupo;
    @FXML private TextField txtNotaAsignacion;
    @FXML private TextField txtIntentosPermitidos;

    private List<Tema> listaTemas;
    private ObservableList<Examen> listaExamenes;
    private FilteredList<Examen> filtroExamenes;
    private Docente docenteActual;
    private Examen ExamenActual;

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
        cbModoSeleccion.setItems(FXCollections.observableArrayList("Manual", "Aleatorio"));

        cbTema.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarPreguntasDisponibles(newVal.getId());
        });

        listPreguntasDisponibles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNotaAsignacion.setText(String.valueOf(newVal.getValorNota()));
            }
        });

        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) seleccionarExamen();
        });

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroExamenes.setPredicate(examen -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return examen.getNombre().toLowerCase().contains(lower) ||
                        examen.getDescripcion().toLowerCase().contains(lower) ||
                        String.valueOf(examen.getIdDocente()).contains(lower);
            });
        });
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
        SortedList<Examen> sorted = new SortedList<>(filtroExamenes);
        sorted.comparatorProperty().bind(tablaExamenes.comparatorProperty());
        tablaExamenes.setItems(sorted);
    }

    @FXML
    public void agregarExamen() {
        if (!validarFormulario()) return;

        Examen nuevoExamen = new Examen();
        nuevoExamen.setNombre(txtNombre.getText().trim());
        nuevoExamen.setDescripcion(txtDescripcion.getText().trim());
        nuevoExamen.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
        nuevoExamen.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
        nuevoExamen.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText().trim()));
        nuevoExamen.setNumeroPreguntas(Integer.parseInt(txtNumPreguntas.getText().trim()));
        nuevoExamen.setModoSeleccion(cbModoSeleccion.getValue());
        nuevoExamen.setTiempoPorPregunta(Integer.parseInt(txtTiempoPorPregunta.getText().trim()));
        nuevoExamen.setIdTema(cbTema.getValue().getId());
        nuevoExamen.setIdGrupo(cbGrupo.getValue().getIdGrupo());
        nuevoExamen.setIdDocente(docenteActual.getIdDocente());
        nuevoExamen.setIntentosPermitidos(Integer.parseInt(txtIntentosPermitidos.getText().trim()));

        boolean exito = ExamenDAO.agregarExamen(nuevoExamen);

        if (exito) {
            mostrarAlerta("Éxito", "Examen creado correctamente.", Alert.AlertType.INFORMATION);
            cargarExamenes();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo crear el examen.", Alert.AlertType.ERROR);
        }
    }


    private void cargarPreguntasDisponibles(int idTema) {
        if (ExamenActual == null) {
            System.out.println("⚠ No se ha seleccionado un examen aún.");
            return; // evita el NullPointerException
        }

        try {
            List<Pregunta> preguntas = ExamenPreguntaDAO.obtenerPreguntasPorTemaYDisponibles(
                    idTema,
                    docenteActual.getIdDocente(),
                    ExamenActual.getId()
            );
            listPreguntasDisponibles.setItems(FXCollections.observableArrayList(preguntas));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seleccionarExamen() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        this.ExamenActual = tablaExamenes.getSelectionModel().getSelectedItem();

        if (ex != null) {
            txtNombre.setText(ex.getNombre());
            txtDescripcion.setText(ex.getDescripcion());
            dpFechaInicio.setValue(ex.getFechaInicio().toLocalDate());
            dpFechaFin.setValue(ex.getFechaFin().toLocalDate());
            txtTiempoLimite.setText(String.valueOf(ex.getTiempoLimite()));
            txtIdDocente.setText(String.valueOf(ex.getIdDocente()));
            txtNumPreguntas.setText(String.valueOf(ex.getNumeroPreguntas()));
            cbModoSeleccion.setValue(ex.getModoSeleccion());
            txtTiempoPorPregunta.setText(String.valueOf(ex.getTiempoPorPregunta()));
            txtIntentosPermitidos.setText(String.valueOf(ex.getIntentosPermitidos()));
            cbTema.getItems().stream().filter(t -> t.getId() == ex.getIdTema()).findFirst().ifPresent(t -> cbTema.getSelectionModel().select(t));
            cbGrupo.getItems().stream().filter(g -> g.getIdGrupo() == ex.getIdGrupo()).findFirst().ifPresent(g -> cbGrupo.getSelectionModel().select(g));
            cargarPreguntasDisponibles(ex.getIdTema());

            // ✅ CARGAR preguntas asignadas al examen
            listPreguntasAsignadas.setItems(FXCollections.observableArrayList(
                    ExamenPreguntaDAO.obtenerPreguntasDeExamen(ex.getId())
            ));
        }
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
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
        txtIntentosPermitidos.clear();
    }

    private boolean validarFormulario() {
        StringBuilder error = new StringBuilder();
        if (txtNombre.getText().isEmpty()) error.append("Nombre requerido.\n");
        if (txtDescripcion.getText().isEmpty()) error.append("Descripción requerida.\n");
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) error.append("Fechas requeridas.\n");
        if (txtTiempoLimite.getText().isEmpty()) error.append("Tiempo requerido.\n");
        if (txtNumPreguntas.getText().isEmpty()) error.append("Número de preguntas requerido.\n");
        if (cbModoSeleccion.getValue() == null) error.append("Modo de selección requerido.\n");
        if (txtTiempoPorPregunta.getText().isEmpty()) error.append("Tiempo por pregunta requerido.\n");
        if (cbTema.getValue() == null) error.append("Tema requerido.\n");
        if (cbGrupo.getValue() == null) error.append("Grupo requerido.\n");
        if (txtIntentosPermitidos.getText().isEmpty()) error.append("Intentos permitidos requerido.\n");

        try {
            if (Integer.parseInt(txtTiempoLimite.getText()) <= 0) error.append("Tiempo debe ser positivo.\n");
            if (Integer.parseInt(txtNumPreguntas.getText()) <= 0) error.append("Mínimo 1 pregunta.\n");
            if (Integer.parseInt(txtTiempoPorPregunta.getText()) <= 0) error.append("Tiempo por pregunta inválido.\n");
            if (Integer.parseInt(txtIntentosPermitidos.getText()) <= 0) error.append("Los intentos deben ser mayor que 0.\n");
        } catch (NumberFormatException e) {
            error.append("Valores numéricos inválidos.\n");
        }

        if (!error.toString().isEmpty()) {
            mostrarAlerta("Error en formulario", error.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    public void editarExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examenSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un examen para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (!validarFormulario()) return;

        examenSeleccionado.setNombre(txtNombre.getText().trim());
        examenSeleccionado.setDescripcion(txtDescripcion.getText().trim());
        examenSeleccionado.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
        examenSeleccionado.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
        examenSeleccionado.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText().trim()));
        examenSeleccionado.setNumeroPreguntas(Integer.parseInt(txtNumPreguntas.getText().trim()));
        examenSeleccionado.setModoSeleccion(cbModoSeleccion.getValue());
        examenSeleccionado.setTiempoPorPregunta(Integer.parseInt(txtTiempoPorPregunta.getText().trim()));
        examenSeleccionado.setIdTema(cbTema.getValue().getId());
        examenSeleccionado.setIdGrupo(cbGrupo.getValue().getIdGrupo());
        examenSeleccionado.setIntentosPermitidos(Integer.parseInt(txtIntentosPermitidos.getText().trim()));

        boolean actualizado = ExamenDAO.editarExamen(examenSeleccionado);

        if (actualizado) {
            mostrarAlerta("Éxito", "Examen actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarExamenes();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el examen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();

        if (examenSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un examen para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar este examen?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminado = ExamenDAO.eliminarExamen(examenSeleccionado.getId());
            if (eliminado) {
                mostrarAlerta("Éxito", "Examen eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el examen.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void asignarPregunta() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        Pregunta pr = listPreguntasDisponibles.getSelectionModel().getSelectedItem();
        if (ex == null || pr == null) {
            mostrarAlerta("Error", "Selecciona un examen y una pregunta.", Alert.AlertType.ERROR);
            return;
        }
        try {
            double nota = Double.parseDouble(txtNotaAsignacion.getText().trim());
            if (nota <= 0 || nota > 5) throw new NumberFormatException();
            if (ExamenPreguntaDAO.asignarPreguntaAExamen(ex.getId(), pr.getId(), nota)) {
                mostrarAlerta("Éxito", "Pregunta asignada con nota.", Alert.AlertType.INFORMATION);

                // ✅ Actualizar las listas inmediatamente
                actualizarListasPreguntas();

                // ✅ Limpiar el campo de nota
                txtNotaAsignacion.clear();

            } else mostrarAlerta("Error", "No se pudo asignar.", Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Nota inválida (0.1 - 5).", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void eliminarPreguntaAsignada() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        Pregunta pr = listPreguntasAsignadas.getSelectionModel().getSelectedItem();
        if (ex == null || pr == null) {
            mostrarAlerta("Error", "Selecciona examen y pregunta.", Alert.AlertType.ERROR);
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar esta pregunta?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = conf.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            if (ExamenPreguntaDAO.eliminarPreguntaDeExamen(ex.getId(), pr.getId())) {
                mostrarAlerta("Éxito", "Pregunta eliminada.", Alert.AlertType.INFORMATION);

                // ✅ Actualizar las listas inmediatamente
                actualizarListasPreguntas();

            } else mostrarAlerta("Error", "No se pudo eliminar.", Alert.AlertType.ERROR);
        }
    }

    /**
     * ✅ Método auxiliar para actualizar ambas listas de preguntas
     */
    private void actualizarListasPreguntas() {
        if (ExamenActual == null) return;

        // Actualizar lista de preguntas disponibles
        Tema temaSeleccionado = cbTema.getValue();
        if (temaSeleccionado != null) {
            cargarPreguntasDisponibles(temaSeleccionado.getId());
        }

        // Actualizar lista de preguntas asignadas
        listPreguntasAsignadas.setItems(FXCollections.observableArrayList(
                ExamenPreguntaDAO.obtenerPreguntasDeExamen(ExamenActual.getId())
        ));
    }
}
