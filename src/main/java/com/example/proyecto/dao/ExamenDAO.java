package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Examen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenDAO {

    // Agregar este nuevo método en ExamenDAO.java

    public static List<Examen> obtenerExamenesPorDocente(int idDocente) {
        List<Examen> listaExamenes = new ArrayList<>();
        String sql = "SELECT e.id_examen, e.nombre, e.descripcion, e.fecha_inicio, e.fecha_fin, " +
                "e.tiempo_limite, e.id_docente, e.id_tema, e.id_grupo, t.NOMBRE AS nombre_tema, g.NOMBRE AS nombre_grupo, " +
                "e.numero_preguntas, e.modo_seleccion, e.tiempo_por_pregunta, e.intentos_permitidos " +
                "FROM EXAMEN e " +
                "LEFT JOIN TEMA t ON e.id_tema = t.id_tema " +
                "LEFT JOIN GRUPO g ON e.id_grupo = g.id_grupo " +
                "WHERE e.id_docente = ? " +
                "ORDER BY e.id_examen DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idExamen = rs.getInt("id_examen");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Date fechaInicio = rs.getDate("fecha_inicio");
                Date fechaFin = rs.getDate("fecha_fin");
                int tiempoLimite = rs.getInt("tiempo_limite");
                int idDocenteResult = rs.getInt("id_docente");
                int idTema = rs.getInt("id_tema");
                String nombreTema = rs.getString("nombre_tema");
                int idGrupo = rs.getInt("id_grupo");
                String nombreGrupo = rs.getString("nombre_grupo");

                int numeroPreguntas = rs.getInt("numero_preguntas");
                String modoSeleccion = rs.getString("modo_seleccion");
                int tiempoPorPregunta = rs.getInt("tiempo_por_pregunta");
                int intentosPermitidos = rs.getInt("intentos_permitidos");

                Examen examen = new Examen(idExamen, nombre, descripcion, fechaInicio, fechaFin,
                        tiempoLimite, idDocenteResult, idTema, idGrupo);

                examen.setNombreTema(nombreTema);
                examen.setNombreGrupo(nombreGrupo);
                examen.setNumeroPreguntas(numeroPreguntas);
                examen.setModoSeleccion(modoSeleccion);
                examen.setTiempoPorPregunta(tiempoPorPregunta);
                examen.setIntentosPermitidos(intentosPermitidos);

                listaExamenes.add(examen);
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al obtener exámenes del docente " + idDocente + ": " + e.getMessage());
        }

        return listaExamenes;
    }

    public static boolean agregarExamen(Examen examen) {
        String sql = "INSERT INTO EXAMEN (NOMBRE, DESCRIPCION, FECHA_INICIO, FECHA_FIN, TIEMPO_LIMITE, ID_DOCENTE, ID_TEMA, ID_GRUPO, MODO_SELECCION, TIEMPO_POR_PREGUNTA, NUMERO_PREGUNTAS, INTENTOS_PERMITIDOS) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, examen.getNombre());
            stmt.setString(2, examen.getDescripcion());
            stmt.setDate(3, examen.getFechaInicio());
            stmt.setDate(4, examen.getFechaFin());
            stmt.setInt(5, examen.getTiempoLimite());
            stmt.setInt(6, examen.getIdDocente());
            stmt.setInt(7, examen.getIdTema());
            stmt.setInt(8, examen.getIdGrupo());
            stmt.setString(9, examen.getModoSeleccion());
            stmt.setInt(10, examen.getTiempoPorPregunta());
            stmt.setInt(11, examen.getNumeroPreguntas());
            stmt.setInt(12, examen.getIntentosPermitidos());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean editarExamen(Examen examen) {
        String sql = "UPDATE EXAMEN SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, " +
                "tiempo_limite = ?, id_docente = ?, id_tema = ?, id_grupo = ?, numero_preguntas = ?, modo_seleccion = ?, tiempo_por_pregunta = ?, intentos_permitidos = ? WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, examen.getNombre());
            stmt.setString(2, examen.getDescripcion());
            stmt.setDate(3, examen.getFechaInicio());
            stmt.setDate(4, examen.getFechaFin());
            stmt.setInt(5, examen.getTiempoLimite());
            stmt.setInt(6, examen.getIdDocente());
            stmt.setInt(7, examen.getIdTema());
            stmt.setInt(8, examen.getIdGrupo());
            stmt.setInt(9, examen.getNumeroPreguntas());
            stmt.setString(10, examen.getModoSeleccion());
            stmt.setInt(11, examen.getTiempoPorPregunta());
            stmt.setInt(12, examen.getIntentosPermitidos());
            stmt.setInt(13, examen.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar el examen: " + e.getMessage());
            return false;
        }
    }

    public static List<Examen> obtenerExamenesPorGrupo(int idGrupo) {
        List<Examen> listaExamenes = new ArrayList<>();
        String sql = "SELECT id_examen, nombre, descripcion, fecha_inicio, fecha_fin, tiempo_limite, id_docente, id_tema, id_grupo FROM EXAMEN WHERE id_grupo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGrupo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Examen examen = new Examen(
                        rs.getInt("id_examen"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getInt("tiempo_limite"),
                        rs.getInt("id_docente"),
                        rs.getInt("id_tema"),
                        rs.getInt("id_grupo")
                );
                listaExamenes.add(examen);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaExamenes;
    }

    public static boolean eliminarExamen(int idExamen) {
        String sqlEliminarRelaciones = "DELETE FROM EXAMEN_PREGUNTA WHERE id_examen = ?";
        String sqlEliminarExamen = "DELETE FROM EXAMEN WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtRel = conn.prepareStatement(sqlEliminarRelaciones);
                 PreparedStatement stmtExamen = conn.prepareStatement(sqlEliminarExamen)) {

                stmtRel.setInt(1, idExamen);
                stmtRel.executeUpdate();

                stmtExamen.setInt(1, idExamen);
                int rows = stmtExamen.executeUpdate();

                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}