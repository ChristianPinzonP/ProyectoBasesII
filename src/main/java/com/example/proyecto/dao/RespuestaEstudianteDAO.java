package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RespuestaEstudianteDAO {

    public static boolean guardarRespuesta(int idPresentacion, int idPregunta, int idRespuesta, String textoLibre) {
        String sql = "INSERT INTO RESPUESTA_ESTUDIANTE (ID_RESPUESTA_ESTUDIANTE, ID_PRESENTACION, ID_PREGUNTA, ID_RESPUESTA) " +
                "VALUES (SEQ_RESPUESTA_ESTUDIANTE.NEXTVAL, ?, ?, ?)";

        boolean esCorrecta = false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPresentacion);
            stmt.setInt(2, idPregunta);

            if (idRespuesta > 0) {
                stmt.setInt(3, idRespuesta);
                esCorrecta = verificarCorrectitud(idRespuesta);
            } else {
                stmt.setNull(3, Types.INTEGER);
                esCorrecta = false;
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return esCorrecta;
    }

    private static boolean verificarCorrectitud(int idRespuesta) {
        String sql = "SELECT ES_CORRECTA FROM RESPUESTA WHERE ID_RESPUESTA = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRespuesta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "S".equalsIgnoreCase(rs.getString("ES_CORRECTA"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


