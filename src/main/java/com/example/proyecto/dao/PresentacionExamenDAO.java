package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PresentacionExamenDAO {

    public static int registrarPresentacion(int idExamen, int idEstudiante) {
        String sqlCall = "{ call PKG_PRESENTACION_EXAMEN.CREAR_PRESENTACION_EXAMEN(?, ?) }";
        String sqlGetId = "SELECT SEQ_PRESENTACION_EXAMEN.CURRVAL FROM DUAL";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall);
             PreparedStatement stmtId = conn.prepareStatement(sqlGetId)) {

            stmt.setInt(1, idExamen);
            stmt.setInt(2, idEstudiante);
            stmt.execute();

            ResultSet rs = stmtId.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error al crear presentación: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static void registrarRespuesta(int idPresentacion, int idPregunta, int idOpcion) {
        String sqlCall = "{ call REGISTRAR_RESPUESTA(?, ?, ?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, idPresentacion);
            stmt.setInt(2, idPregunta);
            stmt.setInt(3, idOpcion);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void calificarAutomaticamente(int idPresentacion) {
        String sqlCall = "{ call PKG_PRESENTACION_EXAMEN.CALIFICAR_EXAMEN_AUTOMATICO(?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, idPresentacion);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();

            String estado = stmt.getString(2);
            System.out.println("Estado de calificación automática: " + estado);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double obtenerCalificacion(int idPresentacion) {
        String sqlCall = "SELECT PKG_PRESENTACION_EXAMEN.OBTENER_CALIFICACION(?) FROM DUAL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCall)) {

            stmt.setInt(1, idPresentacion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int contarIntentosRealizados(int idEstudiante, int idExamen) {
        String sqlCall = "SELECT PKG_PRESENTACION_EXAMEN.CONTAR_INTENTOS_REALIZADOS(?, ?) FROM DUAL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCall)) {

            stmt.setInt(1, idEstudiante);
            stmt.setInt(2, idExamen);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt(1);
                System.out.println(">> Total finalizados: " + total);
                return total;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int obtenerIntentosPermitidos(int idExamen) {
        String sqlCall = "SELECT PKG_PRESENTACION_EXAMEN.OBTENER_INTENTOS_PERMITIDOS(?) FROM DUAL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCall)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // valor por defecto si algo falla
    }

    public static boolean examenTienePresentaciones(int idExamen) {
        String sqlCall = "SELECT PKG_PRESENTACION_EXAMEN.EXAMEN_TIENE_PRESENTACIONES(?) FROM DUAL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCall)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(">> Examen " + idExamen + " tiene " + count + " presentaciones");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, Integer> obtenerEstadisticasPresentaciones(int idExamen) {
        Map<String, Integer> estadisticas = new HashMap<>();
        String sqlCall = "{? = call PKG_PRESENTACION_EXAMEN.OBTENER_ESTADISTICAS_PRESENTACIONES(?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            // Registrar parámetros de salida
            stmt.registerOutParameter(1, Types.VARCHAR); // Estado de retorno
            stmt.setInt(2, idExamen);                    // ID del examen (entrada)
            stmt.registerOutParameter(3, Types.INTEGER); // Total (salida)
            stmt.registerOutParameter(4, Types.INTEGER); // Finalizados (salida)
            stmt.registerOutParameter(5, Types.INTEGER); // En progreso (salida)

            // Ejecutar la función
            stmt.execute();

            // Obtener resultados
            String estado = stmt.getString(1);
            if ("OK".equals(estado) || "NO_DATA".equals(estado)) {
                estadisticas.put("total", stmt.getInt(3));
                estadisticas.put("finalizados", stmt.getInt(4));
                estadisticas.put("en_progreso", stmt.getInt(5));
            } else {
                // En caso de error, devolver valores por defecto
                estadisticas.put("total", 0);
                estadisticas.put("finalizados", 0);
                estadisticas.put("en_progreso", 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // En caso de excepción, devolver valores por defecto
            estadisticas.put("total", 0);
            estadisticas.put("finalizados", 0);
            estadisticas.put("en_progreso", 0);
        }

        return estadisticas;
    }

}
