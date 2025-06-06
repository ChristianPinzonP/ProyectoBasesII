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
        this.tiempoLimiteSegundos = tiempoLimiteSegundos * 60; // minutos a segundos

        // VALIDACIÓN CORRECTA DE INTENTOS
        int intentosHechos = PresentacionExamenDAO.contarIntentosRealizados(idEstudiante, idExamen);
        int intentosPermitidos = PresentacionExamenDAO.obtenerIntentosPermitidos(idExamen);

        if (intentosHechos >= intentosPermitidos) {
            mostrarAlertaFX("Límite de intentos", "Ya has alcanzado el número máximo de intentos permitidos para este examen.");
            bloquearInterfaz();
            return;
        }

        // Registrar el nuevo intento
        this.idPresentacion = PresentacionExamenDAO.registrarPresentacion(idExamen, idEstudiante);

        cargarPreguntas();
        iniciarTemporizador();
    }


    private void cargarPreguntas() {
        List<Pregunta> preguntas = PreguntaDAO.obtenerPreguntasConOpcionesPorExamen(idExamen);

        for (Pregunta pregunta : preguntas) {
            VBox contenedorPregunta = crearBloquePregunta(pregunta);
            contenedorPreguntas.getChildren().add(contenedorPregunta);

            // Mostrar preguntas hijas si existen
            if (pregunta.getPreguntasHijas() != null && !pregunta.getPreguntasHijas().isEmpty()) {
                for (Pregunta hija : pregunta.getPreguntasHijas()) {
                    VBox contenedorHija = crearBloquePregunta(hija);
                    contenedorHija.setStyle("-fx-padding: 0 0 0 20;"); // Sangría para distinguir visualmente
                    contenedorPreguntas.getChildren().add(contenedorHija);
                }
            }
        }
    }

    private VBox crearBloquePregunta(Pregunta pregunta) {
        Label lblPregunta = new Label(pregunta.getTexto());
        lblPregunta.setStyle("-fx-font-weight: bold;");
        VBox contenedor = new VBox(lblPregunta);
        contenedor.setSpacing(5);

        ToggleGroup grupoOpciones = new ToggleGroup();
        respuestasSeleccionadas.put(pregunta.getId(), grupoOpciones);

        for (OpcionRespuesta opcion : pregunta.getOpciones()) {
            RadioButton rb = new RadioButton(opcion.getTexto());
            rb.setUserData(opcion.getId());
            rb.setToggleGroup(grupoOpciones);
            contenedor.getChildren().add(rb);
        }

        return contenedor;
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

        imprimirNotasHijasAjustadas();  // <- AQUÍ

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
    private void imprimirNotasHijasAjustadas() {
        List<Pregunta> preguntas = PreguntaDAO.obtenerPreguntasConOpcionesPorExamen(idExamen);

        for (Pregunta padre : preguntas) {
            if (padre.getTipo().equalsIgnoreCase("Compuesta") && padre.getPreguntasHijas() != null) {
                double notaPadre = padre.getValorNota();
                double sumaHijas = padre.getPreguntasHijas().stream().mapToDouble(Pregunta::getValorNota).sum();

                for (Pregunta hija : padre.getPreguntasHijas()) {
                    double notaOriginal = hija.getValorNota();
                    double notaAjustada = notaOriginal;

                    if (sumaHijas > notaPadre) {
                        notaAjustada = (notaOriginal / sumaHijas) * notaPadre;
                    }

                    System.out.printf("Pregunta hija ID: %d | Nota original: %.2f | Nota ajustada: %.2f%n",
                            hija.getId(), notaOriginal, notaAjustada);
                }
            }
        }
    }

}
