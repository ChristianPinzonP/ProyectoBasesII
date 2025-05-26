package com.example.proyecto.dao;

import com.example.proyecto.ReporteEstudiante;
import com.example.proyecto.ReporteGrupo;
import com.example.proyecto.ReportePregunta;
import com.example.proyecto.ReporteTema;
import com.example.proyecto.ReporteExamen;
import com.example.proyecto.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasDAO {

    public static List<ReporteEstudiante> obtenerReporteEstudiante() {
        List<ReporteEstudiante> lista = new ArrayList<>();
        String sql = "SELECT ID_ESTUDIANTE, NOMBRE, TOTAL_EXAMENES_PRESENTADOS, PROMEDIO_NOTA FROM VISTA_ESTADISTICAS_ESTUDIANTE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ReporteEstudiante r = new ReporteEstudiante();
                r.setIdEstudiante(rs.getInt("ID_ESTUDIANTE"));
                r.setNombre(rs.getString("NOMBRE"));
                r.setCantidadExamenes(rs.getInt("TOTAL_EXAMENES_PRESENTADOS"));
                r.setPromedio(rs.getDouble("PROMEDIO_NOTA"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReporteGrupo> obtenerReporteGrupo() {
        List<ReporteGrupo> lista = new ArrayList<>();
        String sql = "SELECT ID_GRUPO, GRUPO, TOTAL_EXAMENES_PRESENTADOS, PROMEDIO_GENERAL, MAX_NOTA, MIN_NOTA FROM VISTA_ESTADISTICAS_GRUPO";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ReporteGrupo r = new ReporteGrupo();
                r.setIdGrupo(rs.getInt("ID_GRUPO"));
                r.setGrupo(rs.getString("GRUPO")); // <- corregido
                r.setTotalExamenesPresentados(rs.getInt("TOTAL_EXAMENES_PRESENTADOS")); // <- corregido
                r.setPromedioGeneral(rs.getDouble("PROMEDIO_GENERAL"));
                r.setMaxNota(rs.getDouble("MAX_NOTA")); // <- agregado para usar maxNota
                r.setMinNota(rs.getDouble("MIN_NOTA")); // <- agregado para usar minNota
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<ReportePregunta> obtenerReportePregunta() {
        List<ReportePregunta> lista = new ArrayList<>();
        String sql = "SELECT ID_PREGUNTA, PREGUNTA, TOTAL_RESPUESTAS, PORCENTAJE_ACIERTOS FROM VISTA_ESTADISTICAS_PREGUNTA";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ReportePregunta r = new ReportePregunta();
                r.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                r.setPregunta(rs.getString("PREGUNTA"));
                r.setTotalRespuestas(rs.getInt("TOTAL_RESPUESTAS"));
                r.setPorcentajeAciertos(rs.getDouble("PORCENTAJE_ACIERTOS"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReporteExamen> obtenerReporteExamen() {
        List<ReporteExamen> lista = new ArrayList<>();
        String sql = "SELECT ID_EXAMEN, NOMBRE, TOTAL_PRESENTACIONES, PROMEDIO_NOTA, MAX_NOTA, MIN_NOTA FROM VISTA_ESTADISTICAS_EXAMEN";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ReporteTema> obtenerReporteTema() {
        List<ReporteTema> lista = new ArrayList<>();
        String sql = "SELECT ID_TEMA, TEMA, TOTAL_RESPUESTAS, TOTAL_CORRECTAS, TOTAL_INCORRECTAS, PROMEDIO_NOTA, MAX_NOTA, MIN_NOTA FROM VISTA_ESTADISTICAS_TEMA";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
