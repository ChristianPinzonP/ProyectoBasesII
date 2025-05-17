package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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
                case "opcion multiple" -> {
                    ToggleGroup grupo = new ToggleGroup();
                    for (OpcionRespuesta op : pregunta.getOpciones()) {
                        RadioButton rb = new RadioButton(op.getTexto());
                        rb.setUserData(op.getId());
                        rb.setToggleGroup(grupo);
                        preguntaBox.getChildren().add(rb);
                    }
                    respuestasEstudiante.put(pregunta.getId(), grupo);
                }
                case "verdadero/falso" -> {
                    ToggleGroup grupo = new ToggleGroup();
                    for (OpcionRespuesta op : pregunta.getOpciones()) {
                        RadioButton rb = new RadioButton(op.getTexto());
                        rb.setUserData(op.getId());
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
    public void finalizarExamen() {
        int correctas = 0;
        int total = respuestasEstudiante.size();

        for (Map.Entry<Integer, Object> entry : respuestasEstudiante.entrySet()) {
            int idPregunta = entry.getKey();
            Object control = entry.getValue();
            int idRespuesta = -1;
            String textoLibre = null;

            if (control instanceof ToggleGroup group) {
                Toggle selected = group.getSelectedToggle();
                if (selected != null) {
                    idRespuesta = (int) selected.getUserData();
                }
            } else if (control instanceof TextArea area) {
                textoLibre = area.getText().trim();
            }

            boolean esCorrecto = RespuestaEstudianteDAO.guardarRespuesta(
                    idPresentacion, idPregunta, idRespuesta, textoLibre
            );
            if (esCorrecto) correctas++;
        }

        // Calificar automáticamente vía PL/SQL
        PresentacionExamenDAO.calificarAutomaticamente(idPresentacion);
        double nota = PresentacionExamenDAO.obtenerCalificacion(idPresentacion);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Examen finalizado");
        alert.setHeaderText(null);
        alert.setContentText("Tu examen ha sido enviado.\nCalificación: " + nota + " puntos");
        alert.showAndWait();
    }

}
