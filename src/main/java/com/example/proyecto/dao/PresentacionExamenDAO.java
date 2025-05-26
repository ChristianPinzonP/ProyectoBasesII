package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class PresentacionExamenDAO {

    public static int registrarPresentacion(int idExamen, int idEstudiante) {
        String sql = "INSERT INTO PRESENTACION_EXAMEN (ID_PRESENTACION, ID_EXAMEN, ID_ESTUDIANTE, FECHA_PRESENTACION) " +
                "VALUES (SEQ_PRESENTACION_EXAMEN.NEXTVAL, ?, ?, ?)";
        String[] keys = {"ID_PRESENTACION"};

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, keys)) {

            stmt.setInt(1, idExamen);
            stmt.setInt(2, idEstudiante);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void registrarRespuesta(int idPresentacion, int idPregunta, int idOpcion) {
        String sql = "{ call REGISTRAR_RESPUESTA(?, ?, ?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPresentacion);
            stmt.setInt(2, idPregunta);
            stmt.setInt(3, idOpcion);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registrarRespuestaYCalificar(int idEstudiante, int idExamen, int idPregunta, int idRespuesta) {
        int idPresentacion = obtenerIdPresentacion(idEstudiante, idExamen);

        // Registrar desde procedimiento PL/SQL
        registrarRespuesta(idPresentacion, idPregunta, idRespuesta);

        // Calificar automáticamente
        calificarAutomaticamente(idPresentacion);
    }

    public static void finalizarExamen(int idPresentacion) {
        String sql = "{ call PKG_PRESENTACION_EXAMEN.FINALIZAR_EXAMEN(?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPresentacion);
            stmt.registerOutParameter(2, Types.VARCHAR);

            stmt.execute();

            String estado = stmt.getString(2);
            System.out.println("Estado de finalización del examen: " + estado);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void calificarAutomaticamente(int idPresentacion) {
        String sql = "{ call PKG_PRESENTACION_EXAMEN.CALIFICAR_EXAMEN_AUTOMATICO(?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

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
        String sql = "SELECT CALIFICACION FROM PRESENTACION_EXAMEN WHERE ID_PRESENTACION = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPresentacion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("CALIFICACION");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int obtenerIdPresentacion(int idEstudiante, int idExamen) {
        String sql = "SELECT ID_PRESENTACION FROM PRESENTACION_EXAMEN WHERE ID_ESTUDIANTE = ? AND ID_EXAMEN = ? ORDER BY ID_PRESENTACION DESC FETCH FIRST 1 ROWS ONLY";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEstudiante);
            stmt.setInt(2, idExamen);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_PRESENTACION");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean examenFinalizado(int idPresentacion) {
        String sql = "SELECT ESTADO FROM PRESENTACION_EXAMEN WHERE ID_PRESENTACION = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPresentacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String estado = rs.getString("ESTADO");
                return "FINALIZADO".equalsIgnoreCase(estado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int contarIntentosRealizados(int idEstudiante, int idExamen) {
        String sql = "SELECT COUNT(*) AS TOTAL FROM PRESENTACION_EXAMEN WHERE ID_ESTUDIANTE = ? AND ID_EXAMEN = ? AND UPPER(ESTADO) = 'FINALIZADO'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEstudiante);
            stmt.setInt(2, idExamen);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("TOTAL");
                System.out.println(">> Total finalizados: " + total);
                return total;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int obtenerIntentosPermitidos(int idExamen) {
        String sql = "SELECT INTENTOS_PERMITIDOS FROM EXAMEN WHERE ID_EXAMEN = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("INTENTOS_PERMITIDOS");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // valor por defecto si algo falla
    }


}
