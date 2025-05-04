package com.example.proyecto.controller;

import com.example.proyecto.Estudiante;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//MainEstudianteViewController
public class MainEstudianteViewController {
    @FXML private Label lblNombreEstudiante;
    @FXML private BorderPane rootPane;
    @FXML private Label lblGrupoEstudiante;

    private Estudiante estudianteActual;

    // Otros elementos de la interfaz con @FXML
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

    public void inicializarEstudiante(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        // Inicializar la interfaz con los datos del estudiante
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        // Actualizar elementos de la interfaz con información del estudiante
        // Por ejemplo:
         lblNombreEstudiante.setText("Bienvenido, " + estudianteActual.getNombre());

        // Mostrar información del grupo si existe
        if (estudianteActual.getGrupo() != null) {
             lblGrupoEstudiante.setText("Grupo: " + estudianteActual.getGrupo().getNombre());
        } else {
             lblGrupoEstudiante.setText("Sin grupo asignado");
        }
    }

    // Otros métodos del controlador...
    //CONSULTA PARA MOSTRAR TODOS LOS DATOS DEL ESTUDIANTE
    //SELECT ID_USUARIO, NOMBRE, CORREO FROM USUARIO WHERE tipo_usuario = 'Estudiante';
}
