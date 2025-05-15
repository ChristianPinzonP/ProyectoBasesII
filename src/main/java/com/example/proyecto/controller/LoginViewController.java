package com.example.proyecto.controller;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Docente;
import com.example.proyecto.Estudiante;
import com.example.proyecto.Grupo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

import static com.example.proyecto.dao.EstudianteDAO.obtenerEstudianteCompleto;

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
            CallableStatement stmt = conn.prepareCall("{CALL LOGIN_USUARIO(?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            stmt.registerOutParameter(3, Types.INTEGER); // ID_USUARIO
            stmt.registerOutParameter(4, Types.VARCHAR); // NOMBRE
            stmt.registerOutParameter(5, Types.VARCHAR); // TIPO_USUARIO
            stmt.registerOutParameter(6, Types.VARCHAR); // ESTADO

            stmt.execute();

            String estado = stmt.getString(6);
            if ("OK".equals(estado)) {
                int idUsuario = stmt.getInt(3);
                String nombre = stmt.getString(4);
                String tipo = stmt.getString(5);

                System.out.println("✅ Usuario autenticado como: " + tipo);

                if ("Docente".equalsIgnoreCase(tipo)) {
                    Docente docente = obtenerDocenteCompleto(conn, idUsuario);
                    mostrarVistaDocente(docente);
                } else if ("Estudiante".equalsIgnoreCase(tipo)) {
                    Estudiante estudiante = obtenerEstudianteCompleto(conn, idUsuario);
                    mostrarVistaEstudiante(estudiante);
                } else {
                    mostrarAlerta("Error", "Tipo de usuario desconocido.", Alert.AlertType.ERROR);
                }
            } else if ("NO_ENCONTRADO".equals(estado)) {
                mostrarAlerta("Error", "Credenciales inválidas.", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Error", "Error inesperado en la autenticación.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al conectar con la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Docente obtenerDocenteCompleto(Connection conn, int idUsuario) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL OBTENER_DOCENTE_COMPLETO(?, ?, ?, ?, ?)}");
        stmt.setInt(1, idUsuario);
        stmt.registerOutParameter(2, Types.VARCHAR); // nombre
        stmt.registerOutParameter(3, Types.VARCHAR); // correo
        stmt.registerOutParameter(4, Types.VARCHAR); // asignatura
        stmt.registerOutParameter(5, Types.VARCHAR); // estado

        stmt.execute();

        String estado = stmt.getString(5);
        if ("OK".equals(estado)) {
            Docente docente = new Docente();
            docente.setIdUsuario(idUsuario);
            docente.setNombre(stmt.getString(2));
            docente.setCorreo(stmt.getString(3));
            docente.setAsignatura(stmt.getString(4));
            return docente;
        } else {
            return null;
        }
    }

    private void mostrarVistaDocente(Docente docente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/MainDocenteView.fxml"));
            Parent root = loader.load();
            MainDocenteViewController controlador = loader.getController();
            controlador.inicializarDocente(docente);

            Stage stage = new Stage();
            stage.setTitle("Panel Docente");
            stage.setScene(new Scene(root));
            stage.show();
            cerrarVentanaActual();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista del docente.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarVistaEstudiante(Estudiante estudiante) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/MainEstudianteView.fxml"));
            Parent root = loader.load();
            MainEstudianteViewController controlador = loader.getController();
            controlador.inicializarEstudiante(estudiante);

            Stage stage = new Stage();
            stage.setTitle("Panel Estudiante");
            stage.setScene(new Scene(root));
            stage.show();
            cerrarVentanaActual();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista del estudiante.", Alert.AlertType.ERROR);
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

