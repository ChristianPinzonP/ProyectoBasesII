package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EstudianteExamenesController {

    @FXML private TableView<Examen> tablaExamenes;
    @FXML private TableColumn<Examen, String> colNombre;
    @FXML private TableColumn<Examen, String> colDescripcion;
    @FXML private TableColumn<Examen, LocalDate> colFechaInicio;
    @FXML private TableColumn<Examen, LocalDate> colFechaFin;
    @FXML private Button btnPresentar;

    private Estudiante estudiante;
    private ObservableList<Examen> examenesAsignados;

    public void inicializar(Estudiante estudiante) {
        this.estudiante = estudiante;

        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        colFechaInicio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaInicio().toLocalDate()));
        colFechaFin.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaFin().toLocalDate()));

        cargarExamenes();
    }

    private void cargarExamenes() {
        List<Examen> lista = ExamenDAO.obtenerExamenesPorGrupo(estudiante.getGrupo().getIdGrupo());
        examenesAsignados = FXCollections.observableArrayList(lista);
        tablaExamenes.setItems(examenesAsignados);
    }

    @FXML
    public void presentarExamen() {
        Examen examen = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examen == null) {
            mostrarAlerta("Error", "Selecciona un examen.", Alert.AlertType.WARNING);
            return;
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(examen.getFechaInicio().toLocalDate()) || hoy.isAfter(examen.getFechaFin().toLocalDate())) {
            mostrarAlerta("Fuera de fecha", "Este examen no está disponible actualmente.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/PresentarExamenView.fxml"));
            Parent root = loader.load();
            PresentarExamenController controller = loader.getController();
            controller.inicializar(examen, estudiante);

            Stage stage = new Stage();
            stage.setTitle("Presentar Examen");
            stage.setScene(new Scene(root));
            stage.show();

            Stage ventanaActual = (Stage) btnPresentar.getScene().getWindow();
            ventanaActual.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el examen.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
