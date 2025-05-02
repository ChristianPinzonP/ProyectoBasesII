package com.example.proyecto.controller;

import com.example.proyecto.Docente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainDocenteViewController {
    @FXML private Label lblNombreDocente;
    @FXML private BorderPane rootPane; // Asegúrate de tener este fx:id en MainDocenteView.fxml
    @FXML private MenuItem menuCerrarSesion;
    private Docente docenteActual;

    @FXML
    public void mostrarExamenes() {
        cargarVista("/com/example/proyecto/ExamenView.fxml");
    }

    @FXML
    public void mostrarPreguntas() {
        cargarVista("/com/example/proyecto/PreguntaView.fxml");
    }

    @FXML
    public void cerrarSesion() {
        try {
            // Cerrar la ventana actual
            Stage stageActual = (Stage) rootPane.getScene().getWindow();
            stageActual.close();

            // Abrir la ventana de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/LoginView.fxml"));
            Scene sceneLogin = new Scene(loader.load(), 400, 300);

            Stage nuevoStage = new Stage();
            nuevoStage.setTitle("Login - Edusoft");
            nuevoStage.setScene(sceneLogin);
            nuevoStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inicializarDocente(Docente docente) {
        this.docenteActual = docente;
        // Inicializar la interfaz con los datos del docente
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        // Actualizar elementos de la interfaz con información del docente
        // Por ejemplo:
        lblNombreDocente.setText("Bienvenido, " + docenteActual.getNombre());
    }

    private void cargarVista(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vistaCargada = loader.load();

            // Aplicar también el CSS a la vista que cargamos
            vistaCargada.getStylesheets().add(getClass().getResource("/com/example/proyecto/application.css").toExternalForm());

            // Envolver en un StackPane para centrarla
            StackPane contenedorCentrado = new StackPane(vistaCargada);
            contenedorCentrado.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

            rootPane.setCenter(contenedorCentrado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
