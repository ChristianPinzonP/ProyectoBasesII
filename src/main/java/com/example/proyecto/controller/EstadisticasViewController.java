package com.example.proyecto.controller;

import com.example.proyecto.dao.EstadisticasDAO;
import com.example.proyecto.ReporteEstudiante;
import com.example.proyecto.ReporteExamen;
import com.example.proyecto.ReporteGrupo;
import com.example.proyecto.ReportePregunta;
import com.example.proyecto.ReporteTema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class EstadisticasViewController {

    // Tabla estudiantes
    @FXML private TableView<ReporteEstudiante> tablaEstudiantes;
    @FXML private TableColumn<ReporteEstudiante, Integer> colIdEstudiante;
    @FXML private TableColumn<ReporteEstudiante, String> colNombreEstudiante;
    @FXML private TableColumn<ReporteEstudiante, Integer> colCantidadExamenes;
    @FXML private TableColumn<ReporteEstudiante, Double> colPromedioEstudiante;

    // Tabla grupos
    @FXML private TableView<ReporteGrupo> tablaGrupos;
    @FXML private TableColumn<ReporteGrupo, Integer> colIdGrupo;
    @FXML private TableColumn<ReporteGrupo, String> colGrupo;
    @FXML private TableColumn<ReporteGrupo, Integer> colTotalExamenesPresentados;
    @FXML private TableColumn<ReporteGrupo, Double> colPromedioGrupo;

    // Tabla exámenes
    @FXML private TableView<ReporteExamen> tablaExamenes;
    @FXML private TableColumn<ReporteExamen, Integer> colIdExamen;
    @FXML private TableColumn<ReporteExamen, String> colNombreExamen;
    @FXML private TableColumn<ReporteExamen, Integer> colTotalPresentaciones;
    @FXML private TableColumn<ReporteExamen, Double> colPromedioExamen;
    @FXML private TableColumn<ReporteExamen, Double> colMaxNotaExamen;
    @FXML private TableColumn<ReporteExamen, Double> colMinNotaExamen;

    // Tabla preguntas
    @FXML private TableView<ReportePregunta> tablaPreguntas;
    @FXML private TableColumn<ReportePregunta, Integer> colIdPregunta;
    @FXML private TableColumn<ReportePregunta, String> colTextoPregunta;
    @FXML private TableColumn<ReportePregunta, Integer> colTotalRespuestas;
    @FXML private TableColumn<ReportePregunta, Double> colPorcentajeAciertos;

    // Tabla temas
    @FXML private TableView<ReporteTema> tablaTemas;
    @FXML private TableColumn<ReporteTema, Integer> colIdTema;
    @FXML private TableColumn<ReporteTema, String> colTema;
    @FXML private TableColumn<ReporteTema, Integer> colTotalRespuestasTema;
    @FXML private TableColumn<ReporteTema, Integer> colTotalCorrectas;
    @FXML private TableColumn<ReporteTema, Integer> colTotalIncorrectas;
    @FXML private TableColumn<ReporteTema, Double> colPromedioNota;
    @FXML private TableColumn<ReporteTema, Double> colMaxNotaTema;
    @FXML private TableColumn<ReporteTema, Double> colMinNotaTema;

    @FXML
    public void initialize() {
        // Configurar columnas tabla estudiantes
        colIdEstudiante.setCellValueFactory(new PropertyValueFactory<>("idEstudiante"));
        colNombreEstudiante.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidadExamenes.setCellValueFactory(new PropertyValueFactory<>("cantidadExamenes"));
        colPromedioEstudiante.setCellValueFactory(new PropertyValueFactory<>("promedio"));

        // Configurar columnas tabla grupos
        colIdGrupo.setCellValueFactory(new PropertyValueFactory<>("idGrupo"));
        colGrupo.setCellValueFactory(new PropertyValueFactory<>("grupo"));
        colTotalExamenesPresentados.setCellValueFactory(new PropertyValueFactory<>("totalExamenesPresentados"));
        colPromedioGrupo.setCellValueFactory(new PropertyValueFactory<>("promedioGeneral"));

        // Configurar columnas tabla exámenes
        colIdExamen.setCellValueFactory(new PropertyValueFactory<>("idExamen"));
        colNombreExamen.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTotalPresentaciones.setCellValueFactory(new PropertyValueFactory<>("totalPresentaciones"));
        colPromedioExamen.setCellValueFactory(new PropertyValueFactory<>("promedioNota"));
        colMaxNotaExamen.setCellValueFactory(new PropertyValueFactory<>("maxNota"));
        colMinNotaExamen.setCellValueFactory(new PropertyValueFactory<>("minNota"));

        // Configurar columnas tabla preguntas
        colIdPregunta.setCellValueFactory(new PropertyValueFactory<>("idPregunta"));
        colTextoPregunta.setCellValueFactory(new PropertyValueFactory<>("texto"));
        colTotalRespuestas.setCellValueFactory(new PropertyValueFactory<>("totalRespuestas"));
        colPorcentajeAciertos.setCellValueFactory(new PropertyValueFactory<>("porcentajeAciertos"));

        // Configurar columnas tabla temas
        colIdTema.setCellValueFactory(new PropertyValueFactory<>("idTema"));
        colTema.setCellValueFactory(new PropertyValueFactory<>("tema"));
        colTotalRespuestasTema.setCellValueFactory(new PropertyValueFactory<>("totalRespuestas"));
        colTotalCorrectas.setCellValueFactory(new PropertyValueFactory<>("totalCorrectas"));
        colTotalIncorrectas.setCellValueFactory(new PropertyValueFactory<>("totalIncorrectas"));
        colPromedioNota.setCellValueFactory(new PropertyValueFactory<>("promedioNota"));
        colMaxNotaTema.setCellValueFactory(new PropertyValueFactory<>("maxNota"));
        colMinNotaTema.setCellValueFactory(new PropertyValueFactory<>("minNota"));

        // Llenar las tablas
        ObservableList<ReporteEstudiante> estudiantes = FXCollections.observableArrayList(EstadisticasDAO.obtenerReporteEstudiante());
        ObservableList<ReporteGrupo> grupos = FXCollections.observableArrayList(EstadisticasDAO.obtenerReporteGrupo());
        ObservableList<ReporteExamen> examenes = FXCollections.observableArrayList(EstadisticasDAO.obtenerReporteExamen());
        ObservableList<ReportePregunta> preguntas = FXCollections.observableArrayList(EstadisticasDAO.obtenerReportePregunta());
        ObservableList<ReporteTema> temas = FXCollections.observableArrayList(EstadisticasDAO.obtenerReporteTema());

        tablaEstudiantes.setItems(estudiantes);
        tablaGrupos.setItems(grupos);
        tablaExamenes.setItems(examenes);
        tablaPreguntas.setItems(preguntas);
        tablaTemas.setItems(temas);
    }
}
