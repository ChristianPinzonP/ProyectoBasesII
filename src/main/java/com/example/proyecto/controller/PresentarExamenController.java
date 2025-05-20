package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;


import java.util.*;

public class PresentarExamenController {

    @FXML private Label lblTituloExamen;
    @FXML private VBox contenedorPreguntas;

    private Examen examen;
    private Estudiante estudiante;
    private int idPresentacion;
    private final Map<Integer, Object> respuestasEstudiante = new HashMap<>();

    public void inicializar(Examen examen, Estudiante estudiante) {
        this.examen = examen;
        this.estudiante = estudiante;
        lblTituloExamen.setText("Examen: " + examen.getNombre());

        // Generar la presentación del examen
        idPresentacion = PresentacionExamenDAO.registrarPresentacion(examen.getId(), estudiante.getIdEstudiante());

        List<Pregunta> preguntas = PreguntaDAO.obtenerPreguntasConOpcionesPorExamen(examen.getId());

        for (Pregunta pregunta : preguntas) {
            VBox preguntaBox = new VBox(5);
            preguntaBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");

            Label lbl = new Label(pregunta.getTexto());
            preguntaBox.getChildren().add(lbl);

            String tipoNormalizado = normalizarTipo(pregunta.getTipo());

            switch (tipoNormalizado) {
                case "opcion multiple", "verdadero/falso" -> {
                    ToggleGroup grupo = new ToggleGroup();
                    for (OpcionRespuesta op : pregunta.getOpciones()) {
                        RadioButton rb = new RadioButton(op.getTexto());
                        rb.setUserData(op.getId()); // ✅ Usa el ID_RESPUESTA real
                        rb.setToggleGroup(grupo);
                        preguntaBox.getChildren().add(rb);
                    }
                    respuestasEstudiante.put(pregunta.getId(), grupo);
                }
                case "respuesta corta" -> {
                    TextArea area = new TextArea();
                    area.setPromptText("Tu respuesta...");
                    area.setPrefRowCount(2);
                    respuestasEstudiante.put(pregunta.getId(), area);
                    preguntaBox.getChildren().add(area);
                }
                default -> {
                    Label error = new Label("❌ Tipo de pregunta no reconocido: " + pregunta.getTipo());
                    preguntaBox.getChildren().add(error);
                }
            }
            contenedorPreguntas.getChildren().add(preguntaBox);
        }
    }

    private String normalizarTipo(String tipo) {
        return tipo.toLowerCase().trim()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }

    @FXML
    private void finalizarExamen() {
        try (Connection conn = DBConnection.getConnection()) {
            for (Map.Entry<Integer, Object> entry : respuestasEstudiante.entrySet()) {
                int idPregunta = entry.getKey();
                Object control = entry.getValue();
                Integer idRespuestaSeleccionada = null;

                if (control instanceof ToggleGroup grupo) {
                    Toggle seleccion = grupo.getSelectedToggle();
                    if (seleccion != null) {
                        idRespuestaSeleccionada = (Integer) seleccion.getUserData();
                    }
                } else if (control instanceof TextArea area) {
                    String respuestaTexto = area.getText().trim();
                    if (!respuestaTexto.isEmpty()) {
                        idRespuestaSeleccionada = RespuestaEstudianteDAO.insertarRespuestaTexto(idPresentacion, idPregunta, respuestaTexto, conn);
                    }
                }

                if (idRespuestaSeleccionada != null) {
                    RespuestaEstudianteDAO.insertarRespuestaSeleccion(idPresentacion, idPregunta, idRespuestaSeleccionada, conn);
                }
            }

            // Ejecutar procedimiento para calificar
            CallableStatement stmt = conn.prepareCall("{ call CALIFICAR_EXAMEN_AUTOMATICO(?, ?) }");
            stmt.setInt(1, idPresentacion);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();

            String estado = stmt.getString(2);
            stmt.close();

            if ("OK".equals(estado)) {
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT CALIFICACION FROM PRESENTACION_EXAMEN WHERE ID_PRESENTACION = ?");
                ps.setInt(1, idPresentacion);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    double nota = rs.getDouble("CALIFICACION");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Examen finalizado");
                    alert.setHeaderText("Tu examen ha sido calificado.");
                    alert.setContentText("Tu nota es: " + nota);
                    alert.showAndWait();
                }
                rs.close();
                ps.close();
            } else {
                mostrarError("No se pudo calificar el examen. Estado: " + estado);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al finalizar el examen: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
