package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.PreguntaDAO;
import com.example.proyecto.dao.TemaDAO;
import com.example.proyecto.dao.DocenteDAO;
import com.example.proyecto.sesion.SesionUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

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
    @FXML private TableColumn<Pregunta, Boolean> colEsPublica;
    @FXML private ComboBox<Tema> cbTema;
    @FXML private ComboBox<Pregunta> cbPreguntaPadre;
    @FXML private TextField txtValorNota;
    @FXML private CheckBox chkEsPublica;
    @FXML private CheckBox chkEsPreguntaPadre;

    private TextField txtOpcion1, txtOpcion2, txtOpcion3, txtOpcion4;
    private CheckBox chkCorrecta1, chkCorrecta2, chkCorrecta3, chkCorrecta4;
    private TextField txtRespuestaCorta;
    private List<Tema> listaTemas;
    private Pregunta preguntaEnEdicion = null;
    private Docente docenteActual;

    @FXML private TableView<Pregunta> tablaHijas;
    @FXML private TableColumn<Pregunta, Integer> colHijaId;
    @FXML private TableColumn<Pregunta, String> colHijaTexto;

    @FXML
    public void initialize() {
        docenteActual = SesionUsuario.getDocenteActual();
        if (docenteActual == null) {
            docenteActual = DocenteDAO.obtenerDocentePorId(1);
            SesionUsuario.setDocenteActual(docenteActual);
        }

        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));

        cbTipoPregunta.setItems(FXCollections.observableArrayList("Opción Múltiple", "Verdadero/Falso", "Respuesta Corta"));
        cbTipoPregunta.setOnAction(event -> actualizarOpcionesRespuesta());

        cbPreguntaPadre.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pregunta pregunta) {
                return pregunta == null ? "" : "[" + pregunta.getId() + "] " + pregunta.getTexto();
            }

            @Override
            public Pregunta fromString(String string) {
                return null;
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTexto.setCellValueFactory(new PropertyValueFactory<>("texto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("nombreTema"));
        colValorNota.setCellValueFactory(new PropertyValueFactory<>("valorNota"));
        colEsPublica.setCellValueFactory(new PropertyValueFactory<>("esPublica"));
        colHijaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colHijaTexto.setCellValueFactory(new PropertyValueFactory<>("texto"));

        colEsPublica.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean esPublica, boolean empty) {
                super.updateItem(esPublica, empty);
                setText(empty || esPublica == null ? null : (esPublica ? "Pública" : "Privada"));
            }
        });

        chkEsPreguntaPadre.setOnAction(e -> manejarCambioTipoPreguntaCompuesta());
        cargarPreguntas();
        actualizarComboPadres();
    }
    @FXML
    private void manejarCambioTipoPreguntaCompuesta() {
        boolean esPadre = chkEsPreguntaPadre.isSelected();
        vboxOpciones.setDisable(esPadre);
        cbPreguntaPadre.setDisable(esPadre);
        cbTipoPregunta.setDisable(esPadre);
        if (esPadre) {
            vboxOpciones.getChildren().clear();
            cbPreguntaPadre.getSelectionModel().clearSelection();
            cbTipoPregunta.getSelectionModel().clearSelection();
        } else {
            cbTipoPregunta.setDisable(false);
            actualizarOpcionesRespuesta();
        }
    }
    @FXML
    public void editarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta para editar.", Alert.AlertType.WARNING);
            return;
        }
        if (seleccionada.isEsPublica() && seleccionada.getIdDocente() != docenteActual.getIdDocente()) {
            mostrarAlerta("Error", "No puede editar esta pregunta.", Alert.AlertType.WARNING);
            return;
        }

        preguntaEnEdicion = seleccionada;
        boolean esPadre = "Compuesta".equalsIgnoreCase(seleccionada.getTipo()) || !PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId()).isEmpty();

        chkEsPreguntaPadre.setSelected(esPadre);
        chkEsPreguntaPadre.setDisable(true);
        txtTexto.setText(seleccionada.getTexto());
        cbTipoPregunta.setValue(seleccionada.getTipo());
        cbTipoPregunta.setDisable(esPadre);
        txtValorNota.setText(String.valueOf(seleccionada.getValorNota()));
        chkEsPublica.setSelected(seleccionada.isEsPublica());
        cbTema.getSelectionModel().select(listaTemas.stream().filter(t -> t.getId() == seleccionada.getIdTema()).findFirst().orElse(null));
        actualizarComboPadres();
        cbPreguntaPadre.getSelectionModel().select(seleccionada.getIdPreguntaPadre() == null ? null :
                cbPreguntaPadre.getItems().stream().filter(p -> p.getId() == seleccionada.getIdPreguntaPadre()).findFirst().orElse(null));

        if (esPadre) {
            vboxOpciones.getChildren().clear();
            tablaHijas.setItems(FXCollections.observableArrayList(PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId())));
        } else {
            actualizarOpcionesRespuesta();
            List<OpcionRespuesta> opciones = PreguntaDAO.obtenerOpcionesDePregunta(seleccionada.getId());
            switch (seleccionada.getTipo()) {
                case "Opción Múltiple" -> {
                    if (opciones.size() >= 4) {
                        txtOpcion1.setText(opciones.get(0).getTexto()); chkCorrecta1.setSelected(opciones.get(0).isCorrecta());
                        txtOpcion2.setText(opciones.get(1).getTexto()); chkCorrecta2.setSelected(opciones.get(1).isCorrecta());
                        txtOpcion3.setText(opciones.get(2).getTexto()); chkCorrecta3.setSelected(opciones.get(2).isCorrecta());
                        txtOpcion4.setText(opciones.get(3).getTexto()); chkCorrecta4.setSelected(opciones.get(3).isCorrecta());
                    }
                }
                case "Verdadero/Falso" -> {
                    chkCorrecta1.setSelected(opciones.get(0).isCorrecta());
                    chkCorrecta2.setSelected(opciones.get(1).isCorrecta());
                }
                case "Respuesta Corta" -> {
                    if (!opciones.isEmpty()) txtRespuestaCorta.setText(opciones.get(0).getTexto());
                }
            }
            tablaHijas.getItems().clear();
        }
    }

    private void actualizarComboPadres() {
        cbPreguntaPadre.setItems(FXCollections.observableArrayList(
                PreguntaDAO.obtenerPreguntasVisiblesParaDocente(docenteActual.getIdDocente())
        ));
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void actualizarOpcionesRespuesta() {
        if (chkEsPreguntaPadre.isSelected()) return;
        vboxOpciones.getChildren().clear();
        String tipo = cbTipoPregunta.getValue();
        if (tipo == null) return;

        switch (tipo) {
            case "Opción Múltiple" -> {
                txtOpcion1 = new TextField(); chkCorrecta1 = new CheckBox("Correcta");
                txtOpcion2 = new TextField(); chkCorrecta2 = new CheckBox("Correcta");
                txtOpcion3 = new TextField(); chkCorrecta3 = new CheckBox("Correcta");
                txtOpcion4 = new TextField(); chkCorrecta4 = new CheckBox("Correcta");
                vboxOpciones.getChildren().addAll(
                        crearFilaOpcion(txtOpcion1, chkCorrecta1),
                        crearFilaOpcion(txtOpcion2, chkCorrecta2),
                        crearFilaOpcion(txtOpcion3, chkCorrecta3),
                        crearFilaOpcion(txtOpcion4, chkCorrecta4)
                );
            }
            case "Verdadero/Falso" -> {
                TextField txtV = new TextField("Verdadero"); txtV.setEditable(false);
                TextField txtF = new TextField("Falso"); txtF.setEditable(false);
                chkCorrecta1 = new CheckBox("Correcta");
                chkCorrecta2 = new CheckBox("Correcta");
                vboxOpciones.getChildren().addAll(
                        crearFilaOpcion(txtV, chkCorrecta1),
                        crearFilaOpcion(txtF, chkCorrecta2)
                );
            }
            case "Respuesta Corta" -> {
                txtRespuestaCorta = new TextField();
                txtRespuestaCorta.setPromptText("Respuesta");
                vboxOpciones.getChildren().add(txtRespuestaCorta);
            }
        }
    }

    private VBox crearFilaOpcion(TextField txt, CheckBox chk) {
        VBox fila = new VBox(5);
        fila.getChildren().addAll(txt, chk);
        return fila;
    }
    @FXML
    public void quitarPreguntaHija() {
        Pregunta seleccionada = tablaHijas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            boolean exito = PreguntaDAO.quitarRelacionPadreHija(seleccionada.getId());
            if (exito) {
                mostrarAlerta("Éxito", "Pregunta hija desvinculada.", Alert.AlertType.INFORMATION);
                tablaHijas.getItems().remove(seleccionada);
                cargarPreguntas(); // Opcional: refrescar la tabla general
            } else {
                mostrarAlerta("Error", "No se pudo desvincular.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void agregarPregunta() {
        if (!validarCampos()) return;

        Pregunta nueva = new Pregunta();
        nueva.setTexto(txtTexto.getText().trim());
        nueva.setTipo(cbTipoPregunta.getValue());
        nueva.setIdTema(cbTema.getValue().getId());
        nueva.setValorNota(Double.parseDouble(txtValorNota.getText()));
        nueva.setEsPublica(chkEsPublica.isSelected());
        nueva.setIdDocente(docenteActual.getIdDocente());

        if (chkEsPreguntaPadre.isSelected()) {
            nueva.setTipo("Compuesta");
            nueva.setIdPreguntaPadre(null);
        } else {
            Pregunta padre = cbPreguntaPadre.getValue();
            nueva.setIdPreguntaPadre(padre != null ? padre.getId() : null);
        }

        boolean creado = PreguntaDAO.agregarPreguntaConOpciones(nueva, obtenerOpcionesDesdeFormulario());

        if (creado) {
            mostrarAlerta("Éxito", "Pregunta creada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo crear la pregunta.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void guardarEdicion() {
        if (preguntaEnEdicion == null) {
            mostrarAlerta("Advertencia", "No hay pregunta en edición.", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        preguntaEnEdicion.setTexto(txtTexto.getText().trim());
        preguntaEnEdicion.setTipo(cbTipoPregunta.getValue());
        preguntaEnEdicion.setIdTema(cbTema.getValue().getId());
        preguntaEnEdicion.setValorNota(Double.parseDouble(txtValorNota.getText()));
        preguntaEnEdicion.setEsPublica(chkEsPublica.isSelected());

        if (!chkEsPreguntaPadre.isSelected()) {
            Pregunta padre = cbPreguntaPadre.getValue();
            preguntaEnEdicion.setIdPreguntaPadre(padre != null ? padre.getId() : null);
        }

        boolean actualizado = PreguntaDAO.actualizarPregunta(
                preguntaEnEdicion.getId(),
                preguntaEnEdicion.getTexto(),
                preguntaEnEdicion.getTipo(),
                preguntaEnEdicion.getIdTema(),
                preguntaEnEdicion.getValorNota(),
                preguntaEnEdicion.isEsPublica(),
                preguntaEnEdicion.getIdPreguntaPadre(),
                obtenerOpcionesDesdeFormulario()
        );


        if (actualizado) {
            mostrarAlerta("Éxito", "Pregunta actualizada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la pregunta.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una pregunta para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas eliminar esta pregunta?", ButtonType.YES, ButtonType.NO);
        if (confirmar.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if (PreguntaDAO.eliminarPregunta(seleccionada.getId())) {
                mostrarAlerta("Éxito", "Pregunta eliminada correctamente.", Alert.AlertType.INFORMATION);
                cargarPreguntas();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la pregunta.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validarCampos() {
        if (txtTexto.getText().trim().isEmpty() || cbTema.getValue() == null ||
                (!chkEsPreguntaPadre.isSelected() && cbTipoPregunta.getValue() == null)) {
            mostrarAlerta("Error", "Todos los campos obligatorios deben estar llenos.", Alert.AlertType.WARNING);
            return false;
        }

        try {
            double valor = Double.parseDouble(txtValorNota.getText().trim());
            if (valor < 0 || valor > 5) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El valor de la nota debe ser un número entre 0 y 5.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private List<OpcionRespuesta> obtenerOpcionesDesdeFormulario() {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        String tipo = cbTipoPregunta.getValue();

        switch (tipo) {
            case "Opción Múltiple" -> {
                opciones.add(new OpcionRespuesta(txtOpcion1.getText(), chkCorrecta1.isSelected()));
                opciones.add(new OpcionRespuesta(txtOpcion2.getText(), chkCorrecta2.isSelected()));
                opciones.add(new OpcionRespuesta(txtOpcion3.getText(), chkCorrecta3.isSelected()));
                opciones.add(new OpcionRespuesta(txtOpcion4.getText(), chkCorrecta4.isSelected()));
            }
            case "Verdadero/Falso" -> {
                opciones.add(new OpcionRespuesta("Verdadero", chkCorrecta1.isSelected()));
                opciones.add(new OpcionRespuesta("Falso", chkCorrecta2.isSelected()));
            }
            case "Respuesta Corta" -> {
                opciones.add(new OpcionRespuesta(txtRespuestaCorta.getText(), true));
            }
        }

        return opciones;
    }

    private void limpiarFormulario() {
        txtTexto.clear();
        txtValorNota.clear();
        cbTipoPregunta.getSelectionModel().clearSelection();
        cbTema.getSelectionModel().clearSelection();
        cbPreguntaPadre.getSelectionModel().clearSelection();
        chkEsPublica.setSelected(false);
        chkEsPreguntaPadre.setSelected(false);
        chkEsPreguntaPadre.setDisable(false);
        cbTipoPregunta.setDisable(false);
        cbPreguntaPadre.setDisable(false);
        vboxOpciones.getChildren().clear();
        tablaHijas.getItems().clear();
        actualizarComboPadres();
        preguntaEnEdicion = null;
    }
    @FXML
    public void cargarPreguntas() {
        ObservableList<Pregunta> lista = FXCollections.observableArrayList(
                PreguntaDAO.obtenerPreguntasVisiblesParaDocente(docenteActual.getIdDocente())
        );
        tablaPreguntas.setItems(lista);
    }
}

