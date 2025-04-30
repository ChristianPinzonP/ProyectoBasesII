package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            String sqlUsuario = "SELECT ID_USUARIO, NOMBRE FROM USUARIO WHERE CORREO = ? AND CONTRASENA = ?";
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario);
            stmtUsuario.setString(1, correo);
            stmtUsuario.setString(2, contrasena);

            ResultSet rsUsuario = stmtUsuario.executeQuery();

            if (rsUsuario.next()) {
                int idUsuario = rsUsuario.getInt("ID_USUARIO");
                String nombre = rsUsuario.getString("NOMBRE");

                // Determinamos si es docente verificando en tabla DOCENTE
                String sqlDocente = "SELECT ID_DOCENTE FROM DOCENTE WHERE ID_DOCENTE = ?";
                PreparedStatement stmtDocente = conn.prepareStatement(sqlDocente);
                stmtDocente.setInt(1, idUsuario);
                ResultSet rsDocente = stmtDocente.executeQuery();

                if (rsDocente.next()) {
                    // Es un docente
                    mostrarAlerta("Éxito", "Inicio de sesión exitoso como Docente", Alert.AlertType.INFORMATION);

                    // Crear el objeto Docente
                    Docente docente = new Docente();
                    docente.setIdDocente(idUsuario);
                    docente.setNombre(nombre);

                    // Cerrar ventana de login
                    Stage stage = (Stage) txtCorreo.getScene().getWindow();
                    stage.close();

                    // Abrir panel docente
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/MainDocenteView.fxml"));
                    Parent root = loader.load();

                    // Pasar el objeto Docente al controlador
                    MainDocenteViewController controlador = loader.getController();
                    controlador.inicializarDocente(docente);

                    Stage docenteStage = new Stage();
                    docenteStage.setTitle("Panel Docente");
                    docenteStage.setScene(new Scene(root, 800, 600));
                    docenteStage.show();

                } else {
                    // Verificamos si es estudiante
                    String sqlEstudiante = "SELECT e.ID_ESTUDIANTE, g.ID_GRUPO, g.NOMBRE as NOMBRE_GRUPO " +
                            "FROM ESTUDIANTE e " +
                            "LEFT JOIN GRUPO g ON e.ID_GRUPO = g.ID_GRUPO " +
                            "WHERE e.ID_ESTUDIANTE = ?";
                    PreparedStatement stmtEstudiante = conn.prepareStatement(sqlEstudiante);
                    stmtEstudiante.setInt(1, idUsuario);
                    ResultSet rsEstudiante = stmtEstudiante.executeQuery();

                    if (rsEstudiante.next()) {
                        // Es un estudiante
                        Integer idGrupo = rsEstudiante.getObject("ID_GRUPO") != null ?
                                rsEstudiante.getInt("ID_GRUPO") : null;
                        String nombreGrupo = rsEstudiante.getString("NOMBRE_GRUPO");

                        String mensajeGrupo = nombreGrupo != null ?
                                " del grupo " + nombreGrupo : " (sin grupo asignado)";

                        mostrarAlerta("Éxito", "Inicio de sesión exitoso como Estudiante" +
                                mensajeGrupo, Alert.AlertType.INFORMATION);

                        // Crear objetos de modelo
                        Estudiante estudiante = new Estudiante();
                        estudiante.setIdEstudiante(idUsuario);
                        estudiante.setNombre(nombre);

                        Grupo grupo = null;
                        if (idGrupo != null) {
                            grupo = new Grupo();
                            grupo.setIdGrupo(idGrupo);
                            grupo.setNombre(nombreGrupo);
                            estudiante.setGrupo(grupo);
                        }

                        // Cerrar ventana de login
                        Stage stage = (Stage) txtCorreo.getScene().getWindow();
                        stage.close();

                        // Abrir panel estudiante
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/MainEstudianteView.fxml"));
                        Parent root = loader.load();

                        // Pasar el objeto Estudiante al controlador
                        MainEstudianteViewController controlador = loader.getController();
                        controlador.inicializarEstudiante(estudiante);

                        Stage estudianteStage = new Stage();
                        estudianteStage.setTitle("Panel Estudiante");
                        estudianteStage.setScene(new Scene(root, 800, 600));
                        estudianteStage.show();
                    } else {
                        // Usuario existe pero no es ni docente ni estudiante
                        mostrarAlerta("Error", "Usuario existente pero sin rol asignado.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                mostrarAlerta("Error", "Correo o contraseña incorrectos.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al iniciar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}