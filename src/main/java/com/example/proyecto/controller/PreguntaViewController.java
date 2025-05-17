package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.PreguntaDAO;
import com.example.proyecto.dao.TemaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class PreguntaViewController {
    @FXML private TextArea txtTexto;
    @FXML private ComboBox<String> cbTipoPregunta;
    @FXML private VBox vboxOpciones;
    @FXML private TableView<Pregunta> tablaPreguntas;
    @FXML private TableColumn<Pregunta, Integer> colId, colTema;
    @FXML private TableColumn<Pregunta, String> colTexto, colTipo;
    @FXML private TableColumn<Pregunta, Double> colValorNota;
    @FXML private ComboBox<Tema> cbTema;
    @FXML private TextField txtValorNota;

    private TextField txtOpcion1, txtOpcion2, txtOpcion3, txtOpcion4;
    private CheckBox chkCorrecta1, chkCorrecta2, chkCorrecta3, chkCorrecta4;
    private TextField txtRespuestaCorta;
    private List<Tema> listaTemas;

    private Pregunta preguntaEnEdicion = null;

    @FXML
    public void initialize() {
        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));

        cbTipoPregunta.setItems(FXCollections.observableArrayList("Opción Múltiple", "Verdadero/Falso", "Respuesta Corta"));
        cbTipoPregunta.setOnAction(event -> actualizarOpcionesRespuesta());

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTexto.setCellValueFactory(new PropertyValueFactory<>("texto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("nombreTema"));
        colValorNota.setCellValueFactory(new PropertyValueFactory<>("valorNota"));

        cargarPreguntas();
    }

    @FXML
    public void editarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta para editar.", Alert.AlertType.WARNING);
            return;
        }

        preguntaEnEdicion = seleccionada;

        txtTexto.setText(seleccionada.getTexto());
        cbTipoPregunta.setValue(seleccionada.getTipo());
        txtValorNota.setText(String.valueOf(seleccionada.getValorNota()));

        for (Tema tema : listaTemas) {
            if (tema.getId() == seleccionada.getIdTema()) {
                cbTema.getSelectionModel().select(tema);
                break;
            }
        }

        actualizarOpcionesRespuesta();
        List<OpcionRespuesta> opciones = PreguntaDAO.obtenerOpcionesDePregunta(seleccionada.getId());

        if (seleccionada.getTipo().equals("Opción Múltiple")) {
            if (opciones.size() >= 4) {
                txtOpcion1.setText(opciones.get(0).getTexto()); chkCorrecta1.setSelected(opciones.get(0).isCorrecta());
                txtOpcion2.setText(opciones.get(1).getTexto()); chkCorrecta2.setSelected(opciones.get(1).isCorrecta());
                txtOpcion3.setText(opciones.get(2).getTexto()); chkCorrecta3.setSelected(opciones.get(2).isCorrecta());
                txtOpcion4.setText(opciones.get(3).getTexto()); chkCorrecta4.setSelected(opciones.get(3).isCorrecta());
            }
        } else if (seleccionada.getTipo().equals("Verdadero/Falso")) {
            chkCorrecta1.setSelected(opciones.get(0).isCorrecta());
            chkCorrecta2.setSelected(opciones.get(1).isCorrecta());
        } else if (seleccionada.getTipo().equals("Respuesta Corta")) {
            if (!opciones.isEmpty()) {
                txtRespuestaCorta.setText(opciones.get(0).getTexto());
            }
        }
    }

    @FXML
    public void eliminarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una pregunta para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar la pregunta seleccionada?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean exito = PreguntaDAO.eliminarPregunta(seleccionada.getId());
                if (exito) {
                    mostrarAlerta("Éxito", "Pregunta eliminada correctamente.", Alert.AlertType.INFORMATION);
                    cargarPreguntas();
                    limpiarFormulario();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la pregunta.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    public void guardarEdicion() {
        if (preguntaEnEdicion == null) {
            mostrarAlerta("Error", "Debe seleccionar una pregunta primero.", Alert.AlertType.WARNING);
            return;
        }

        String nuevoTexto = txtTexto.getText().trim();
        String nuevoTipo = cbTipoPregunta.getValue();
        Tema temaSeleccionado = cbTema.getValue();
        String valorNotaTexto = txtValorNota.getText().trim();

        if (nuevoTexto.isEmpty() || nuevoTipo == null || temaSeleccionado == null || valorNotaTexto.isEmpty()) {
            mostrarAlerta("Error", "Debe completar todos los campos, incluyendo el valor de la nota.", Alert.AlertType.WARNING);
            return;
        }

        double nuevoValorNota;
        try {
            nuevoValorNota = Double.parseDouble(valorNotaTexto);
            if (nuevoValorNota < 0 || nuevoValorNota > 5) {
                mostrarAlerta("Error", "El valor de la nota debe estar entre 0 y 5.", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El valor de la nota debe ser un número válido.", Alert.AlertType.WARNING);
            return;
        }

        List<OpcionRespuesta> nuevasOpciones = new ArrayList<>();

        if (nuevoTipo.equals("Opción Múltiple")) {
            agregarOpcionSiExiste(txtOpcion1, chkCorrecta1, nuevasOpciones);
            agregarOpcionSiExiste(txtOpcion2, chkCorrecta2, nuevasOpciones);
            agregarOpcionSiExiste(txtOpcion3, chkCorrecta3, nuevasOpciones);
            agregarOpcionSiExiste(txtOpcion4, chkCorrecta4, nuevasOpciones);
        } else if (nuevoTipo.equals("Verdadero/Falso")) {
            nuevasOpciones.add(new OpcionRespuesta("Verdadero", chkCorrecta1.isSelected()));
            nuevasOpciones.add(new OpcionRespuesta("Falso", chkCorrecta2.isSelected()));
        } else if (nuevoTipo.equals("Respuesta Corta")) {
            if (!txtRespuestaCorta.getText().trim().isEmpty()) {
                nuevasOpciones.add(new OpcionRespuesta(txtRespuestaCorta.getText().trim(), true));
            }
        }

        preguntaEnEdicion.setValorNota(nuevoValorNota);

        boolean exito = PreguntaDAO.actualizarPregunta(
                preguntaEnEdicion.getId(), nuevoTexto, nuevoTipo, temaSeleccionado.getId(), nuevoValorNota, nuevasOpciones);

        if (exito) {
            mostrarAlerta("Éxito", "Pregunta actualizada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
            preguntaEnEdicion = null;
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la pregunta.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void agregarPregunta() {
        String textoPregunta = txtTexto.getText().trim();
        String tipoPregunta = cbTipoPregunta.getValue();
        Tema temaSeleccionado = cbTema.getValue();
        String valorTexto = txtValorNota.getText().trim();

        if (textoPregunta.isEmpty() || tipoPregunta == null || temaSeleccionado == null || valorTexto.isEmpty()) {
            mostrarAlerta("Error", "Debe completar todos los campos, incluyendo el valor de la nota.", Alert.AlertType.WARNING);
            return;
        }

        double valorNota;
        try {
            valorNota = Double.parseDouble(valorTexto);
            if (valorNota < 0 || valorNota > 5) {
                mostrarAlerta("Error", "El valor de la nota debe estar entre 0 y 5.", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El valor de la nota debe ser un número válido.", Alert.AlertType.WARNING);
            return;
        }

        List<OpcionRespuesta> opciones = new ArrayList<>();
        if (tipoPregunta.equals("Opción Múltiple")) {
            agregarOpcionSiExiste(txtOpcion1, chkCorrecta1, opciones);
            agregarOpcionSiExiste(txtOpcion2, chkCorrecta2, opciones);
            agregarOpcionSiExiste(txtOpcion3, chkCorrecta3, opciones);
            agregarOpcionSiExiste(txtOpcion4, chkCorrecta4, opciones);
        } else if (tipoPregunta.equals("Verdadero/Falso")) {
            opciones.add(new OpcionRespuesta("Verdadero", chkCorrecta1.isSelected()));
            opciones.add(new OpcionRespuesta("Falso", chkCorrecta2.isSelected()));
        } else if (tipoPregunta.equals("Respuesta Corta")) {
            if (!txtRespuestaCorta.getText().trim().isEmpty()) {
                opciones.add(new OpcionRespuesta(txtRespuestaCorta.getText().trim(), true));
            }
        }

        Pregunta nuevaPregunta = new Pregunta(0, textoPregunta, tipoPregunta, temaSeleccionado.getId(), opciones);
        nuevaPregunta.setNombreTema(temaSeleccionado.getNombre());
        nuevaPregunta.setValorNota(valorNota);

        boolean insertada = PreguntaDAO.agregarPregunta(nuevaPregunta);

        if (insertada) {
            mostrarAlerta("Éxito", "Pregunta agregada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo agregar la pregunta.", Alert.AlertType.ERROR);
        }
    }

    private void agregarOpcionSiExiste(TextField txtOpcion, CheckBox chkCorrecta, List<OpcionRespuesta> opciones) {
        if (txtOpcion != null && chkCorrecta != null && !txtOpcion.getText().trim().isEmpty()) {
            opciones.add(new OpcionRespuesta(txtOpcion.getText().trim(), chkCorrecta.isSelected()));
        }
    }

    private void actualizarOpcionesRespuesta() {
        vboxOpciones.getChildren().clear();
        String tipoSeleccionado = cbTipoPregunta.getValue();
        if (tipoSeleccionado == null) return;

        switch (tipoSeleccionado) {
            case "Opción Múltiple":
                txtOpcion1 = new TextField(); chkCorrecta1 = new CheckBox("Correcto");
                txtOpcion2 = new TextField(); chkCorrecta2 = new CheckBox("Correcto");
                txtOpcion3 = new TextField(); chkCorrecta3 = new CheckBox("Correcto");
                txtOpcion4 = new TextField(); chkCorrecta4 = new CheckBox("Correcto");

                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion1, chkCorrecta1));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion2, chkCorrecta2));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion3, chkCorrecta3));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion4, chkCorrecta4));
                break;

            case "Verdadero/Falso":
                TextField txtVerdadero = new TextField("Verdadero"); txtVerdadero.setEditable(false);
                TextField txtFalso = new TextField("Falso"); txtFalso.setEditable(false);
                chkCorrecta1 = new CheckBox("Correcto");
                chkCorrecta2 = new CheckBox("Correcto");
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtVerdadero, chkCorrecta1));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtFalso, chkCorrecta2));
                break;

            case "Respuesta Corta":
                txtRespuestaCorta = new TextField();
                txtRespuestaCorta.setPromptText("Ingrese la respuesta correcta");
                vboxOpciones.getChildren().add(txtRespuestaCorta);
                break;
        }
    }

    private VBox crearFilaOpcion(TextField txtOpcion, CheckBox chkCorrecta) {
        VBox fila = new VBox(5);
        fila.getChildren().addAll(txtOpcion, chkCorrecta);
        return fila;
    }

    private void limpiarFormulario() {
        txtTexto.clear();
        txtValorNota.clear();
        cbTipoPregunta.getSelectionModel().clearSelection();
        cbTema.getSelectionModel().clearSelection();
        vboxOpciones.getChildren().clear();
        preguntaEnEdicion = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void cargarPreguntas() {
        ObservableList<Pregunta> listaPreguntas = FXCollections.observableArrayList(PreguntaDAO.obtenerTodasLasPreguntas());
        tablaPreguntas.setItems(listaPreguntas);
    }
}
