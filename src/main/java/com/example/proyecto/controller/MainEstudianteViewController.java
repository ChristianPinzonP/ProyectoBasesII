package com.example.proyecto.controller;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Estudiante;
import com.example.proyecto.Grupo;
import com.example.proyecto.EstadisticaExamen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainEstudianteViewController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblGrupo;
    @FXML private ImageView logoImage;

    @FXML private TableView<EstadisticaExamen> tablaEstadisticas;
    @FXML private TableColumn<EstadisticaExamen, String> colNombreExamen;
    @FXML private TableColumn<EstadisticaExamen, Double> colNota;
    @FXML private TableColumn<EstadisticaExamen, String> colFecha;

    private Estudiante estudiante;

    @FXML
    public void initialize() {
        try {
            logoImage.setImage(new Image(getClass().getResourceAsStream("/images/edusoft_logo.png")));
        } catch (Exception e) {
            System.out.println("Error cargando logo: " + e.getMessage());
        }
    }

    public void inicializarEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        lblBienvenida.setText("Bienvenido, " + estudiante.getNombre());
        Grupo grupo = estudiante.getGrupo();
        if (grupo != null) {
            lblGrupo.setText("Grupo: " + grupo.getNombre());
        } else {
            lblGrupo.setText("Grupo: No asignado");
        }

        configurarTablaEstadisticas();
        cargarEstadisticas();
    }

    private void configurarTablaEstadisticas() {
        colNombreExamen.setCellValueFactory(cell -> cell.getValue().nombreExamenProperty());
        colNota.setCellValueFactory(cell -> cell.getValue().notaProperty().asObject());
        colFecha.setCellValueFactory(cell -> cell.getValue().fechaProperty());
    }

    private void cargarEstadisticas() {
        ObservableList<EstadisticaExamen> estadisticas = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
            SELECT E.NOMBRE AS EXAMEN, PE.CALIFICACION, TO_CHAR(PE.FECHA_PRESENTACION, 'DD/MM/YYYY') AS FECHA
            FROM PRESENTACION_EXAMEN PE
            JOIN EXAMEN E ON PE.ID_EXAMEN = E.ID_EXAMEN
            WHERE PE.ID_ESTUDIANTE = ? AND PE.ESTADO = 'FINALIZADO'
            ORDER BY PE.FECHA_PRESENTACION DESC
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, estudiante.getIdEstudiante()); // <-- ¿Seguro que es ID = 2?
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                estadisticas.add(new EstadisticaExamen(
                        rs.getString("EXAMEN"),
                        rs.getDouble("CALIFICACION"),
                        rs.getString("FECHA")
                ));
            }

            tablaEstadisticas.setItems(estadisticas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void abrirVistaExamenes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/EstudianteExamenesView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            EstudianteExamenesController controlador = loader.getController();
            controlador.inicializar(estudiante);

            Stage stage = new Stage();
            stage.setTitle("Exámenes Asignados");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            Stage ventanaActual = (Stage) lblBienvenida.getScene().getWindow();
            ventanaActual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
