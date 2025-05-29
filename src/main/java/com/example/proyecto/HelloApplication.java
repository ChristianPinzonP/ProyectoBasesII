package com.example.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private Stage primaryStage;

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

    public static void main(String[] args) {
        launch(args);
    }
}
