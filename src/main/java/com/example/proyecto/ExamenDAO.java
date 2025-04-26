package com.example.proyecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExamenDAO {

// Metodo para obtener los examenes para la tabla
    public static List<Examen> obtenerTodosLosExamenes() {
        List<Examen> listaExamenes = new ArrayList<>();
        String sql = "SELECT e.id_examen, e.nombre, e.descripcion, e.fecha_inicio, e.fecha_fin, e.tiempo_limite, e.id_docente, e.id_tema, t.NOMBRE as NOMBRE_TEMA " +
                "FROM EXAMEN e " +
                "LEFT JOIN TEMA t ON e.ID_TEMA = t.ID_TEMA " +
                "ORDER BY e.id_examen DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement pstmt = conn.createStatement();
             ResultSet rs = pstmt.executeQuery(sql)) {

            while (rs.next()) {
                int idExamen = rs.getInt("id_examen");
                String nombreExamen = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Date fechaInicio = rs.getDate("fecha_inicio");
                Date fechaFin = rs.getDate("fecha_fin");
                int tiempoLimite = rs.getInt("tiempo_limite");
                int idDocente = rs.getInt("id_docente");
                int idTema = rs.getInt("id_tema");
                String nombreTema = rs.getString("NOMBRE_TEMA");

                Examen examen = new Examen(idExamen, nombreExamen, descripcion, fechaInicio, fechaFin, tiempoLimite, idDocente, idTema);
                examen.setNombre(nombreTema);
                listaExamenes.add(examen);

                //System.out.println("✅ Pregunta obtenida: ID=" + idExamen + "nombre:" + nombreExamen + ", descripcion=" + descripcion + ", Tema=" + nombreTema);
            }
        } catch (SQLException e) {
            System.out.println("🚨 Error al obtener exámenes: " + e.getMessage());
        }

        return listaExamenes;
    }

    //Metodo para editar los examenes
    public static boolean editarExamen(Examen examen) {
        String sql = "UPDATE EXAMEN SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, " +
                "tiempo_limite = ?, id_docente = ?, id_tema = ? WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, examen.getNombre());
            stmt.setString(2, examen.getDescripcion());
            stmt.setDate(3, examen.getFechaInicio());
            stmt.setDate(4, examen.getFechaFin());
            stmt.setInt(5, examen.getTiempoLimite());
            stmt.setInt(6, examen.getIdDocente());
            stmt.setInt(7, examen.getIdTema()); // ✅ actualizar tema también
            stmt.setInt(8, examen.getId());     // ID del examen a modificar

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar el examen: " + e.getMessage());
            return false;
        }
    }



    // Método para insertar un nuevo examen
    public static boolean agregarExamen(Examen examen) {
        String sql = "INSERT INTO EXAMEN (id_examen, nombre, descripcion, fecha_inicio, fecha_fin, tiempo_limite, id_docente, id_tema) " +
                "VALUES (SEQ_EXAMEN.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Deshabilitar autocommit para manejar transacciones manualmente
            conn.setAutoCommit(false);

            pstmt.setString(1, examen.getNombre());
            pstmt.setString(2, examen.getDescripcion());
            pstmt.setDate(3, examen.getFechaInicio());
            pstmt.setDate(4, examen.getFechaFin());
            pstmt.setInt(5, examen.getTiempoLimite());
            pstmt.setInt(6, examen.getIdDocente());
            pstmt.setInt(7, examen.getIdTema()); // 👉 Aquí va el ID_TEMA

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                conn.commit(); // 🔥 Confirmar los cambios
                System.out.println("✅ Examen agregado correctamente.");
                return true;
            } else {
                conn.rollback(); // Revertir cambios si no se insertó nada
                System.out.println("❌ No se pudo agregar el examen.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("🚨 Error al agregar examen: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // Método para eliminar un examen
    public static boolean eliminarExamen(int idExamen) {
        String sql = "DELETE FROM EXAMEN WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idExamen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
