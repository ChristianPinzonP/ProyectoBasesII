package com.example.proyecto.controller;

import com.example.proyecto.Docente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainDocenteViewController {

    @FXML private Label lblNombreDocente;
    @FXML private BorderPane rootPane;
    @FXML private ImageView logoImage;

    private Docente docenteActual;

    @FXML
    public void initialize() {
        try {
            logoImage.setImage(new Image(getClass().getResourceAsStream("/images/edusoft_logo.png")));
        } catch (Exception e) {
            System.out.println("Error cargando logo: " + e.getMessage());
        }
    }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/LoginView.fxml"));
            Parent root = loader.load();
            Scene loginScene = new Scene(root);

            loginScene.getStylesheets().add(getClass().getResource("/com/example/proyecto/application.css").toExternalForm());

            Stage nuevoStage = new Stage();
            nuevoStage.setTitle("Login - Edusoft");
            nuevoStage.setScene(loginScene);
            nuevoStage.show();

            // ✅ Cerrar la ventana actual de forma confiable
            Stage ventanaActual = (Stage) rootPane.getScene().getWindow();
            ventanaActual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void inicializarDocente(Docente docente) {
        this.docenteActual = docente;
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        lblNombreDocente.setText("Bienvenido, " + docenteActual.getNombre());
    }

    private void cargarVista(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vistaCargada = loader.load();

            vistaCargada.getStylesheets().add(getClass().getResource("/com/example/proyecto/application.css").toExternalForm());

            Object controlador = loader.getController();

            if (controlador instanceof ExamenViewController) {
                ((ExamenViewController) controlador).inicializarDocente(docenteActual);
            }

            StackPane contenedorCentrado = new StackPane(vistaCargada);
            contenedorCentrado.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
            rootPane.setCenter(contenedorCentrado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
