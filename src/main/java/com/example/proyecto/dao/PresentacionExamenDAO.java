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

    public static void calificarAutomaticamente(int idPresentacion) {
        String sql = "{ call CALIFICAR_EXAMEN_AUTOMATICO(?, ?) }";
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
}
