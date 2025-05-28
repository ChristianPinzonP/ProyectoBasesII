package com.example.proyecto.controller;

import com.example.proyecto.*;
import com.example.proyecto.dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.time.LocalDate;

public class ExamenViewController {

    @FXML private TableView<Examen> tablaExamenes;
    @FXML private TableColumn<Examen, Integer> colId;
    @FXML private TableColumn<Examen, String> colNombre;
    @FXML private TableColumn<Examen, String> colDescripcion;
    @FXML private TableColumn<Examen, Date> colFechaInicio;
    @FXML private TableColumn<Examen, Date> colFechaFin;
    @FXML private TableColumn<Examen, Integer> colTiempoLimite;
    @FXML private TableColumn<Examen, Integer> colIdDocente;
    @FXML private TableColumn<Examen, String> colTema;
    @FXML private TableColumn<Examen, String> colGrupo;

    @FXML private ListView<Pregunta> listPreguntasDisponibles;
    @FXML private ListView<Pregunta> listPreguntasAsignadas;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtTiempoLimite;
    @FXML private TextField txtIdDocente;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtNumPreguntas;
    @FXML private ComboBox<String> cbModoSeleccion;
    @FXML private TextField txtTiempoPorPregunta;
    @FXML private ComboBox<Tema> cbTema;
    @FXML private ComboBox<Grupo> cbGrupo;
    @FXML private TextField txtNotaAsignacion;
    @FXML private TextField txtIntentosPermitidos;

    // Nuevos campos para asignación aleatoria
    @FXML private TextField txtCantidadAleatoria;
    @FXML private TextField txtNotaAleatoria;
    @FXML private Label lblPreguntasActuales;
    @FXML private Label lblPreguntasFaltantes;

    private List<Tema> listaTemas;
    private ObservableList<Examen> listaExamenes;
    private FilteredList<Examen> filtroExamenes;
    private Docente docenteActual;
    private Examen ExamenActual;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colTiempoLimite.setCellValueFactory(new PropertyValueFactory<>("tiempoLimite"));
        colIdDocente.setCellValueFactory(new PropertyValueFactory<>("idDocente"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("nombreTema"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("nombreGrupo"));

        cargarExamenes();

        listaTemas = TemaDAO.obtenerTemas();
        cbTema.setItems(FXCollections.observableArrayList(listaTemas));
        cbModoSeleccion.setItems(FXCollections.observableArrayList("Manual", "Aleatorio", "Mixto"));

        cbTema.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarPreguntasDisponibles(newVal.getId());
        });

        listPreguntasDisponibles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNotaAsignacion.setText(String.valueOf(newVal.getValorNota()));
            }
        });

        tablaExamenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) seleccionarExamen();
        });

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroExamenes.setPredicate(examen -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return examen.getNombre().toLowerCase().contains(lower) ||
                        examen.getDescripcion().toLowerCase().contains(lower) ||
                        String.valueOf(examen.getIdDocente()).contains(lower);
            });
        });

        // Listener para actualizar contadores de preguntas
        listPreguntasAsignadas.getItems().addListener((javafx.collections.ListChangeListener<Pregunta>) c -> {
            actualizarContadoresPreguntas();
        });

        // NUEVAS CONFIGURACIONES PARA VALIDACIÓN DE FECHAS
        configurarListenersFechas();
        configurarFechasMinimas();

        // Listener para actualizar contadores de preguntas (ya existe)
        listPreguntasAsignadas.getItems().addListener((javafx.collections.ListChangeListener<Pregunta>) c -> {
            actualizarContadoresPreguntas();
        });
    }

    public void inicializarDocente(Docente docente) {
        this.docenteActual = docente;
        txtIdDocente.setText(String.valueOf(docente.getIdDocente()));
        txtIdDocente.setDisable(true);
        List<Grupo> grupos = GrupoDAO.obtenerGruposPorDocente(docente.getIdDocente());
        cbGrupo.setItems(FXCollections.observableArrayList(grupos));
    }

    @FXML
    public void cargarExamenes() {
        listaExamenes = FXCollections.observableArrayList(ExamenDAO.obtenerTodosLosExamenes());
        filtroExamenes = new FilteredList<>(listaExamenes, p -> true);
        SortedList<Examen> sorted = new SortedList<>(filtroExamenes);
        sorted.comparatorProperty().bind(tablaExamenes.comparatorProperty());
        tablaExamenes.setItems(sorted);
    }

    @FXML
    public void agregarExamen() {
        if (!validarFormulario()) return;

        Examen nuevoExamen = new Examen();
        nuevoExamen.setNombre(txtNombre.getText().trim());
        nuevoExamen.setDescripcion(txtDescripcion.getText().trim());
        nuevoExamen.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
        nuevoExamen.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
        nuevoExamen.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText().trim()));
        nuevoExamen.setNumeroPreguntas(Integer.parseInt(txtNumPreguntas.getText().trim()));
        nuevoExamen.setModoSeleccion(cbModoSeleccion.getValue());
        nuevoExamen.setTiempoPorPregunta(Integer.parseInt(txtTiempoPorPregunta.getText().trim()));
        nuevoExamen.setIdTema(cbTema.getValue().getId());
        nuevoExamen.setIdGrupo(cbGrupo.getValue().getIdGrupo());
        nuevoExamen.setIdDocente(docenteActual.getIdDocente());
        nuevoExamen.setIntentosPermitidos(Integer.parseInt(txtIntentosPermitidos.getText().trim()));

        boolean exito = ExamenDAO.agregarExamen(nuevoExamen);

        if (exito) {
            mostrarAlerta("Éxito", "Examen creado correctamente.", Alert.AlertType.INFORMATION);
            cargarExamenes();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo crear el examen.", Alert.AlertType.ERROR);
        }
    }

    private void cargarPreguntasDisponibles(int idTema) {
        if (ExamenActual == null) {
            System.out.println("⚠ No se ha seleccionado un examen aún.");
            return;
        }

        try {
            List<Pregunta> preguntas = ExamenPreguntaDAO.obtenerPreguntasPorTemaYDisponibles(
                    idTema,
                    docenteActual.getIdDocente(),
                    ExamenActual.getId()
            );
            listPreguntasDisponibles.setItems(FXCollections.observableArrayList(preguntas));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seleccionarExamen() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        this.ExamenActual = tablaExamenes.getSelectionModel().getSelectedItem();

        if (ex != null) {
            txtNombre.setText(ex.getNombre());
            txtDescripcion.setText(ex.getDescripcion());
            dpFechaInicio.setValue(ex.getFechaInicio().toLocalDate());
            dpFechaFin.setValue(ex.getFechaFin().toLocalDate());
            txtTiempoLimite.setText(String.valueOf(ex.getTiempoLimite()));
            txtIdDocente.setText(String.valueOf(ex.getIdDocente()));
            txtNumPreguntas.setText(String.valueOf(ex.getNumeroPreguntas()));
            cbModoSeleccion.setValue(ex.getModoSeleccion());
            txtTiempoPorPregunta.setText(String.valueOf(ex.getTiempoPorPregunta()));
            txtIntentosPermitidos.setText(String.valueOf(ex.getIntentosPermitidos()));
            cbTema.getItems().stream().filter(t -> t.getId() == ex.getIdTema()).findFirst().ifPresent(t -> cbTema.getSelectionModel().select(t));
            cbGrupo.getItems().stream().filter(g -> g.getIdGrupo() == ex.getIdGrupo()).findFirst().ifPresent(g -> cbGrupo.getSelectionModel().select(g));
            cargarPreguntasDisponibles(ex.getIdTema());

            listPreguntasAsignadas.setItems(FXCollections.observableArrayList(
                    ExamenPreguntaDAO.obtenerPreguntasDeExamen(ex.getId())
            ));

            // Actualizar contadores
            actualizarContadoresPreguntas();
        }
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        txtTiempoLimite.clear();
        txtNumPreguntas.clear();
        cbModoSeleccion.getSelectionModel().clearSelection();
        txtTiempoPorPregunta.clear();
        txtNotaAsignacion.clear();
        cbTema.getSelectionModel().clearSelection();
        cbGrupo.getSelectionModel().clearSelection();
        txtIntentosPermitidos.clear();
        txtCantidadAleatoria.clear();
        txtNotaAleatoria.clear();
    }

    private boolean validarFormulario() {
        StringBuilder error = new StringBuilder();
        LocalDate fechaActual = LocalDate.now();

        if (txtNombre.getText().isEmpty()) error.append("Nombre requerido.\n");
        if (txtDescripcion.getText().isEmpty()) error.append("Descripción requerida.\n");
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) error.append("Fechas requeridas.\n");
        if (txtTiempoLimite.getText().isEmpty()) error.append("Tiempo requerido.\n");
        if (txtNumPreguntas.getText().isEmpty()) error.append("Número de preguntas requerido.\n");
        if (cbModoSeleccion.getValue() == null) error.append("Modo de selección requerido.\n");
        if (txtTiempoPorPregunta.getText().isEmpty()) error.append("Tiempo por pregunta requerido.\n");
        if (cbTema.getValue() == null) error.append("Tema requerido.\n");
        if (cbGrupo.getValue() == null) error.append("Grupo requerido.\n");
        if (txtIntentosPermitidos.getText().isEmpty()) error.append("Intentos permitidos requerido.\n");

        // NUEVAS VALIDACIONES DE FECHAS
        if (dpFechaInicio.getValue() != null) {
            LocalDate fechaInicio = dpFechaInicio.getValue();

            // Validar que la fecha de inicio no sea anterior a hoy
            if (fechaInicio.isBefore(fechaActual)) {
                error.append("La fecha de inicio no puede ser anterior a la fecha actual.\n");
            }
        }

        if (dpFechaFin.getValue() != null) {
            LocalDate fechaFin = dpFechaFin.getValue();

            // Validar que la fecha de fin no sea anterior a hoy
            if (fechaFin.isBefore(fechaActual)) {
                error.append("La fecha de fin no puede ser anterior a la fecha actual.\n");
            }
        }

        // Validar relación entre fechas de inicio y fin
        if (dpFechaInicio.getValue() != null && dpFechaFin.getValue() != null) {
            LocalDate fechaInicio = dpFechaInicio.getValue();
            LocalDate fechaFin = dpFechaFin.getValue();

            if (fechaFin.isBefore(fechaInicio)) {
                error.append("La fecha de fin no puede ser anterior a la fecha de inicio.\n");
            }

            if (fechaFin.equals(fechaInicio)) {
                error.append("La fecha de fin debe ser posterior a la fecha de inicio.\n");
            }
        }

        try {
            if (Integer.parseInt(txtTiempoLimite.getText()) <= 0) error.append("Tiempo debe ser positivo.\n");
            if (Integer.parseInt(txtNumPreguntas.getText()) <= 0) error.append("Mínimo 1 pregunta.\n");
            if (Integer.parseInt(txtTiempoPorPregunta.getText()) <= 0) error.append("Tiempo por pregunta inválido.\n");
            if (Integer.parseInt(txtIntentosPermitidos.getText()) <= 0) error.append("Los intentos deben ser mayor que 0.\n");
        } catch (NumberFormatException e) {
            error.append("Valores numéricos inválidos.\n");
        }

        if (!error.toString().isEmpty()) {
            mostrarAlerta("Error en formulario", error.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    public void editarExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();
        if (examenSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un examen para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (!validarFormulario()) return;

        examenSeleccionado.setNombre(txtNombre.getText().trim());
        examenSeleccionado.setDescripcion(txtDescripcion.getText().trim());
        examenSeleccionado.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
        examenSeleccionado.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
        examenSeleccionado.setTiempoLimite(Integer.parseInt(txtTiempoLimite.getText().trim()));
        examenSeleccionado.setNumeroPreguntas(Integer.parseInt(txtNumPreguntas.getText().trim()));
        examenSeleccionado.setModoSeleccion(cbModoSeleccion.getValue());
        examenSeleccionado.setTiempoPorPregunta(Integer.parseInt(txtTiempoPorPregunta.getText().trim()));
        examenSeleccionado.setIdTema(cbTema.getValue().getId());
        examenSeleccionado.setIdGrupo(cbGrupo.getValue().getIdGrupo());
        examenSeleccionado.setIntentosPermitidos(Integer.parseInt(txtIntentosPermitidos.getText().trim()));

        boolean actualizado = ExamenDAO.editarExamen(examenSeleccionado);

        if (actualizado) {
            mostrarAlerta("Éxito", "Examen actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarExamenes();
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el examen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void eliminarExamen() {
        Examen examenSeleccionado = tablaExamenes.getSelectionModel().getSelectedItem();

        if (examenSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un examen para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar este examen?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminado = ExamenDAO.eliminarExamen(examenSeleccionado.getId());
            if (eliminado) {
                mostrarAlerta("Éxito", "Examen eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarExamenes();
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el examen.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void asignarPregunta() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        Pregunta pr = listPreguntasDisponibles.getSelectionModel().getSelectedItem();

        if (ex == null || pr == null) {
            mostrarAlerta("Error", "Selecciona un examen y una pregunta.", Alert.AlertType.ERROR);
            return;
        }

        // VALIDACIÓN: Verificar coherencia de tema
        if (!validarCoherenciaTema(ex, pr)) {
            mostrarAlerta("Error de coherencia",
                    "La pregunta seleccionada no pertenece al tema del examen.\n" +
                            "Tema del examen: " + cbTema.getValue().getNombre() + "\n" +
                            "Tema de la pregunta: " + pr.getNombreTema(),
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            double nota = Double.parseDouble(txtNotaAsignacion.getText().trim());
            if (nota <= 0 || nota > 5) throw new NumberFormatException();

            if (ExamenPreguntaDAO.asignarPreguntaAExamen(ex.getId(), pr.getId(), nota)) {
                mostrarAlerta("Éxito", "Pregunta asignada con nota.", Alert.AlertType.INFORMATION);
                actualizarListasPreguntas();
                txtNotaAsignacion.clear();
            } else {
                mostrarAlerta("Error", "No se pudo asignar.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Nota inválida (0.1 - 5).", Alert.AlertType.WARNING);
        }
    }

    // Validar coherencia entre tema del examen y pregunta
    private boolean validarCoherenciaTema(Examen examen, Pregunta pregunta) {
        if (examen == null || pregunta == null) return false;

        // Obtener el tema del examen desde el ComboBox
        Tema temaExamen = cbTema.getValue();
        if (temaExamen == null) return false;

        // Verificar que la pregunta pertenezca al mismo tema
        return pregunta.getIdTema() == temaExamen.getId();
    }

    @FXML
    public void eliminarPreguntaAsignada() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        Pregunta pr = listPreguntasAsignadas.getSelectionModel().getSelectedItem();
        if (ex == null || pr == null) {
            mostrarAlerta("Error", "Selecciona examen y pregunta.", Alert.AlertType.ERROR);
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar esta pregunta?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = conf.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            if (ExamenPreguntaDAO.eliminarPreguntaDeExamen(ex.getId(), pr.getId())) {
                mostrarAlerta("Éxito", "Pregunta eliminada.", Alert.AlertType.INFORMATION);
                actualizarListasPreguntas();
            } else mostrarAlerta("Error", "No se pudo eliminar.", Alert.AlertType.ERROR);
        }
    }

    // NUEVA FUNCIONALIDAD: Asignación aleatoria de preguntas
    @FXML
    public void asignarPreguntasAleatorias() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        if (ex == null) {
            mostrarAlerta("Error", "Selecciona un examen primero.", Alert.AlertType.ERROR);
            return;
        }

        Tema tema = cbTema.getValue();
        if (tema == null) {
            mostrarAlerta("Error", "Selecciona un tema primero.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int cantidadDeseada = Integer.parseInt(txtCantidadAleatoria.getText().trim());

            if (cantidadDeseada <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }

            // Verificar límite del examen
            int preguntasActuales = listPreguntasAsignadas.getItems().size();
            int totalEsperado = Integer.parseInt(txtNumPreguntas.getText().trim());

            if (preguntasActuales + cantidadDeseada > totalEsperado) {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Límite excedido");
                confirmacion.setHeaderText("El total de preguntas excederá el límite del examen");
                confirmacion.setContentText(
                        String.format("Preguntas actuales: %d\nCantidad a agregar: %d\nTotal resultante: %d\nLímite del examen: %d\n\n¿Continuar de todas formas?",
                                preguntasActuales, cantidadDeseada, preguntasActuales + cantidadDeseada, totalEsperado)
                );

                Optional<ButtonType> resultado = confirmacion.showAndWait();
                if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                    return;
                }
            }

            // FILTRADO POR TEMA: Solo preguntas del tema del examen
            List<Pregunta> preguntasDisponibles = listPreguntasDisponibles.getItems().stream()
                    .filter(p -> p.getIdTema() == tema.getId())
                    .collect(java.util.stream.Collectors.toList());

            if (preguntasDisponibles.isEmpty()) {
                mostrarAlerta("Error",
                        "No hay preguntas disponibles del tema '" + tema.getNombre() + "' para asignar.",
                        Alert.AlertType.WARNING);
                return;
            }

            // Resto del método permanece igual...
            int cantidadReal = Math.min(cantidadDeseada, preguntasDisponibles.size());
            if (cantidadReal < cantidadDeseada) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Cantidad ajustada");
                info.setHeaderText(null);
                info.setContentText(String.format("Solo hay %d preguntas disponibles del tema '%s'. Se asignarán %d preguntas.",
                        preguntasDisponibles.size(), tema.getNombre(), cantidadReal));
                info.showAndWait();
            }

            Collections.shuffle(preguntasDisponibles);
            List<Pregunta> preguntasAAsignar = preguntasDisponibles.subList(0, cantidadReal);

            int asignadas = 0;
            for (Pregunta pregunta : preguntasAAsignar) {
                // Validación adicional antes de asignar
                if (!validarCoherenciaTema(ex, pregunta)) {
                    System.err.println("⚠ Pregunta " + pregunta.getId() + " no coincide con tema del examen");
                    continue;
                }

                double notaDelSistema = pregunta.getValorNota();
                if (ExamenPreguntaDAO.asignarPreguntaAExamen(ex.getId(), pregunta.getId(), notaDelSistema)) {
                    asignadas++;
                }
            }

            if (asignadas > 0) {
                actualizarListasPreguntas();
                txtCantidadAleatoria.clear();
                mostrarAlerta("Éxito",
                        String.format("Se asignaron %d preguntas del tema '%s' usando sus notas del sistema.",
                                asignadas, tema.getNombre()),
                        Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo asignar ninguna pregunta.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Valor numérico inválido en cantidad.", Alert.AlertType.WARNING);
        }
    }

    // NUEVA FUNCIONALIDAD: Completar automáticamente las preguntas faltantes
    @FXML
    public void completarPreguntasFaltantes() {
        Examen ex = tablaExamenes.getSelectionModel().getSelectedItem();
        if (ex == null) {
            mostrarAlerta("Error", "Selecciona un examen primero.", Alert.AlertType.ERROR);
            return;
        }

        Tema tema = cbTema.getValue();
        if (tema == null) {
            mostrarAlerta("Error", "Selecciona un tema primero.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int totalRequerido = Integer.parseInt(txtNumPreguntas.getText().trim());
            int preguntasActuales = listPreguntasAsignadas.getItems().size();
            int faltantes = totalRequerido - preguntasActuales;

            if (faltantes <= 0) {
                mostrarAlerta("Información", "El examen ya tiene todas las preguntas requeridas.", Alert.AlertType.INFORMATION);
                return;
            }

            // FILTRADO POR TEMA: Solo preguntas del tema del examen
            List<Pregunta> preguntasDisponibles = listPreguntasDisponibles.getItems().stream()
                    .filter(p -> p.getIdTema() == tema.getId())
                    .collect(java.util.stream.Collectors.toList());

            if (preguntasDisponibles.isEmpty()) {
                mostrarAlerta("Error",
                        "No hay preguntas disponibles del tema '" + tema.getNombre() + "' para completar.",
                        Alert.AlertType.WARNING);
                return;
            }

            int cantidadReal = Math.min(faltantes, preguntasDisponibles.size());

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Completar preguntas faltantes");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText(
                    String.format("Se asignarán %d preguntas del tema '%s' de forma aleatoria para completar el examen.\nCada pregunta usará su nota del sistema.\n\n¿Continuar?",
                            cantidadReal, tema.getNombre())
            );

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }

            Collections.shuffle(preguntasDisponibles);
            List<Pregunta> preguntasAAsignar = preguntasDisponibles.subList(0, cantidadReal);

            int asignadas = 0;
            for (Pregunta pregunta : preguntasAAsignar) {
                // Validación adicional antes de asignar
                if (!validarCoherenciaTema(ex, pregunta)) {
                    System.err.println("⚠ Pregunta " + pregunta.getId() + " no coincide con tema del examen");
                    continue;
                }

                double notaDelSistema = pregunta.getValorNota();
                if (ExamenPreguntaDAO.asignarPreguntaAExamen(ex.getId(), pregunta.getId(), notaDelSistema)) {
                    asignadas++;
                }
            }

            if (asignadas > 0) {
                actualizarListasPreguntas();
                mostrarAlerta("Éxito",
                        String.format("Se completaron %d preguntas faltantes del tema '%s' usando sus notas del sistema.",
                                asignadas, tema.getNombre()),
                        Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo completar ninguna pregunta.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Valores numéricos inválidos.", Alert.AlertType.WARNING);
        }
    }

    // Actualizar contadores de preguntas
    private void actualizarContadoresPreguntas() {
        if (ExamenActual == null || txtNumPreguntas.getText().trim().isEmpty()) {
            lblPreguntasActuales.setText("Actuales: 0");
            lblPreguntasFaltantes.setText("Faltantes: 0");
            return;
        }

        try {
            int total = Integer.parseInt(txtNumPreguntas.getText().trim());
            int actuales = listPreguntasAsignadas.getItems().size();
            int faltantes = Math.max(0, total - actuales);

            lblPreguntasActuales.setText("Actuales: " + actuales);
            lblPreguntasFaltantes.setText("Faltantes: " + faltantes);

            // Cambiar color si excede el límite
            if (actuales > total) {
                lblPreguntasActuales.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else if (actuales == total) {
                lblPreguntasActuales.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                lblPreguntasActuales.setStyle("-fx-text-fill: black;");
            }

        } catch (NumberFormatException e) {
            lblPreguntasActuales.setText("Actuales: 0");
            lblPreguntasFaltantes.setText("Faltantes: 0");
        }
    }

    private void actualizarListasPreguntas() {
        if (ExamenActual == null) return;

        Tema temaSeleccionado = cbTema.getValue();
        if (temaSeleccionado != null) {
            cargarPreguntasDisponibles(temaSeleccionado.getId());
        }

        listPreguntasAsignadas.setItems(FXCollections.observableArrayList(
                ExamenPreguntaDAO.obtenerPreguntasDeExamen(ExamenActual.getId())
        ));

        // Actualizar contadores después de cambiar las listas
        actualizarContadoresPreguntas();
    }

    // Agregar listeners para validación en tiempo real
    private void configurarListenersFechas() {
        // Listener para la fecha de inicio
        dpFechaInicio.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                LocalDate fechaActual = LocalDate.now();

                if (newDate.isBefore(fechaActual)) {
                    // Cambiar estilo para indicar error
                    dpFechaInicio.setStyle("-fx-border-color: red; -fx-border-width: 2px;");

                    // Mostrar tooltip con el error
                    Tooltip tooltip = new Tooltip("La fecha no puede ser anterior a hoy (" + fechaActual + ")");
                    dpFechaInicio.setTooltip(tooltip);
                } else {
                    // Restaurar estilo normal
                    dpFechaInicio.setStyle("");
                    dpFechaInicio.setTooltip(null);
                }

                // Validar coherencia con fecha de fin
                if (dpFechaFin.getValue() != null && newDate.isAfter(dpFechaFin.getValue())) {
                    dpFechaInicio.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
                    Tooltip tooltip = new Tooltip("La fecha de inicio debe ser anterior a la fecha de fin");
                    dpFechaInicio.setTooltip(tooltip);
                }
            }
        });

        // Listener para la fecha de fin
        dpFechaFin.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                LocalDate fechaActual = LocalDate.now();

                if (newDate.isBefore(fechaActual)) {
                    dpFechaFin.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    Tooltip tooltip = new Tooltip("La fecha no puede ser anterior a hoy (" + fechaActual + ")");
                    dpFechaFin.setTooltip(tooltip);
                } else {
                    dpFechaFin.setStyle("");
                    dpFechaFin.setTooltip(null);
                }

                // Validar coherencia con fecha de inicio
                if (dpFechaInicio.getValue() != null && newDate.isBefore(dpFechaInicio.getValue())) {
                    dpFechaFin.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
                    Tooltip tooltip = new Tooltip("La fecha de fin debe ser posterior a la fecha de inicio");
                    dpFechaFin.setTooltip(tooltip);
                }
            }
        });
    }

    // Método para configurar fecha mínima en los DatePickers
    private void configurarFechasMinimas() {
        LocalDate fechaActual = LocalDate.now();

        // Configurar fecha mínima para ambos DatePickers
        dpFechaInicio.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date.isBefore(fechaActual)) {
                    setDisabled(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Rosa claro para fechas deshabilitadas
                }
            }
        });

        dpFechaFin.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate fechaInicio = dpFechaInicio.getValue();

                if (date.isBefore(fechaActual)) {
                    setDisabled(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                } else if (fechaInicio != null && date.isBefore(fechaInicio.plusDays(1))) {
                    setDisabled(true);
                    setStyle("-fx-background-color: #ffe4b5;"); // Beige para fechas no válidas por ser anteriores al inicio
                }
            }
        });
    }
}