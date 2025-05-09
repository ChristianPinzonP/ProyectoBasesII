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
    @FXML private ComboBox<Tema> cbTema;

    private TextField txtOpcion1, txtOpcion2, txtOpcion3, txtOpcion4;
    private CheckBox chkCorrecta1, chkCorrecta2, chkCorrecta3, chkCorrecta4;
    private TextField txtRespuestaCorta;
    private List<Tema> listaTemas;

    @FXML
    public void initialize() {
        try {
            listaTemas = TemaDAO.obtenerTemas();

            if (listaTemas.isEmpty()) {
                System.out.println("⚠️ No se cargaron temas. Verifica conexión o datos.");
            } else {
                System.out.println("✅ Temas cargados: " + listaTemas.size());
            }

            ObservableList<Tema> temas = FXCollections.observableArrayList(listaTemas);
            cbTema.setItems(temas);

        } catch (Exception e) {
            System.out.println("❌ Error al inicializar el ComboBox de temas:");
            e.printStackTrace();
        }

        cbTipoPregunta.setItems(FXCollections.observableArrayList("Opción Múltiple", "Verdadero/Falso", "Respuesta Corta"));
        cbTipoPregunta.setOnAction(event -> actualizarOpcionesRespuesta());

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTexto.setCellValueFactory(new PropertyValueFactory<>("texto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("nombreTema"));

        cargarPreguntas();
        inicializarOpciones();
    }
    private void inicializarOpciones() {
        txtOpcion1 = new TextField("Opción 1");
        chkCorrecta1 = new CheckBox("Correcto");

        txtOpcion2 = new TextField("Opción 2");
        chkCorrecta2 = new CheckBox("Correcto");

        txtOpcion3 = new TextField("Opción 3");
        chkCorrecta3 = new CheckBox("Correcto");

        txtOpcion4 = new TextField("Opción 4");
        chkCorrecta4 = new CheckBox("Correcto");

        txtRespuestaCorta = new TextField();
        txtRespuestaCorta.setPromptText("Escriba la respuesta correcta");

        actualizarOpcionesRespuesta();
    }

    @FXML
    public void agregarPregunta() {
        String textoPregunta = txtTexto.getText().trim();
        String tipoPregunta = cbTipoPregunta.getValue();
        int idTema;

        Tema temaSeleccionado = cbTema.getValue();
        if (temaSeleccionado != null) {
            idTema = temaSeleccionado.getId();
        } else {
            mostrarAlerta("Error", "Debe seleccionar un tema.", Alert.AlertType.WARNING);
            return;
        }

        if (textoPregunta.isEmpty() || tipoPregunta == null) {
            mostrarAlerta("Error", "Debe completar todos los campos.", Alert.AlertType.WARNING);
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

        System.out.println("Número de opciones antes de enviar: " + opciones.size());

        Pregunta nuevaPregunta = new Pregunta(
                0,
                textoPregunta,
                tipoPregunta,
                idTema,
                opciones
        );

        if (PreguntaDAO.agregarPregunta(nuevaPregunta)) {
            mostrarAlerta("Éxito", "Pregunta agregada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
        }
    }


    private void agregarOpcionSiExiste(TextField txtOpcion, CheckBox chkCorrecta, List<OpcionRespuesta> opciones) {
        if (txtOpcion != null && chkCorrecta != null && !txtOpcion.getText().trim().isEmpty()) {
            opciones.add(new OpcionRespuesta(txtOpcion.getText().trim(), chkCorrecta.isSelected()));
            System.out.println("Opción agregada: " + txtOpcion.getText().trim() + " - Correcta: " + chkCorrecta.isSelected());
        }
    }

    private void actualizarOpcionesRespuesta() {
        vboxOpciones.getChildren().clear(); // 🛠️ Limpia todas las opciones previas

        String tipoSeleccionado = cbTipoPregunta.getValue();
        if (tipoSeleccionado == null) return;

        switch (tipoSeleccionado) {
            case "Opción Múltiple":
                txtOpcion1 = new TextField();
                chkCorrecta1 = new CheckBox("Correcto");

                txtOpcion2 = new TextField();
                chkCorrecta2 = new CheckBox("Correcto");

                txtOpcion3 = new TextField();
                chkCorrecta3 = new CheckBox("Correcto");

                txtOpcion4 = new TextField();
                chkCorrecta4 = new CheckBox("Correcto");

                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion1, chkCorrecta1));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion2, chkCorrecta2));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion3, chkCorrecta3));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtOpcion4, chkCorrecta4));
                break;

            case "Verdadero/Falso":
                TextField txtVerdadero = new TextField("Verdadero");
                txtVerdadero.setEditable(false);
                CheckBox chkVerdadero = new CheckBox("Correcto");

                TextField txtFalso = new TextField("Falso");
                txtFalso.setEditable(false);
                CheckBox chkFalso = new CheckBox("Correcto");

                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtVerdadero, chkVerdadero));
                vboxOpciones.getChildren().addAll(crearFilaOpcion(txtFalso, chkFalso));
                break;

            case "Respuesta Corta":
                txtRespuestaCorta = new TextField();
                txtRespuestaCorta.setPromptText("Ingrese la respuesta correcta");

                vboxOpciones.getChildren().add(txtRespuestaCorta);
                break;
        }

        System.out.println("Opciones de respuesta actualizadas para: " + tipoSeleccionado);
    }

    @FXML
    public void editarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta para editar.", Alert.AlertType.WARNING);
            return;
        }

        // Cargar los datos en los campos de texto y el ComboBox
        txtTexto.setText(seleccionada.getTexto());
        cbTipoPregunta.setValue(seleccionada.getTipo());

        // Seleccionar el tema en el ComboBox por nombre
        for (Tema tema : listaTemas) {
            if (tema.getId() == seleccionada.getIdTema()) {
                cbTema.getSelectionModel().select(tema);
                break;
            }
        }

        // Actualizar las opciones de respuesta según el tipo de pregunta
        actualizarOpcionesRespuesta();

        // Limpiar opciones previas en la interfaz
        vboxOpciones.getChildren().clear();

        // Cargar opciones de respuesta desde la base de datos
        List<OpcionRespuesta> opciones = PreguntaDAO.obtenerOpcionesDePregunta(seleccionada.getId());

        if (seleccionada.getTipo().equals("Opción Múltiple")) {
            for (OpcionRespuesta opcion : opciones) {
                TextField txtOpcion = new TextField(opcion.getTexto());
                CheckBox chkCorrecta = new CheckBox("Correcto");
                chkCorrecta.setSelected(opcion.isCorrecta());

                VBox filaOpcion = new VBox(5);
                filaOpcion.getChildren().addAll(txtOpcion, chkCorrecta);
                vboxOpciones.getChildren().add(filaOpcion);
            }
        } else if (seleccionada.getTipo().equals("Verdadero/Falso")) {
            for (OpcionRespuesta opcion : opciones) {
                TextField txtOpcion = new TextField(opcion.getTexto());
                txtOpcion.setEditable(false);
                CheckBox chkCorrecta = new CheckBox("Correcto");
                chkCorrecta.setSelected(opcion.isCorrecta());

                VBox filaOpcion = new VBox(5);
                filaOpcion.getChildren().addAll(txtOpcion, chkCorrecta);
                vboxOpciones.getChildren().add(filaOpcion);
            }
        } else if (seleccionada.getTipo().equals("Respuesta Corta")) {
            if (!opciones.isEmpty()) {
                TextField txtRespuestaCorta = new TextField(opciones.get(0).getTexto());
                vboxOpciones.getChildren().add(txtRespuestaCorta);
            } else {
                TextField txtRespuestaCorta = new TextField();
                vboxOpciones.getChildren().add(txtRespuestaCorta);
            }
        }

        System.out.println("Edición iniciada para la pregunta ID: " + seleccionada.getId());
    }


    @FXML
    public void eliminarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta para eliminar.", Alert.AlertType.WARNING);
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
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la pregunta.", Alert.AlertType.ERROR);
                }
            }
        });
    }


    @FXML
    public void guardarEdicion() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta para guardar los cambios.", Alert.AlertType.WARNING);
            return;
        }

        String nuevoTexto = txtTexto.getText().trim();
        String nuevoTipo = cbTipoPregunta.getValue();
        int nuevoIdTema;

        try {
            // Obtener el índice seleccionado en el ComboBox
            int indiceSeleccionado = cbTema.getSelectionModel().getSelectedIndex();
            if (indiceSeleccionado >= 0) {
                // Obtener el ID del tema correspondiente al nombre seleccionado
                nuevoIdTema = listaTemas.get(indiceSeleccionado).getId();
            } else {
                // Si no hay selección, mantener el tema actual
                nuevoIdTema = seleccionada.getIdTema();
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al obtener el tema seleccionado.", Alert.AlertType.WARNING);
            return;
        }

        if (nuevoTexto.isEmpty() || nuevoTipo == null) {
            mostrarAlerta("Error", "Debe completar todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // Obtener las opciones de respuesta actualizadas
        List<OpcionRespuesta> nuevasOpciones = new ArrayList<>();

        try {
            if (nuevoTipo.equals("Opción Múltiple")) {
                // Recorrer los componentes del VBox que contiene las opciones
                for (javafx.scene.Node nodo : vboxOpciones.getChildren()) {
                    if (nodo instanceof VBox) { // Cambiado de HBox a VBox según tu implementación
                        VBox fila = (VBox) nodo;
                        if (fila.getChildren().size() >= 2) {
                            TextField txtOpcion = (TextField) fila.getChildren().get(0);
                            CheckBox chkCorrecta = (CheckBox) fila.getChildren().get(1);

                            if (!txtOpcion.getText().trim().isEmpty()) {
                                nuevasOpciones.add(new OpcionRespuesta(txtOpcion.getText().trim(), chkCorrecta.isSelected()));
                            }
                        }
                    }
                }
            } else if (nuevoTipo.equals("Verdadero/Falso")) {
                // Procesar las opciones para Verdadero/Falso
                for (javafx.scene.Node nodo : vboxOpciones.getChildren()) {
                    if (nodo instanceof VBox) {
                        VBox fila = (VBox) nodo;
                        if (fila.getChildren().size() >= 2) {
                            TextField txtOpcion = (TextField) fila.getChildren().get(0);
                            CheckBox chkCorrecta = (CheckBox) fila.getChildren().get(1);
                            nuevasOpciones.add(new OpcionRespuesta(txtOpcion.getText(), chkCorrecta.isSelected()));
                        }
                    }
                }
            } else if (nuevoTipo.equals("Respuesta Corta")) {
                // Para respuesta corta, solo hay un TextField
                if (vboxOpciones.getChildren().size() > 0) {
                    TextField txtRespuestaCorta = (TextField) vboxOpciones.getChildren().get(0);
                    if (txtRespuestaCorta != null && !txtRespuestaCorta.getText().trim().isEmpty()) {
                        nuevasOpciones.add(new OpcionRespuesta(txtRespuestaCorta.getText().trim(), true));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al procesar opciones: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al procesar las opciones de respuesta.", Alert.AlertType.ERROR);
            return;
        }

        System.out.println("Actualizando pregunta ID: " + seleccionada.getId());
        System.out.println("Nuevo texto: " + nuevoTexto);
        System.out.println("Nuevo tipo: " + nuevoTipo);
        System.out.println("Nuevo ID Tema: " + nuevoIdTema);
        System.out.println("Nuevas opciones: " + nuevasOpciones.size());

        // Llamar a la base de datos para actualizar la pregunta
        boolean exito = PreguntaDAO.actualizarPregunta(seleccionada.getId(), nuevoTexto, nuevoTipo, nuevoIdTema, nuevasOpciones);

        if (exito) {
            mostrarAlerta("Éxito", "Pregunta actualizada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la pregunta.", Alert.AlertType.ERROR);
        }
    }



    private VBox crearFilaOpcion(TextField txtOpcion, CheckBox chkCorrecta) {
        VBox fila = new VBox(5);
        fila.getChildren().addAll(txtOpcion, chkCorrecta);
        return fila;
    }

    private void limpiarFormulario() {
        txtTexto.clear();
        cbTipoPregunta.getSelectionModel().clearSelection();
        vboxOpciones.getChildren().clear();
        System.out.println("Formulario limpiado.");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void cargarPreguntas() {
        ObservableList<Pregunta> listaPreguntas = FXCollections.observableArrayList(PreguntaDAO.obtenerTodasLasPreguntas());
        tablaPreguntas.setItems(listaPreguntas);
        System.out.println("Preguntas cargadas en la tabla: " + listaPreguntas.size());
    }
}
