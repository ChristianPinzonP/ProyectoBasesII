package com.example.proyecto.dao;

import com.example.proyecto.reportes.ReporteEstudiante;
import com.example.proyecto.reportes.ReporteGrupo;
import com.example.proyecto.reportes.ReportePregunta;
import com.example.proyecto.reportes.ReporteTema;
import com.example.proyecto.reportes.ReporteExamen;
import com.example.proyecto.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasDAO {

    public static List<ReporteEstudiante> obtenerReporteEstudiante() {
        List<ReporteEstudiante> lista = new ArrayList<>();

        // Primero ejecutar el procedimiento PL/SQL para generar/actualizar estadísticas
        String callProcedure = "{CALL GENERAR_ESTADISTICAS_ESTUDIANTE(?)}";
        String selectQuery = "SELECT ID_ESTUDIANTE, NOMBRE, TOTAL_EXAMENES_PRESENTADOS, PROMEDIO_NOTA FROM VISTA_ESTADISTICAS_ESTUDIANTE";

        try (Connection conn = DBConnection.getConnection()) {

            // Obtener todos los IDs de estudiantes únicos que han presentado exámenes
            String getStudentsQuery = "SELECT DISTINCT id_estudiante FROM PRESENTACION_EXAMEN";
            try (Statement stmt = conn.createStatement();
                 ResultSet rsStudents = stmt.executeQuery(getStudentsQuery)) {

                // Ejecutar el procedimiento para cada estudiante
                try (CallableStatement cs = conn.prepareCall(callProcedure)) {
                    while (rsStudents.next()) {
                        int idEstudiante = rsStudents.getInt("id_estudiante");
                        cs.setInt(1, idEstudiante);
                        cs.execute();
                    }
                }
            }

            // Ahora obtener los datos de la vista actualizada
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                while (rs.next()) {
                    ReporteEstudiante r = new ReporteEstudiante();
                    r.setIdEstudiante(rs.getInt("ID_ESTUDIANTE"));
                    r.setNombre(rs.getString("NOMBRE"));
                    r.setCantidadExamenes(rs.getInt("TOTAL_EXAMENES_PRESENTADOS"));
                    r.setPromedio(rs.getDouble("PROMEDIO_NOTA"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReporteGrupo> obtenerReporteGrupo() {
        List<ReporteGrupo> lista = new ArrayList<>();

        // Primero ejecutar el procedimiento PL/SQL para generar/actualizar estadísticas
        String callProcedure = "{CALL GENERAR_ESTADISTICAS_GRUPO(?)}";
        String selectQuery = "SELECT ID_GRUPO, GRUPO, TOTAL_EXAMENES_PRESENTADOS, PROMEDIO_GENERAL, MAX_NOTA, MIN_NOTA FROM VISTA_ESTADISTICAS_GRUPO";

        try (Connection conn = DBConnection.getConnection()) {

            // Obtener todos los IDs de grupos únicos
            String getGroupsQuery = "SELECT DISTINCT g.id_grupo FROM GRUPO g " +
                    "JOIN ESTUDIANTE_GRUPO eg ON eg.id_grupo = g.id_grupo " +
                    "JOIN PRESENTACION_EXAMEN pres ON pres.id_estudiante = eg.id_estudiante";
            try (Statement stmt = conn.createStatement();
                 ResultSet rsGroups = stmt.executeQuery(getGroupsQuery)) {

                // Ejecutar el procedimiento para cada grupo
                try (CallableStatement cs = conn.prepareCall(callProcedure)) {
                    while (rsGroups.next()) {
                        int idGrupo = rsGroups.getInt("id_grupo");
                        cs.setInt(1, idGrupo);
                        cs.execute();
                    }
                }
            }

            // Ahora obtener los datos de la vista actualizada
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                while (rs.next()) {
                    ReporteGrupo r = new ReporteGrupo();
                    r.setIdGrupo(rs.getInt("ID_GRUPO"));
                    r.setGrupo(rs.getString("GRUPO"));
                    r.setTotalExamenesPresentados(rs.getInt("TOTAL_EXAMENES_PRESENTADOS"));
                    r.setPromedioGeneral(rs.getDouble("PROMEDIO_GENERAL"));
                    r.setMaxNota(rs.getDouble("MAX_NOTA"));
                    r.setMinNota(rs.getDouble("MIN_NOTA"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReportePregunta> obtenerReportePregunta() {
        List<ReportePregunta> lista = new ArrayList<>();

        // Primero ejecutar el procedimiento PL/SQL para generar/actualizar estadísticas
        String callProcedure = "{CALL GENERAR_ESTADISTICAS_PREGUNTA(?)}";
        String selectQuery = "SELECT ID_PREGUNTA, PREGUNTA, TOTAL_RESPUESTAS, PORCENTAJE_ACIERTOS FROM VISTA_ESTADISTICAS_PREGUNTA";

        try (Connection conn = DBConnection.getConnection()) {

            // Obtener todos los IDs de preguntas que han sido respondidas
            String getPreguntasQuery = "SELECT DISTINCT re.id_pregunta FROM RESPUESTA_ESTUDIANTE re";
            try (Statement stmt = conn.createStatement();
                 ResultSet rsPreguntas = stmt.executeQuery(getPreguntasQuery)) {

                // Ejecutar el procedimiento para cada pregunta
                try (CallableStatement cs = conn.prepareCall(callProcedure)) {
                    while (rsPreguntas.next()) {
                        int idPregunta = rsPreguntas.getInt("id_pregunta");
                        cs.setInt(1, idPregunta);
                        cs.execute();
                    }
                }
            }

            // Ahora obtener los datos de la vista actualizada
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                while (rs.next()) {
                    ReportePregunta r = new ReportePregunta();
                    r.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                    r.setPregunta(rs.getString("PREGUNTA"));
                    r.setTotalRespuestas(rs.getInt("TOTAL_RESPUESTAS"));
                    r.setPorcentajeAciertos(rs.getDouble("PORCENTAJE_ACIERTOS"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReporteExamen> obtenerReporteExamen() {
        List<ReporteExamen> lista = new ArrayList<>();

        // Primero ejecutar el procedimiento PL/SQL para generar/actualizar estadísticas
        String callProcedure = "{CALL GENERAR_ESTADISTICAS_EXAMEN(?)}";
        String selectQuery = "SELECT ID_EXAMEN, NOMBRE, TOTAL_PRESENTACIONES, PROMEDIO_NOTA, MAX_NOTA, MIN_NOTA FROM VISTA_ESTADISTICAS_EXAMEN";

        try (Connection conn = DBConnection.getConnection()) {

            // Obtener todos los IDs de exámenes únicos que han sido presentados
            String getExamenesQuery = "SELECT DISTINCT id_examen FROM PRESENTACION_EXAMEN";
            try (Statement stmt = conn.createStatement();
                 ResultSet rsExamenes = stmt.executeQuery(getExamenesQuery)) {

                // Ejecutar el procedimiento para cada examen
                try (CallableStatement cs = conn.prepareCall(callProcedure)) {
                    while (rsExamenes.next()) {
                        int idExamen = rsExamenes.getInt("id_examen");
                        cs.setInt(1, idExamen);
                        cs.execute();
                    }
                }
            }

            // Ahora obtener los datos de la vista actualizada
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ReporteExamen r = new ReporteExamen();
                    r.setIdExamen(rs.getInt("ID_EXAMEN"));
                    r.setNombre(rs.getString("NOMBRE"));
                    r.setTotalPresentaciones(rs.getInt("TOTAL_PRESENTACIONES"));
                    r.setPromedioNota(rs.getDouble("PROMEDIO_NOTA"));
                    r.setMaxNota(rs.getDouble("MAX_NOTA"));
                    r.setMinNota(rs.getDouble("MIN_NOTA"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReporteTema> obtenerReporteTema() {
        List<ReporteTema> lista = new ArrayList<>();

        // Primero ejecutar el procedimiento PL/SQL para generar/actualizar estadísticas
        String callProcedure = "{CALL GENERAR_ESTADISTICAS_TEMA(?)}";
        String selectQuery = "SELECT ID_TEMA, TEMA, TOTAL_RESPUESTAS, TOTAL_CORRECTAS, TOTAL_INCORRECTAS, PROMEDIO_NOTA, MAX_NOTA, MIN_NOTA FROM VISTA_ESTADISTICAS_TEMA";

        try (Connection conn = DBConnection.getConnection()) {

            // Obtener todos los IDs de temas únicos que tienen preguntas respondidas
            String getTemasQuery = "SELECT DISTINCT t.id_tema FROM TEMA t " +
                    "JOIN PREGUNTA p ON p.id_tema = t.id_tema " +
                    "JOIN RESPUESTA r ON r.id_pregunta = p.id_pregunta " +
                    "JOIN RESPUESTA_ESTUDIANTE re ON re.id_respuesta = r.id_respuesta";

            try (Statement stmt = conn.createStatement();
                 ResultSet rsTemas = stmt.executeQuery(getTemasQuery)) {

                // Ejecutar el procedimiento para cada tema
                try (CallableStatement cs = conn.prepareCall(callProcedure)) {
                    while (rsTemas.next()) {
                        int idTema = rsTemas.getInt("id_tema");
                        cs.setInt(1, idTema);
                        cs.execute();
                    }
                }
            }

            // Ahora obtener los datos de la vista actualizada
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectQuery)) {

                while (rs.next()) {
                    ReporteTema r = new ReporteTema();
                    r.setIdTema(rs.getInt("ID_TEMA"));
                    r.setTema(rs.getString("TEMA"));
                    r.setTotalRespuestas(rs.getInt("TOTAL_RESPUESTAS"));
                    r.setTotalCorrectas(rs.getInt("TOTAL_CORRECTAS"));
                    r.setTotalIncorrectas(rs.getInt("TOTAL_INCORRECTAS"));
                    r.setPromedioNota(rs.getDouble("PROMEDIO_NOTA"));
                    r.setMaxNota(rs.getDouble("MAX_NOTA"));
                    r.setMinNota(rs.getDouble("MIN_NOTA"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
