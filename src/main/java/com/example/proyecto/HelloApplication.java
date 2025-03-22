package com.example.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Sistema de Gestión");

        initRootLayout();
        showExamenView(); // Por defecto, carga la vista de Examen
    }

    private void initRootLayout() {
        try {
            rootLayout = new BorderPane();

            // Menú
            MenuBar menuBar = new MenuBar();
            Menu menuVistas = new Menu("Vistas");
            MenuItem menuExamen = new MenuItem("Gestión de Exámenes");
            MenuItem menuPregunta = new MenuItem("Gestión de Preguntas");

            // Acciones para cambiar vistas
            menuExamen.setOnAction(e -> showExamenView());
            menuPregunta.setOnAction(e -> showPreguntaView());

            menuVistas.getItems().addAll(menuExamen, menuPregunta);
            menuBar.getMenus().add(menuVistas);

            rootLayout.setTop(menuBar);

            Scene scene = new Scene(rootLayout, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExamenView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/ExamenView.fxml"));
            rootLayout.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPreguntaView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/PreguntaView.fxml"));
            rootLayout.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
