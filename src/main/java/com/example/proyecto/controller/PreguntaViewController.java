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
        //Obtener el docente actual - Esto viene del login
        docenteActual = SesionUsuario.getDocenteActual();
        if (docenteActual == null) {
            docenteActual = DocenteDAO.obtenerDocentePorId(1); // Ajusta según tu caso
            SesionUsuario.setDocenteActual(docenteActual);
        }

        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));

        cbTipoPregunta.setItems(FXCollections.observableArrayList("Opción Múltiple", "Verdadero/Falso", "Respuesta Corta"));
        cbTipoPregunta.setOnAction(event -> actualizarOpcionesRespuesta());
        cbPreguntaPadre.setItems(FXCollections.observableArrayList(PreguntaDAO.obtenerPreguntasCandidatasAPadre(docenteActual.getIdDocente())));

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

        // Personalizar la columna de visibilidad
        colEsPublica.setCellFactory(tc -> new TableCell<Pregunta, Boolean>() {
            @Override
            protected void updateItem(Boolean esPublica, boolean empty) {
                super.updateItem(esPublica, empty);
                if (empty || esPublica == null) {
                    setText(null);
                } else {
                    setText(esPublica ? "Pública" : "Privada");
                }
            }
        });
        chkEsPreguntaPadre.setOnAction(e -> manejarCambioTipoPreguntaCompuesta());

        cargarPreguntas();
    }

    @FXML
    private void manejarCambioTipoPreguntaCompuesta() {
        boolean esPadre = chkEsPreguntaPadre.isSelected();

        // Ocultar o mostrar las opciones
        vboxOpciones.setDisable(esPadre);
        cbPreguntaPadre.setDisable(esPadre);
        cbTipoPregunta.setDisable(esPadre);

        if (esPadre) {
            vboxOpciones.getChildren().clear(); // Limpia cualquier opción de respuesta cargada
            cbPreguntaPadre.getSelectionModel().clearSelection(); // Limpia selección de padre
            cbTipoPregunta.getSelectionModel().clearSelection(); // limpiar tipo de pregunta
        } else {
            cbTipoPregunta.setDisable(false);
            actualizarOpcionesRespuesta(); // Vuelve a mostrar según tipo
        }
    }

    // Método para refrescar el ComboBox cuando sea necesario:
    private void actualizarComboBoxPreguntaPadre() {
        List<Pregunta> preguntasCandidatas = PreguntaDAO.obtenerPreguntasCandidatasAPadre(docenteActual.getIdDocente());
        cbPreguntaPadre.setItems(FXCollections.observableArrayList(preguntasCandidatas));
    }

    @FXML
    public void editarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una pregunta para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (seleccionada.isEsPublica() && seleccionada.getIdDocente() != docenteActual.getIdDocente()) {
            mostrarAlerta("Error", "No puede editar esta pregunta porque pertenece a otro docente.", Alert.AlertType.WARNING);
            return;
        }

        preguntaEnEdicion = seleccionada;

        // Verificar si es pregunta hija (tiene padre asignado)
        boolean esPreguntaHija = seleccionada.getIdPreguntaPadre() != null && seleccionada.getIdPreguntaPadre() > 0;

        // Verificar si es pregunta padre (tipo compuesta O tiene hijas)
        boolean esTipoCompuesta = "Compuesta".equalsIgnoreCase(seleccionada.getTipo());
        boolean tieneHijas = !PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId()).isEmpty();
        boolean esPreguntaPadre = esTipoCompuesta || tieneHijas;

        // Debug mejorado
        System.out.println("=== EDITANDO PREGUNTA ===");
        System.out.println("ID: " + seleccionada.getId());
        System.out.println("Tipo: " + seleccionada.getTipo());
        System.out.println("ID Padre: " + seleccionada.getIdPreguntaPadre());
        System.out.println("Es pregunta hija: " + esPreguntaHija);
        System.out.println("Es pregunta padre: " + esPreguntaPadre);
        System.out.println("========================");

        // Configurar checkbox de pregunta padre
        chkEsPreguntaPadre.setSelected(esPreguntaPadre);
        chkEsPreguntaPadre.setDisable(true); // Siempre desactivado al editar

        // Llenar campos básicos
        txtTexto.setText(seleccionada.getTexto());
        cbTipoPregunta.setValue(seleccionada.getTipo());
        txtValorNota.setText(String.valueOf(seleccionada.getValorNota()));
        chkEsPublica.setSelected(seleccionada.isEsPublica());

        // Seleccionar tema
        for (Tema tema : listaTemas) {
            if (tema.getId() == seleccionada.getIdTema()) {
                cbTema.getSelectionModel().select(tema);
                break;
            }
        }

        if (esPreguntaPadre) {
            // Configuración para pregunta padre
            vboxOpciones.getChildren().clear();
            cbPreguntaPadre.getSelectionModel().clearSelection();
            cbPreguntaPadre.setDisable(true);
            cbTipoPregunta.setDisable(true);

            // Cargar preguntas hijas en la tabla
            List<Pregunta> hijas = PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId());
            tablaHijas.setItems(FXCollections.observableArrayList(hijas));

        } else if (esPreguntaHija) {
            // Configuración para pregunta hija
            cbPreguntaPadre.setDisable(true); // CRÍTICO: Deshabilitar para preguntas hijas
            cbTipoPregunta.setDisable(false); // Permitir cambiar tipo

            // Buscar y seleccionar el padre actual
            Pregunta padreActual = null;
            for (Pregunta p : cbPreguntaPadre.getItems()) {
                if (p.getId() == seleccionada.getIdPreguntaPadre()) {
                    padreActual = p;
                    break;
                }
            }

            if (padreActual != null) {
                cbPreguntaPadre.getSelectionModel().select(padreActual);
                System.out.println("Padre seleccionado: " + padreActual.getTexto());
            } else {
                System.out.println("⚠️ No se encontró el padre en la lista del ComboBox");
            }

            // Aplicar estilo visual para indicar que no se puede cambiar
            cbPreguntaPadre.setStyle("-fx-background-color: #f0f0f0; -fx-opacity: 0.7;");

            // Actualizar opciones y cargar respuestas
            actualizarOpcionesRespuesta();
            cargarOpcionesExistentes(seleccionada);
            tablaHijas.getItems().clear();

        } else {
            // Configuración para pregunta independiente (sin padre, no es padre)
            cbPreguntaPadre.setDisable(false);
            cbTipoPregunta.setDisable(false);
            cbPreguntaPadre.getSelectionModel().clearSelection();
            cbPreguntaPadre.setStyle(""); // Restaurar estilo normal

            // Actualizar opciones y cargar respuestas
            actualizarOpcionesRespuesta();
            cargarOpcionesExistentes(seleccionada);
            tablaHijas.getItems().clear();
        }
    }

    // Método auxiliar para cargar las opciones existentes
    private void cargarOpcionesExistentes(Pregunta pregunta) {
        List<OpcionRespuesta> opciones = PreguntaDAO.obtenerOpcionesDePregunta(pregunta.getId());

        switch (pregunta.getTipo()) {
            case "Opción Múltiple":
                if (opciones.size() >= 4) {
                    txtOpcion1.setText(opciones.get(0).getTexto());
                    chkCorrecta1.setSelected(opciones.get(0).isCorrecta());
                    txtOpcion2.setText(opciones.get(1).getTexto());
                    chkCorrecta2.setSelected(opciones.get(1).isCorrecta());
                    txtOpcion3.setText(opciones.get(2).getTexto());
                    chkCorrecta3.setSelected(opciones.get(2).isCorrecta());
                    txtOpcion4.setText(opciones.get(3).getTexto());
                    chkCorrecta4.setSelected(opciones.get(3).isCorrecta());
                }
                break;
            case "Verdadero/Falso":
                if (opciones.size() >= 2) {
                    chkCorrecta1.setSelected(opciones.get(0).isCorrecta());
                    chkCorrecta2.setSelected(opciones.get(1).isCorrecta());
                }
                break;
            case "Respuesta Corta":
                if (!opciones.isEmpty()) {
                    txtRespuestaCorta.setText(opciones.get(0).getTexto());
                }
                break;
        }
    }

    @FXML
    private void quitarPreguntaHija() {
        Pregunta hijaSeleccionada = tablaHijas.getSelectionModel().getSelectedItem();
        if (hijaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una pregunta hija para quitar.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = PreguntaDAO.quitarVinculoPadre(hijaSeleccionada.getId());
        if (exito) {
            tablaHijas.getItems().remove(hijaSeleccionada);
            mostrarAlerta("Éxito", "La pregunta hija fue desvinculada del padre.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo desvincular la pregunta hija.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una pregunta para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        // Verificar si la pregunta pertenece al docente actual
        if (seleccionada.getIdDocente() != docenteActual.getIdDocente()) {
            mostrarAlerta("Error", "Solo puede eliminar preguntas creadas por usted.", Alert.AlertType.WARNING);
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

        // Verificar estado actual de la pregunta
        boolean esPreguntaHija = preguntaEnEdicion.getIdPreguntaPadre() != null && preguntaEnEdicion.getIdPreguntaPadre() > 0;
        boolean esPreguntaPadre = chkEsPreguntaPadre.isSelected();

        String nuevoTexto = txtTexto.getText().trim();
        Tema temaSeleccionado = cbTema.getValue();
        String valorNotaTexto = txtValorNota.getText().trim();
        boolean nuevaEsPublica = chkEsPublica.isSelected();

        System.out.println("=== GUARDANDO EDICIÓN ===");
        System.out.println("ID pregunta: " + preguntaEnEdicion.getId());
        System.out.println("Es pregunta hija: " + esPreguntaHija);
        System.out.println("ID padre original: " + preguntaEnEdicion.getIdPreguntaPadre());

        // VALIDACIÓN CRÍTICA: Verificar cambios en relación padre-hija
        if (esPreguntaHija) {
            Pregunta padreSeleccionado = cbPreguntaPadre.getValue();

            // Verificar que el padre seleccionado coincida con el original
            if (padreSeleccionado == null) {
                mostrarAlerta("Error", "Una pregunta hija no puede quedar sin padre asignado.", Alert.AlertType.ERROR);
                return;
            }

            if (padreSeleccionado.getId() != preguntaEnEdicion.getIdPreguntaPadre()) {
                mostrarAlerta("Error", "No se puede cambiar la pregunta padre de una pregunta hija existente.\n" +
                        "Padre original: " + preguntaEnEdicion.getIdPreguntaPadre() +
                        "\nPadre seleccionado: " + padreSeleccionado.getId(), Alert.AlertType.ERROR);
                return;
            }

            System.out.println("✅ Validación padre-hija correcta");
        }

        // Validaciones básicas
        if (nuevoTexto.isEmpty() || temaSeleccionado == null || valorNotaTexto.isEmpty()) {
            mostrarAlerta("Error", "Debe completar todos los campos obligatorios.", Alert.AlertType.WARNING);
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

        // Determinar tipo y padre según el estado de la pregunta
        String nuevoTipo;
        Integer idPadre;

        if (esPreguntaPadre) {
            nuevoTipo = "Compuesta";
            idPadre = null; // Las preguntas padre no tienen padre
        } else if (esPreguntaHija) {
            // Para preguntas hijas, mantener el padre original SIEMPRE
            nuevoTipo = cbTipoPregunta.getValue();
            idPadre = preguntaEnEdicion.getIdPreguntaPadre(); // NUNCA cambiar

            if (nuevoTipo == null || nuevoTipo.isEmpty()) {
                mostrarAlerta("Error", "Debe seleccionar un tipo de pregunta.", Alert.AlertType.WARNING);
                return;
            }
        } else {
            // Pregunta independiente
            nuevoTipo = cbTipoPregunta.getValue();
            Pregunta padreSeleccionado = cbPreguntaPadre.getValue();
            idPadre = (padreSeleccionado != null) ? padreSeleccionado.getId() : null;

            if (nuevoTipo == null || nuevoTipo.isEmpty()) {
                mostrarAlerta("Error", "Debe seleccionar un tipo de pregunta.", Alert.AlertType.WARNING);
                return;
            }
        }

        // Preparar opciones de respuesta
        List<OpcionRespuesta> nuevasOpciones = new ArrayList<>();
        if (!esPreguntaPadre) {
            switch (nuevoTipo) {
                case "Opción Múltiple":
                    agregarOpcionSiExiste(txtOpcion1, chkCorrecta1, nuevasOpciones);
                    agregarOpcionSiExiste(txtOpcion2, chkCorrecta2, nuevasOpciones);
                    agregarOpcionSiExiste(txtOpcion3, chkCorrecta3, nuevasOpciones);
                    agregarOpcionSiExiste(txtOpcion4, chkCorrecta4, nuevasOpciones);
                    break;
                case "Verdadero/Falso":
                    nuevasOpciones.add(new OpcionRespuesta("Verdadero", chkCorrecta1.isSelected()));
                    nuevasOpciones.add(new OpcionRespuesta("Falso", chkCorrecta2.isSelected()));
                    break;
                case "Respuesta Corta":
                    if (!txtRespuestaCorta.getText().trim().isEmpty()) {
                        nuevasOpciones.add(new OpcionRespuesta(txtRespuestaCorta.getText().trim(), true));
                    }
                    break;
            }
        }

        // VALIDACIÓN FINAL DE SEGURIDAD
        if (esPreguntaHija && !idPadre.equals(preguntaEnEdicion.getIdPreguntaPadre())) {
            mostrarAlerta("Error", "Error crítico: Se detectó un intento de modificar la relación padre-hija.", Alert.AlertType.ERROR);
            System.out.println("❌ INTENTO DE MODIFICACIÓN BLOQUEADO - Padre original: " +
                    preguntaEnEdicion.getIdPreguntaPadre() + ", Nuevo padre: " + idPadre);
            return;
        }

        // Actualizar objeto pregunta
        preguntaEnEdicion.setTexto(nuevoTexto);
        preguntaEnEdicion.setTipo(nuevoTipo);
        preguntaEnEdicion.setIdTema(temaSeleccionado.getId());
        preguntaEnEdicion.setValorNota(nuevoValorNota);
        preguntaEnEdicion.setEsPublica(nuevaEsPublica);
        preguntaEnEdicion.setIdPreguntaPadre(idPadre);

        System.out.println("Actualizando pregunta con padre: " + idPadre);

        // Ejecutar actualización en base de datos
        boolean exito = PreguntaDAO.actualizarPregunta(
                preguntaEnEdicion.getId(), nuevoTexto, nuevoTipo, temaSeleccionado.getId(),
                nuevoValorNota, nuevaEsPublica, idPadre, nuevasOpciones
        );

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
        boolean esPreguntaPadre = chkEsPreguntaPadre.isSelected();
        String tipoPregunta = esPreguntaPadre ? "Compuesta" : cbTipoPregunta.getValue();
        Tema temaSeleccionado = cbTema.getValue();
        String valorTexto = txtValorNota.getText().trim();
        boolean esPublica = chkEsPublica.isSelected();
        Pregunta padreSeleccionado = cbPreguntaPadre.getValue();
        Integer idPadre = (!esPreguntaPadre && padreSeleccionado != null) ? padreSeleccionado.getId() : null;

        List<OpcionRespuesta> opciones = new ArrayList<>();
        if (!esPreguntaPadre) {
            switch (tipoPregunta) {
                case "Opción Múltiple":
                    agregarOpcionSiExiste(txtOpcion1, chkCorrecta1, opciones);
                    agregarOpcionSiExiste(txtOpcion2, chkCorrecta2, opciones);
                    agregarOpcionSiExiste(txtOpcion3, chkCorrecta3, opciones);
                    agregarOpcionSiExiste(txtOpcion4, chkCorrecta4, opciones);
                    break;
                case "Verdadero/Falso":
                    opciones.add(new OpcionRespuesta("Verdadero", chkCorrecta1.isSelected()));
                    opciones.add(new OpcionRespuesta("Falso", chkCorrecta2.isSelected()));
                    break;
                case "Respuesta Corta":
                    if (!txtRespuestaCorta.getText().trim().isEmpty()) {
                        opciones.add(new OpcionRespuesta(txtRespuestaCorta.getText().trim(), true));
                    }
                    break;
            }
        }
        if(!esPreguntaPadre) {
            if (textoPregunta.isEmpty() || tipoPregunta == null || temaSeleccionado == null || valorTexto.isEmpty()) {
                mostrarAlerta("Error", "Debe completar todos los campos, incluyendo el valor de la nota.", Alert.AlertType.WARNING);
                return;
            }
        } else {
            if (textoPregunta.isEmpty() || temaSeleccionado == null || valorTexto.isEmpty()) {
                mostrarAlerta("Error", "Pregunta padre debe completar el texto de la pregunta, " +
                        "el tema y el valor de la nota.", Alert.AlertType.WARNING);
                return;
            }
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

        if (!esPreguntaPadre && padreSeleccionado != null && "Compuesta".equalsIgnoreCase(padreSeleccionado.getTipo())) {
            mostrarAlerta("Error", "No puede asignar una pregunta hija a otra pregunta padre.", Alert.AlertType.WARNING);
            return;
        }

        Pregunta nuevaPregunta = new Pregunta(0, textoPregunta, tipoPregunta, temaSeleccionado.getId(),
                valorNota, esPublica, docenteActual.getIdDocente(), opciones);
        nuevaPregunta.setNombreTema(temaSeleccionado.getNombre());
        nuevaPregunta.setValorNota(valorNota);
        nuevaPregunta.setIdPreguntaPadre(idPadre); // ASIGNAR PADRE

        boolean insertada = PreguntaDAO.agregarPregunta(nuevaPregunta);

        if (insertada) {
            mostrarAlerta("Éxito", "Pregunta agregada correctamente.", Alert.AlertType.INFORMATION);
            cargarPreguntas();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo agregar la pregunta.", Alert.AlertType.ERROR);
        }
        actualizarComboBoxPreguntaPadre();
    }

    private void agregarOpcionSiExiste(TextField txtOpcion, CheckBox chkCorrecta, List<OpcionRespuesta> opciones) {
        if (txtOpcion != null && chkCorrecta != null && !txtOpcion.getText().trim().isEmpty()) {
            opciones.add(new OpcionRespuesta(txtOpcion.getText().trim(), chkCorrecta.isSelected()));
        }
    }

    private void actualizarOpcionesRespuesta() {
        if (chkEsPreguntaPadre.isSelected()) return;

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
        // Primero desbloquear todos los campos
        bloquearCamposParaVisualizacion(false);

        txtTexto.clear();
        txtValorNota.clear();
        cbTipoPregunta.getSelectionModel().clearSelection();
        cbTema.getSelectionModel().clearSelection();
        cbPreguntaPadre.getSelectionModel().clearSelection();
        chkEsPublica.setSelected(false);
        chkEsPreguntaPadre.setSelected(false);
        vboxOpciones.getChildren().clear();
        tablaHijas.getItems().clear();
        preguntaEnEdicion = null;

        //REstaurar estilos normales
        txtTexto.setStyle("");
        cbTipoPregunta.setStyle("");
        cbTema.setStyle("");
        txtValorNota.setStyle("");
        cbPreguntaPadre.setStyle("");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void cargarPreguntas() {
        // Cargar solo las preguntas visibles para el docente actual
        // (Preguntas públicas + preguntas privadas propias)
        ObservableList<Pregunta> listaPreguntas = FXCollections.observableArrayList(
                PreguntaDAO.obtenerPreguntasVisiblesParaDocente(docenteActual.getIdDocente())
        );
        tablaPreguntas.setItems(listaPreguntas);
        actualizarComboBoxPreguntaPadre();
    }

    @FXML
    public void mostrarPregunta() {
        Pregunta seleccionada = tablaPreguntas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Aviso", "Seleccione una pregunta para ver sus detalles.", Alert.AlertType.INFORMATION);
            return;
        }

        // Resetear el formulario primero
        limpiarFormulario();

        // Verificar si es pregunta hija (tiene padre asignado)
        boolean esPreguntaHija = seleccionada.getIdPreguntaPadre() != null && seleccionada.getIdPreguntaPadre() > 0;

        // Verificar si es pregunta padre (tipo compuesta O tiene hijas)
        boolean esTipoCompuesta = "Compuesta".equalsIgnoreCase(seleccionada.getTipo());
        boolean tieneHijas = !PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId()).isEmpty();
        boolean esPreguntaPadre = esTipoCompuesta || tieneHijas;

        System.out.println("=== MOSTRANDO PREGUNTA ===");
        System.out.println("ID: " + seleccionada.getId());
        System.out.println("Tipo: " + seleccionada.getTipo());
        System.out.println("ID Padre: " + seleccionada.getIdPreguntaPadre());
        System.out.println("Es pregunta hija: " + esPreguntaHija);
        System.out.println("Es pregunta padre: " + esPreguntaPadre);
        System.out.println("========================");

        // Configurar checkbox de pregunta padre
        chkEsPreguntaPadre.setSelected(esPreguntaPadre);

        // Llenar campos básicos
        txtTexto.setText(seleccionada.getTexto());
        cbTipoPregunta.setValue(seleccionada.getTipo());
        txtValorNota.setText(String.valueOf(seleccionada.getValorNota()));
        chkEsPublica.setSelected(seleccionada.isEsPublica());

        // Seleccionar tema
        for (Tema tema : listaTemas) {
            if (tema.getId() == seleccionada.getIdTema()) {
                cbTema.getSelectionModel().select(tema);
                break;
            }
        }

        if (esPreguntaPadre) {
            // Configuración para pregunta padre
            vboxOpciones.getChildren().clear();
            cbPreguntaPadre.getSelectionModel().clearSelection();

            // Cargar preguntas hijas en la tabla
            List<Pregunta> hijas = PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId());
            tablaHijas.setItems(FXCollections.observableArrayList(hijas));

        } else if (esPreguntaHija) {
            // Configuración para pregunta hija
            // Buscar y seleccionar el padre actual
            Pregunta padreActual = null;
            for (Pregunta p : cbPreguntaPadre.getItems()) {
                if (p.getId() == seleccionada.getIdPreguntaPadre()) {
                    padreActual = p;
                    break;
                }
            }

            if (padreActual != null) {
                cbPreguntaPadre.getSelectionModel().select(padreActual);
                System.out.println("Padre mostrado: " + padreActual.getTexto());
            }

            // Actualizar opciones y cargar respuestas
            actualizarOpcionesRespuesta();
            cargarOpcionesExistentes(seleccionada);
            tablaHijas.getItems().clear();

        } else {
            // Configuración para pregunta independiente
            cbPreguntaPadre.getSelectionModel().clearSelection();

            // Actualizar opciones y cargar respuestas
            actualizarOpcionesRespuesta();
            cargarOpcionesExistentes(seleccionada);
            tablaHijas.getItems().clear();
        }

        // BLOQUEAR TODOS LOS CAMPOS PARA MODO SOLO LECTURA
        bloquearCamposParaVisualizacion(true);
    }

    /**
     * Método para bloquear/desbloquear todos los campos del formulario
     * @param bloquear true para bloquear (modo solo lectura), false para desbloquear
     */
    private void bloquearCamposParaVisualizacion(boolean bloquear) {
        // Campos principales
        txtTexto.setDisable(bloquear);
        cbTipoPregunta.setDisable(bloquear);
        cbTema.setDisable(bloquear);
        txtValorNota.setDisable(bloquear);
        chkEsPublica.setDisable(bloquear);
        cbPreguntaPadre.setDisable(bloquear);
        chkEsPreguntaPadre.setDisable(bloquear);

        // Aplicar estilo visual para indicar modo solo lectura
        String estiloSoloLectura = bloquear ? "-fx-background-color: #f8f9fa; -fx-opacity: 0.8;" : "";

        txtTexto.setStyle(estiloSoloLectura);
        cbTipoPregunta.setStyle(estiloSoloLectura);
        cbTema.setStyle(estiloSoloLectura);
        txtValorNota.setStyle(estiloSoloLectura);
        cbPreguntaPadre.setStyle(estiloSoloLectura);

        // Bloquear opciones de respuesta si existen
        if (txtOpcion1 != null) txtOpcion1.setDisable(bloquear);
        if (txtOpcion2 != null) txtOpcion2.setDisable(bloquear);
        if (txtOpcion3 != null) txtOpcion3.setDisable(bloquear);
        if (txtOpcion4 != null) txtOpcion4.setDisable(bloquear);
        if (chkCorrecta1 != null) chkCorrecta1.setDisable(bloquear);
        if (chkCorrecta2 != null) chkCorrecta2.setDisable(bloquear);
        if (chkCorrecta3 != null) chkCorrecta3.setDisable(bloquear);
        if (chkCorrecta4 != null) chkCorrecta4.setDisable(bloquear);
        if (txtRespuestaCorta != null) txtRespuestaCorta.setDisable(bloquear);

        // Aplicar estilo a las opciones de respuesta
        if (txtOpcion1 != null) txtOpcion1.setStyle(estiloSoloLectura);
        if (txtOpcion2 != null) txtOpcion2.setStyle(estiloSoloLectura);
        if (txtOpcion3 != null) txtOpcion3.setStyle(estiloSoloLectura);
        if (txtOpcion4 != null) txtOpcion4.setStyle(estiloSoloLectura);
        if (txtRespuestaCorta != null) txtRespuestaCorta.setStyle(estiloSoloLectura);
    }
}
