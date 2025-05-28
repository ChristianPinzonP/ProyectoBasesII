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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // ACTUALIZADO: Usar método DAO para obtener tema del padre
        cbPreguntaPadre.setOnAction(event -> {
            Pregunta padreSeleccionado = cbPreguntaPadre.getValue();
            if (padreSeleccionado != null && !chkEsPreguntaPadre.isSelected()) {
                // Usar DAO para obtener el tema del padre
                int idTemaPadre = PreguntaDAO.obtenerTemaDePregunta(padreSeleccionado.getId());

                if (idTemaPadre != -1) {
                    // Buscar y seleccionar automáticamente el tema del padre
                    for (Tema tema : listaTemas) {
                        if (tema.getId() == idTemaPadre) {
                            cbTema.getSelectionModel().select(tema);
                            cbTema.setDisable(true); // Deshabilitar para forzar el mismo tema
                            cbTema.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffc107;");
                            break;
                        }
                    }
                } else {
                    mostrarAlerta("Error", "No se pudo obtener el tema de la pregunta padre.", Alert.AlertType.ERROR);
                }
            } else if (padreSeleccionado == null) {
                // Si no hay padre seleccionado, habilitar la selección libre de tema
                cbTema.setDisable(false);
                cbTema.setStyle("");
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

        // NUEVA VALIDACIÓN: Verificar si la pregunta está en exámenes presentados
        if (PreguntaDAO.preguntaEstaEnExamenPresentado(seleccionada.getId())) {
            List<Map<String, Object>> examenes = PreguntaDAO.obtenerExamenesQuUsanPregunta(seleccionada.getId());
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("No se puede editar esta pregunta porque está siendo utilizada en los siguientes exámenes que ya han sido presentados:\n\n");

            for (Map<String, Object> examen : examenes) {
                int presentaciones = (Integer) examen.get("totalPresentaciones");
                if (presentaciones > 0) {
                    mensaje.append("• ").append(examen.get("titulo"))
                            .append(" (").append(presentaciones).append(" presentaciones)\n");
                }
            }

            mensaje.append("\nPor motivos de integridad académica, no se permite modificar preguntas que ya han sido utilizadas en exámenes presentados.");

            mostrarAlerta("Edición No Permitida", mensaje.toString(), Alert.AlertType.WARNING);
            return;
        }

        preguntaEnEdicion = seleccionada;

        // Verificar si es pregunta hija (tiene padre asignado)
        boolean esPreguntaHija = seleccionada.getIdPreguntaPadre() != null && seleccionada.getIdPreguntaPadre() > 0;

        // Verificar si es pregunta padre (tipo compuesta O tiene hijas)
        boolean esTipoCompuesta = "Compuesta".equalsIgnoreCase(seleccionada.getTipo());
        boolean tieneHijas = !PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId()).isEmpty();
        boolean esPreguntaPadre = esTipoCompuesta || tieneHijas;

        // Debug
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
            cbTema.setDisable(false); // Los padres pueden cambiar tema
            cbTema.setStyle("");

            // Cargar preguntas hijas en la tabla
            List<Pregunta> hijas = PreguntaDAO.obtenerPreguntasHijas(seleccionada.getId());
            tablaHijas.setItems(FXCollections.observableArrayList(hijas));

        } else if (esPreguntaHija) {
            // Configuración para pregunta hija
            cbPreguntaPadre.setDisable(true); // CRÍTICO: Deshabilitar para preguntas hijas
            cbTipoPregunta.setDisable(false); // Permitir cambiar tipo
            cbTema.setDisable(true); // CRÍTICO: Las hijas no pueden cambiar tema
            cbTema.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffc107;");

            if (seleccionada.getIdPreguntaPadre() != null) {
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

                    // Verificar que el tema sea consistente usando DAO
                    int temaPadreActual = PreguntaDAO.obtenerTemaDePregunta(padreActual.getId());
                    if (temaPadreActual != seleccionada.getIdTema()) {
                        System.out.println("⚠️ Advertencia: Inconsistencia de tema detectada entre padre e hija");
                    }
                }
            }

            // Aplicar estilo visual para indicar que no se puede cambiar
            cbPreguntaPadre.setStyle("-fx-background-color: #f0f0f0; -fx-opacity: 0.7;");

            // Actualizar opciones y cargar respuestas
            actualizarOpcionesRespuesta();
            cargarOpcionesExistentes(seleccionada);
            tablaHijas.getItems().clear();

        } else {
            // Configuración para pregunta independiente
            cbPreguntaPadre.setDisable(false);
            cbTipoPregunta.setDisable(false);
            cbTema.setDisable(false);
            cbPreguntaPadre.getSelectionModel().clearSelection();
            cbPreguntaPadre.setStyle("");
            cbTema.setStyle("");//Restaurar estilo a normal

            // Actualizar opciones y cargar respuestas
            actualizarOpcionesRespuesta();
            cargarOpcionesExistentes(seleccionada);
            tablaHijas.getItems().clear();
        }
    }

    private String obtenerNombreTemaPorId(int idTema) {
        for (Tema tema : listaTemas) {
            if (tema.getId() == idTema) {
                return tema.getNombre();
            }
        }
        return "Tema no encontrado";
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

        // NUEVA VALIDACIÓN: Verificar si la pregunta está en exámenes presentados
        if (PreguntaDAO.preguntaEstaEnExamenPresentado(seleccionada.getId())) {
            List<Map<String, Object>> examenes = PreguntaDAO.obtenerExamenesQuUsanPregunta(seleccionada.getId());
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("No se puede eliminar esta pregunta porque está siendo utilizada en los siguientes exámenes que ya han sido presentados:\n\n");

            for (Map<String, Object> examen : examenes) {
                int presentaciones = (Integer) examen.get("totalPresentaciones");
                if (presentaciones > 0) {
                    mensaje.append("• ").append(examen.get("titulo"))
                            .append(" (").append(presentaciones).append(" presentaciones)\n");
                }
            }

            mensaje.append("\nPor motivos de integridad académica, no se permite eliminar preguntas que ya han sido utilizadas en exámenes presentados.");

            mostrarAlerta("Eliminación No Permitida", mensaje.toString(), Alert.AlertType.WARNING);
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

        // NUEVA VALIDACIÓN: Verificar nuevamente antes de guardar
        if (PreguntaDAO.preguntaEstaEnExamenPresentado(preguntaEnEdicion.getId())) {
            mostrarAlerta("Error",
                    "No se pueden guardar los cambios porque esta pregunta está siendo utilizada en exámenes que ya han sido presentados.",
                    Alert.AlertType.WARNING);
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

        // ACTUALIZADO: Usar método DAO para validar tema en edición
        if (esPreguntaHija) {
            Pregunta padreSeleccionado = cbPreguntaPadre.getValue();

            if (padreSeleccionado != null && temaSeleccionado != null) {
                if (!PreguntaDAO.validarTemaHijaPadre(padreSeleccionado.getId(), temaSeleccionado.getId())) {
                    // Obtener nombres de temas para el mensaje
                    String nombreTemaPadre = obtenerNombreTemaPorId(PreguntaDAO.obtenerTemaDePregunta(padreSeleccionado.getId()));
                    String nombreTemaSeleccionado = temaSeleccionado.getNombre();

                    mostrarAlerta("Error",
                            "La pregunta hija debe mantener el mismo tema que la pregunta padre.\n" +
                                    "Tema del padre: " + nombreTemaPadre + "\n" +
                                    "Tema seleccionado: " + nombreTemaSeleccionado,
                            Alert.AlertType.WARNING);
                    return;
                }
            }
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

        // Actualizar objeto pregunta
        preguntaEnEdicion.setTexto(nuevoTexto);
        preguntaEnEdicion.setTipo(nuevoTipo);
        preguntaEnEdicion.setIdTema(temaSeleccionado.getId());
        preguntaEnEdicion.setValorNota(nuevoValorNota);
        preguntaEnEdicion.setEsPublica(nuevaEsPublica);
        preguntaEnEdicion.setIdPreguntaPadre(idPadre);

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
            preguntaEnEdicion.setOpciones(nuevasOpciones);
        }

        System.out.println("Actualizando pregunta con padre: " + idPadre);

        // Ejecutar actualización en base de datos
        boolean actualizada = PreguntaDAO.actualizarPregunta(
                preguntaEnEdicion.getId(), nuevoTexto, nuevoTipo, temaSeleccionado.getId(),
                nuevoValorNota, nuevaEsPublica, idPadre, nuevasOpciones
        );

        if (actualizada) {
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

        // ACTUALIZADO: Usar método DAO para validar tema hija-padre
        if (padreSeleccionado != null && temaSeleccionado != null) {
            if (!PreguntaDAO.validarTemaHijaPadre(padreSeleccionado.getId(), temaSeleccionado.getId())) {
                // Obtener nombres de temas para mostrar en el mensaje
                String nombreTemaPadre = obtenerNombreTemaPorId(PreguntaDAO.obtenerTemaDePregunta(padreSeleccionado.getId()));
                String nombreTemaSeleccionado = temaSeleccionado.getNombre();

                mostrarAlerta("Error",
                        "La pregunta hija debe tener el mismo tema que la pregunta padre.\n" +
                                "Tema del padre: " + nombreTemaPadre + "\n" +
                                "Tema seleccionado: " + nombreTemaSeleccionado,
                        Alert.AlertType.WARNING);
                return;
            }
        }

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
        // Validación de valor numérico
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

        // Crear y guardar la pregunta
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

        // Restaurar estilos normales y habilitar campos
        txtTexto.setStyle("");
        cbTipoPregunta.setStyle("");
        cbTema.setStyle("");
        cbTema.setDisable(false); // IMPORTANTE: Rehabilitar tema
        txtValorNota.setStyle("");
        cbPreguntaPadre.setStyle("");
        cbPreguntaPadre.setDisable(false);
        cbTipoPregunta.setDisable(false);
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

        // Aplicar estilo condicional a las filas
        tablaPreguntas.setRowFactory(tv -> {
            TableRow<Pregunta> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldPregunta, newPregunta) -> {
                if (newPregunta != null) {
                    // Verificar si la pregunta está en exámenes presentados
                    if (PreguntaDAO.preguntaEstaEnExamenPresentado(newPregunta.getId())) {
                        row.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                        row.setTooltip(new Tooltip("Esta pregunta no se puede editar porque está en exámenes presentados"));
                    } else {
                        row.setStyle("");
                        row.setTooltip(null);
                    }
                }
            });
            return row;
        });

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

    public boolean esTemaValidoParaHija(int idPreguntaPadre, int idTemaHija) {
        String sql = "SELECT id_tema FROM pregunta WHERE id_pregunta = ?";
        try (PreparedStatement stmt = (PreparedStatement) DBConnection.getConnection()) {
            stmt.setInt(1, idPreguntaPadre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idTemaPadre = rs.getInt("id_tema");
                return idTemaPadre == idTemaHija;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}