package com.example.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/proyecto/LoginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);

            // 🔥 Aplicar el CSS globalmente
            scene.getStylesheets().add(getClass().getResource("/com/example/proyecto/application.css").toExternalForm());

            primaryStage.setTitle("Login - Edusoft");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
    private void initRootLayout() {
        try {
            rootLayout = new BorderPane();

            Scene scene = new Scene(rootLayout, 800, 600);

            // 🔥 Aplicar el CSS globalmente
            scene.getStylesheets().add(getClass().getResource("/com/example/proyecto/application.css").toExternalForm());

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPreguntaView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/PreguntaView.fxml"));
            rootLayout.setCenter(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
