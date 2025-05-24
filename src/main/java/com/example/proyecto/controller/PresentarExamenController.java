package com.example.proyecto.controller;

import com.example.proyecto.dao.PresentacionExamenDAO;
import com.example.proyecto.dao.PreguntaDAO;
import com.example.proyecto.OpcionRespuesta;
import com.example.proyecto.Pregunta;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresentarExamenController {

    @FXML private Label lblTituloExamen;
    @FXML private Label lblTiempo;
    @FXML private VBox contenedorPreguntas;
    @FXML private Button btnFinalizar;

    private int idExamen;
    private int idEstudiante;
    private int tiempoLimiteSegundos;
    private Timeline temporizador;
    private Map<Integer, ToggleGroup> respuestasSeleccionadas = new HashMap<>();
    private int idPresentacion;

    public void inicializar(int idExamen, int idEstudiante, int tiempoLimiteSegundos) {
        this.idExamen = idExamen;
        this.idEstudiante = idEstudiante;
        this.tiempoLimiteSegundos = tiempoLimiteSegundos;

        // Verificar si ya existe una presentación y está finalizada
        idPresentacion = PresentacionExamenDAO.obtenerIdPresentacion(idEstudiante, idExamen);
        if (idPresentacion != -1 && PresentacionExamenDAO.examenFinalizado(idPresentacion)) {
            mostrarAlertaFX("Intento inválido", "Ya ha finalizado este examen. No puede volver a presentarlo.");
            bloquearInterfaz();
            return;
        }

        if (idPresentacion == -1) {
            idPresentacion = PresentacionExamenDAO.registrarPresentacion(idExamen, idEstudiante);
        }

        cargarPreguntas();
        iniciarTemporizador();
    }

    private void cargarPreguntas() {
        List<Pregunta> preguntas = PreguntaDAO.obtenerPreguntasConOpcionesPorExamen(idExamen);

        for (Pregunta pregunta : preguntas) {
            Label lblPregunta = new Label(pregunta.getTexto());
            lblPregunta.setStyle("-fx-font-weight: bold;");
            VBox contenedorPregunta = new VBox(lblPregunta);
            contenedorPregunta.setSpacing(5);

            ToggleGroup grupoOpciones = new ToggleGroup();
            respuestasSeleccionadas.put(pregunta.getId(), grupoOpciones);

            for (OpcionRespuesta opcion : pregunta.getOpciones()) {
                RadioButton radioButton = new RadioButton(opcion.getTexto());
                radioButton.setUserData(opcion.getId());
                radioButton.setToggleGroup(grupoOpciones);
                contenedorPregunta.getChildren().add(radioButton);
            }

            contenedorPreguntas.getChildren().add(contenedorPregunta);
        }
    }

    private void iniciarTemporizador() {
        temporizador = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoLimiteSegundos--;
            int minutos = tiempoLimiteSegundos / 60;
            int segundos = tiempoLimiteSegundos % 60;
            lblTiempo.setText(String.format("Tiempo restante: %02d:%02d", minutos, segundos));

            if (tiempoLimiteSegundos <= 0) {
                temporizador.stop();
                finalizarExamenDesdeTemporizador();
            }
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();
    }

    @FXML
    private void finalizarExamen() {
        if (temporizador != null) temporizador.stop();

        for (Map.Entry<Integer, ToggleGroup> entry : respuestasSeleccionadas.entrySet()) {
            int idPregunta = entry.getKey();
            Toggle seleccion = entry.getValue().getSelectedToggle();

            if (seleccion != null) {
                int idRespuestaSeleccionada = (int) seleccion.getUserData();
                PresentacionExamenDAO.registrarRespuesta(idPresentacion, idPregunta, idRespuestaSeleccionada);
            }
        }

        PresentacionExamenDAO.calificarAutomaticamente(idPresentacion);
        double nota = PresentacionExamenDAO.obtenerCalificacion(idPresentacion);

        bloquearInterfaz();
        mostrarAlertaFX("Examen finalizado", "Su calificación fue: " + nota);
    }

    private void finalizarExamenDesdeTemporizador() {
        Platform.runLater(() -> {
            finalizarExamen();
            mostrarAlertaFX("Tiempo finalizado", "Se ha agotado el tiempo para responder el examen.");
        });
    }

    private void bloquearInterfaz() {
        for (Node nodo : contenedorPreguntas.getChildren()) {
            nodo.setDisable(true);
        }
        btnFinalizar.setDisable(true);
        lblTiempo.setText("Examen finalizado");
    }

    private void mostrarAlertaFX(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
