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
    @FXML private ComboBox<Tema> cbTema;
    @FXML private ComboBox<Grupo> cbGrupo;

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
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("nombreGrupo"));

        cargarExamenes();

        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));

        cbTema.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarPreguntasDisponiblesPorTema(newVal.getId());
        });

        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) seleccionarExamen();
        });

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            filtroExamenes.setPredicate(examen -> {
                if (newVal == null || newVal.trim().isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return examen.getNombre().toLowerCase().contains(lower)
                        || examen.getDescripcion().toLowerCase().contains(lower)
                        || String.valueOf(examen.getIdDocente()).contains(lower);
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
        SortedList<Examen> sortedData = new SortedList<>(filtroExamenes);
        sortedData.comparatorProperty().bind(tablaExamenes.comparatorProperty());
        tablaExamenes.setItems(sortedData);
    }

    @FXML
    public void agregarExamen() {
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
                    txtNombre.getText(),
                    txtDescripcion.getText(),
                    Date.valueOf(dpFechaInicio.getValue()),
                    Date.valueOf(dpFechaFin.getValue()),
                    Integer.parseInt(txtTiempoLimite.getText()),
                    docenteActual.getIdDocente(),
                    temaSeleccionado.getId(),
                    grupoSeleccionado.getIdGrupo()
            );

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
        Examen examen = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examen == null) {
            mostrarAlerta("Error", "Debes seleccionar un examen para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (!validarFormulario()) return;

        Tema tema = cbTema.getValue();
        Grupo grupo = cbGrupo.getValue();

        if (tema == null || grupo == null) {
            mostrarAlerta("Error", "Debes seleccionar un tema y un grupo.", Alert.AlertType.WARNING);
            return;
        }

        examen.setNombre(txtNombre.getText());
        examen.setDescripcion(txtDescripcion.getText());
        examen.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
        examen.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
        examen.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText()));
        examen.setIdTema(tema.getId());
        examen.setIdDocente(docenteActual.getIdDocente());
        examen.setIdGrupo(grupo.getIdGrupo());

        boolean actualizado = ExamenDAO.editarExamen(examen);

        if (actualizado) {
            mostrarAlerta("Éxito", "Examen actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarExamenes();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "Error al actualizar el examen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarExamen() {
        Examen examen = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examen == null) {
            mostrarAlerta("Error", "Debes seleccionar un examen para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de eliminar el examen seleccionado?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminado = ExamenDAO.eliminarExamen(examen.getId());
            if (eliminado) {
                mostrarAlerta("Éxito", "Examen eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "Error al eliminar el examen.", Alert.AlertType.ERROR);
            }
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

        if (ExamenPreguntaDAO.eliminarPreguntaDeExamen(examenSeleccionado.getId(), preguntaSeleccionada.getId())) {
            mostrarAlerta("Éxito", "Pregunta eliminada correctamente.", Alert.AlertType.INFORMATION);
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
        cbTema.getSelectionModel().clearSelection();
        cbGrupo.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        String msg = "";
        if (txtNombre.getText().isEmpty()) msg += "Nombre requerido.\n";
        if (txtDescripcion.getText().isEmpty()) msg += "Descripción requerida.\n";
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) msg += "Fechas requeridas.\n";
        if (txtTiempoLimite.getText().isEmpty()) msg += "Tiempo requerido.\n";
        if (cbTema.getValue() == null) msg += "Tema requerido.\n";
        if (cbGrupo.getValue() == null) msg += "Grupo requerido.\n";

        if (!msg.isEmpty()) {
            mostrarAlerta("Formulario incompleto", msg, Alert.AlertType.WARNING);
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
        List<Pregunta> preguntas = PreguntaDAO.obtenerPreguntasPorTema(idTema);
        listPreguntasDisponibles.setItems(FXCollections.observableArrayList(preguntas));
    }

    private void seleccionarExamen() {
        Examen examen = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examen != null) {
            txtNombre.setText(examen.getNombre());
            txtDescripcion.setText(examen.getDescripcion());
            dpFechaInicio.setValue(examen.getFechaInicio().toLocalDate());
            dpFechaFin.setValue(examen.getFechaFin().toLocalDate());
            txtTiempoLimite.setText(String.valueOf(examen.getTiempoLimite()));
            txtIdDocente.setText(String.valueOf(examen.getIdDocente()));

            for (Grupo g : cbGrupo.getItems()) {
                if (g.getIdGrupo() == examen.getIdGrupo()) {
                    cbGrupo.getSelectionModel().select(g);
                    break;
                }
            }

            cargarPreguntasDeExamen();
        }
    }
}
