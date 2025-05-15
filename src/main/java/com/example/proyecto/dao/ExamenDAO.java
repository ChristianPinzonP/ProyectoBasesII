package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Examen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenDAO {

    public static List<Examen> obtenerTodosLosExamenes() {
        List<Examen> listaExamenes = new ArrayList<>();
        String sql = "SELECT e.id_examen, e.nombre, e.descripcion, e.fecha_inicio, e.fecha_fin, " +
                "e.tiempo_limite, e.id_docente, e.id_tema, e.id_grupo, t.NOMBRE AS nombre_tema, g.NOMBRE AS nombre_grupo " +
                "FROM EXAMEN e " +
                "LEFT JOIN TEMA t ON e.id_tema = t.id_tema " +
                "LEFT JOIN GRUPO g ON e.id_grupo = g.id_grupo " +
                "ORDER BY e.id_examen DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idExamen = rs.getInt("id_examen");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Date fechaInicio = rs.getDate("fecha_inicio");
                Date fechaFin = rs.getDate("fecha_fin");
                int tiempoLimite = rs.getInt("tiempo_limite");
                int idDocente = rs.getInt("id_docente");
                int idTema = rs.getInt("id_tema");
                String nombreTema = rs.getString("nombre_tema");
                int idGrupo = rs.getInt("id_grupo");
                String nombreGrupo = rs.getString("nombre_grupo");

                Examen examen = new Examen(idExamen, nombre, descripcion, fechaInicio, fechaFin,
                        tiempoLimite, idDocente, idTema, idGrupo);
                examen.setNombre(nombreTema);
                examen.setNombreGrupo(nombreGrupo);
                listaExamenes.add(examen);
                //System.out.println("✅ Pregunta obtenida: ID=" + idExamen + "nombre:" + nombreExamen + ", descripcion=" + descripcion + ", Tema=" + nombreTema);
            }
        } catch (SQLException e) {
            System.out.println("\uD83D\uDEA8 Error al obtener exámenes: " + e.getMessage());
        }

        return listaExamenes;
    }

    public static boolean editarExamen(Examen examen) {
        String sql = "UPDATE EXAMEN SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, " +
                "tiempo_limite = ?, id_docente = ?, id_tema = ?, id_grupo = ? WHERE id_examen = ?";

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
            stmt.setInt(9, examen.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar el examen: " + e.getMessage());
            return false;
        }
    }

    public static boolean agregarExamen(Examen examen) {
        String sql = "INSERT INTO EXAMEN (id_examen, nombre, descripcion, fecha_inicio, fecha_fin, " +
                "tiempo_limite, id_docente, id_tema, id_grupo) " +
                "VALUES (SEQ_EXAMEN.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            stmt.setString(1, examen.getNombre());
            stmt.setString(2, examen.getDescripcion());
            stmt.setDate(3, examen.getFechaInicio());
            stmt.setDate(4, examen.getFechaFin());
            stmt.setInt(5, examen.getTiempoLimite());
            stmt.setInt(6, examen.getIdDocente());
            stmt.setInt(7, examen.getIdTema());
            stmt.setInt(8, examen.getIdGrupo());

            if (stmt.executeUpdate() > 0) {
                conn.commit();
                System.out.println("✅ Examen agregado correctamente.");
                return true;
            } else {
                conn.rollback();
                System.out.println("❌ No se pudo agregar el examen.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("\uD83D\uDEA8 Error al agregar examen: " + e.getMessage());
            return false;
        }
    }

    public static int agregarExamenYRetornarId(Examen examen) {
        String sql = "INSERT INTO EXAMEN (id_examen, nombre, descripcion, fecha_inicio, fecha_fin, " +
                "tiempo_limite, id_docente, id_tema, id_grupo) " +
                "VALUES (SEQ_EXAMEN.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] keys = {"id_examen"};

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, keys)) {

            stmt.setString(1, examen.getNombre());
            stmt.setString(2, examen.getDescripcion());
            stmt.setDate(3, examen.getFechaInicio());
            stmt.setDate(4, examen.getFechaFin());
            stmt.setInt(5, examen.getTiempoLimite());
            stmt.setInt(6, examen.getIdDocente());
            stmt.setInt(7, examen.getIdTema());
            stmt.setInt(8, examen.getIdGrupo());

            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar y retornar ID: " + e.getMessage());
        }

        return -1;
    }

    public static List<Examen> obtenerExamenesPorGrupo(int idGrupo) {
        List<Examen> listaExamenes = new ArrayList<>();
        String sql = "SELECT id_examen, nombre, descripcion, fecha_inicio, fecha_fin, tiempo_limite, id_docente, id_tema, id_grupo " +
                "FROM EXAMEN WHERE id_grupo = ?";

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
        String sql = "DELETE FROM EXAMEN WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
