package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Examen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenDAO {

    public static List<Examen> obtenerExamenesPorDocente(int idDocente) {
        List<Examen> listaExamenes = new ArrayList<>();
        String sqlCall = "{call PKG_EXAMEN.OBTENER_EXAMENES_POR_DOCENTE(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, idDocente);
            stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);

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
        String sqlCall = "{call PKG_EXAMEN.AGREGAR_EXAMEN(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

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
            stmt.registerOutParameter(13, Types.INTEGER);

            stmt.execute();

            int resultado = stmt.getInt(13);
            return resultado > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean editarExamen(Examen examen) {
        String sqlCall = "{call PKG_EXAMEN.EDITAR_EXAMEN(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, examen.getId());
            stmt.setString(2, examen.getNombre());
            stmt.setString(3, examen.getDescripcion());
            stmt.setDate(4, examen.getFechaInicio());
            stmt.setDate(5, examen.getFechaFin());
            stmt.setInt(6, examen.getTiempoLimite());
            stmt.setInt(7, examen.getIdDocente());
            stmt.setInt(8, examen.getIdTema());
            stmt.setInt(9, examen.getIdGrupo());
            stmt.setInt(10, examen.getNumeroPreguntas());
            stmt.setString(11, examen.getModoSeleccion());
            stmt.setInt(12, examen.getTiempoPorPregunta());
            stmt.setInt(13, examen.getIntentosPermitidos());
            stmt.registerOutParameter(14, Types.INTEGER);

            stmt.execute();

            int resultado = stmt.getInt(14);
            return resultado > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar el examen: " + e.getMessage());
            return false;
        }
    }

    public static List<Examen> obtenerExamenesPorGrupo(int idGrupo) {
        List<Examen> listaExamenes = new ArrayList<>();
        String sqlCall = "{call PKG_EXAMEN.OBTENER_EXAMENES_POR_GRUPO(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, idGrupo);
            stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);

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
        String sqlCall = "{call PKG_EXAMEN.ELIMINAR_EXAMEN(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, idExamen);
            stmt.registerOutParameter(2, Types.INTEGER);

            stmt.execute();

            int resultado = stmt.getInt(2);
            return resultado > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}