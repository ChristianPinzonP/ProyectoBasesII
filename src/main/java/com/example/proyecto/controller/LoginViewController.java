package com.example.proyecto.controller;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Docente;
import com.example.proyecto.Estudiante;
import com.example.proyecto.Grupo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.*;

import static com.example.proyecto.dao.DocenteDAO.esDocente;
import static com.example.proyecto.dao.DocenteDAO.obtenerDocenteCompleto;
import static com.example.proyecto.dao.EstudianteDAO.esEstudiante;
import static com.example.proyecto.dao.EstudianteDAO.obtenerEstudianteCompleto;

//ACTUALIZACIÓN DEL LOGIN
public class LoginViewController {
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;

    @FXML
    public void login() {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor ingresa correo y contraseña.", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Primero verificamos si el usuario existe en la tabla base USUARIO
            String sqlUsuario = "SELECT ID_USUARIO, NOMBRE, CORREO FROM USUARIO WHERE CORREO = ? AND CONTRASENA = ?";
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario);
            stmtUsuario.setString(1, correo);
            stmtUsuario.setString(2, contrasena);

            ResultSet rsUsuario = stmtUsuario.executeQuery();

            if (rsUsuario.next()) {
                int idUsuario = rsUsuario.getInt("ID_USUARIO");
                String nombre = rsUsuario.getString("NOMBRE");

                // Verificar si es docente
                if (esDocente(conn, idUsuario)) {
                    Docente docente = obtenerDocenteCompleto(conn, idUsuario);
                    mostrarVistaDocente(docente);
                    return;
                }

                // Verificar si es estudiante
                if (esEstudiante(conn, idUsuario)) {
                    Estudiante estudiante = obtenerEstudianteCompleto(conn, idUsuario);
                    mostrarVistaEstudiante(estudiante);
                    return;
                }

                mostrarAlerta("Error", "El usuario no está registrado como docente ni como estudiante.", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Error", "Credenciales inválidas.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al conectar con la base de datos." + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarVistaDocente(Docente docente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/MainDocenteView.fxml"));
            Parent root = loader.load();
            Scene escenaDocente = new Scene(root, 800, 600);
            MainDocenteViewController controlador = loader.getController();
            controlador.inicializarDocente(docente);

            Stage docenteStage = new Stage();
            docenteStage.setTitle("Panel Docente");
            docenteStage.setScene(escenaDocente);
            docenteStage.show();

            cerrarVentanaActual();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarVistaEstudiante(Estudiante estudiante) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/MainEstudianteView.fxml"));
            Parent root = loader.load();
            Scene escenaEstudiante = new Scene(root, 800, 600);
            MainEstudianteViewController controlador = loader.getController();
            controlador.inicializarEstudiante(estudiante);

            Stage estudianteStage = new Stage();
            estudianteStage.setTitle("Panel Estudiante");
            estudianteStage.setScene(escenaEstudiante);
            estudianteStage.show();

            cerrarVentanaActual();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cerrarVentanaActual() {
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}