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
            String sql = "SELECT * FROM USUARIO WHERE CORREO = ? AND CONTRASENA = ? AND LOWER(TIPO_USUARIO) IN ('estudiante', 'docente')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idUsuario = rs.getInt("ID_USUARIO");
                String nombre = rs.getString("NOMBRE");
                String tipo = rs.getString("TIPO_USUARIO");

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
            } else {
                mostrarAlerta("Error", "Credenciales inválidas.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al conectar con la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Docente obtenerDocenteCompleto(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, d.asignatura " +
                "FROM USUARIO u JOIN DOCENTE d ON u.id_usuario = d.id_docente " +
                "WHERE u.id_usuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Docente docente = new Docente();
            docente.setIdUsuario(rs.getInt("id_usuario"));
            docente.setNombre(rs.getString("nombre"));
            docente.setCorreo(rs.getString("correo"));
            docente.setAsignatura(rs.getString("asignatura"));
            return docente;
        }
        return null;
    }

    private Estudiante obtenerEstudianteCompleto(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, g.id_grupo, g.nombre AS nombre_grupo " +
                "FROM USUARIO u JOIN ESTUDIANTE e ON u.id_usuario = e.id_estudiante " +
                "LEFT JOIN GRUPO g ON e.id_grupo = g.id_grupo " +
                "WHERE u.id_usuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Estudiante estudiante = new Estudiante();
            estudiante.setIdUsuario(rs.getInt("id_usuario"));
            estudiante.setNombre(rs.getString("nombre"));
            estudiante.setCorreo(rs.getString("correo"));

            Grupo grupo = new Grupo();
            grupo.setIdGrupo(rs.getInt("id_grupo"));
            grupo.setNombre(rs.getString("nombre_grupo"));

            estudiante.setGrupo(grupo);
            return estudiante;
        }
        return null;
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
