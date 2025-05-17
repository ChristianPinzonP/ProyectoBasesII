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
                "e.tiempo_limite, e.id_docente, e.id_tema, e.id_grupo, t.NOMBRE AS nombre_tema, g.NOMBRE AS nombre_grupo, " +
                "e.numero_preguntas, e.modo_seleccion, e.tiempo_por_pregunta " +  // <- Agregado campos
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

                int numeroPreguntas = rs.getInt("numero_preguntas");
                String modoSeleccion = rs.getString("modo_seleccion");
                int tiempoPorPregunta = rs.getInt("tiempo_por_pregunta");

                Examen examen = new Examen(idExamen, nombre, descripcion, fechaInicio, fechaFin,
                        tiempoLimite, idDocente, idTema, idGrupo);

                examen.setNombreTema(nombreTema);
                examen.setNombreGrupo(nombreGrupo);

                // Setear los nuevos campos
                examen.setNumeroPreguntas(numeroPreguntas);
                examen.setModoSeleccion(modoSeleccion);
                examen.setTiempoPorPregunta(tiempoPorPregunta);

                listaExamenes.add(examen);
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al obtener exámenes: " + e.getMessage());
        }

        return listaExamenes;
    }


    public static int agregarExamenYRetornarId(Examen examen) {
        String sql = "{ call CREAR_EXAMEN(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, examen.getNombre());                           // p_nombre
            stmt.setString(2, examen.getDescripcion());                      // p_descripcion
            stmt.setDate(3, examen.getFechaInicio());                        // p_fecha_inicio
            stmt.setDate(4, examen.getFechaFin());                           // p_fecha_fin
            stmt.setInt(5, examen.getTiempoLimite());                        // p_tiempo_limite
            stmt.setInt(6, examen.getIdDocente());                           // p_id_docente
            stmt.setInt(7, examen.getNumeroPreguntas());                     // p_numero_preguntas
            stmt.setString(8, examen.getModoSeleccion());                    // p_modo_seleccion
            stmt.setInt(9, examen.getTiempoPorPregunta());                   // p_tiempo_por_pregunta
            stmt.setInt(10, examen.getIdTema());                             // p_id_tema
            stmt.setDouble(11, 3.0);                                         // p_nota_minima_aprob (puedes usar examen.getNotaMinimaAprobacion() si tienes)
            stmt.setInt(12, examen.getIdGrupo());                            // p_id_grupo

            stmt.registerOutParameter(13, Types.VARCHAR);                    // p_estado OUT
            stmt.registerOutParameter(14, Types.INTEGER);                    // p_id_generado OUT

            stmt.execute();

            String estado = stmt.getString(13);
            if ("OK".equalsIgnoreCase(estado)) {
                return stmt.getInt(14);
            } else {
                System.out.println("❌ Error en PL/SQL: Estado = " + estado);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar examen con procedimiento: " + e.getMessage());
        }
        return -1;
    }

    public static boolean editarExamen(Examen examen) {
        String sql = "UPDATE EXAMEN SET nombre = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, " +
                "tiempo_limite = ?, id_docente = ?, id_tema = ?, id_grupo = ?, numero_preguntas = ?, modo_seleccion = ?, tiempo_por_pregunta = ? WHERE id_examen = ?";

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
            stmt.setInt(12, examen.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar el examen: " + e.getMessage());
            return false;
        }
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
        String sqlEliminarRelaciones = "DELETE FROM EXAMEN_PREGUNTA WHERE id_examen = ?";
        String sqlEliminarExamen = "DELETE FROM EXAMEN WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtRel = conn.prepareStatement(sqlEliminarRelaciones);
                 PreparedStatement stmtExamen = conn.prepareStatement(sqlEliminarExamen)) {

                // Eliminar relaciones pregunta-examen
                stmtRel.setInt(1, idExamen);
                stmtRel.executeUpdate();

                // Eliminar examen
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
