package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static void actualizarCalificacion(int idPresentacion, double nota) {
        String sql = "UPDATE PRESENTACION_EXAMEN SET CALIFICACION = ? WHERE ID_PRESENTACION = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nota);
            stmt.setInt(2, idPresentacion);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
