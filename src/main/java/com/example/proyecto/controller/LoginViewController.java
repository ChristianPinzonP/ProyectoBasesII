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
            String sql = "SELECT * FROM USUARIO WHERE CORREO = ? AND CONTRASENA = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idUsuario = rs.getInt("ID_USUARIO");
                String nombre = rs.getString("NOMBRE");
                String tipo = rs.getString("TIPO_USUARIO");

                if ("Docente".equalsIgnoreCase(tipo)) {
                    Docente docente = new Docente();
                    docente.setIdUsuario(idUsuario);
                    docente.setNombre(nombre);
                    docente.setCorreo(correo);
                    // Puedes traer la asignatura si la tienes guardada en otra tabla, o establecer un valor por defecto.
                    docente.setAsignatura("Asignatura por definir");
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
            mostrarAlerta("Error", "Error al conectar con la base de datos." + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private boolean esDocente(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM DOCENTE WHERE id_docente = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private boolean esEstudiante(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM ESTUDIANTE WHERE id_estudiante = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private Docente obtenerDocenteCompleto(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, d.asignatura " +
                "FROM USUARIO u JOIN DOCENTE d ON u.id_usuario = d.id_docente WHERE u.id_usuario = ?";
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
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, g.id_grupo, g.nombre AS nombre_grupo , g.id_docente_titular " +
                "FROM USUARIO u JOIN ESTUDIANTE e ON u.id_usuario = e.id_estudiante " +
                "LEFT JOIN GRUPO g ON e.id_grupo = g.id_grupo " +
                "LEFT JOIN DOCENTE d ON g.id_docente_titular = d.id_docente " +
                "WHERE u.id_usuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Estudiante estudiante = new Estudiante();
            estudiante.setIdUsuario(rs.getInt("id_usuario"));
            estudiante.setNombre(rs.getString("nombre"));
            estudiante.setCorreo(rs.getString("correo"));

            // Cargar grupo
            Grupo grupo = new Grupo();
            grupo.setIdGrupo(rs.getInt("id_grupo"));
            grupo.setNombre(rs.getString("nombre_grupo"));
            grupo.setIdDocente(rs.getInt("id_docente_titular"));
            estudiante.setGrupo(grupo);

            return estudiante;
        }
        return null;
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