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
    // Método para insertar respuestas de selección (opción múltiple o VF)
    public static void insertarRespuestaSeleccion(int idPresentacion, int idPregunta, int idRespuesta, Connection conn) throws SQLException {
        String sql = "INSERT INTO RESPUESTA_ESTUDIANTE (ID_RESPUESTA_ESTUDIANTE, ID_PRESENTACION, ID_PREGUNTA, ID_RESPUESTA) " +
                "VALUES (SEQ_RESPUESTA_ESTUDIANTE.NEXTVAL, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPresentacion);
            ps.setInt(2, idPregunta);
            ps.setInt(3, idRespuesta);
            ps.executeUpdate();
        }
    }

    public static int insertarRespuestaTexto(int idPresentacion, int idPregunta, String texto, Connection conn) throws SQLException {
        // 1. Verificar si ya existe esta respuesta
        PreparedStatement psBuscar = conn.prepareStatement(
                "SELECT ID_RESPUESTA FROM RESPUESTA WHERE ID_PREGUNTA = ? AND TEXTO = ?");
        psBuscar.setInt(1, idPregunta);
        psBuscar.setString(2, texto);
        ResultSet rs = psBuscar.executeQuery();
        if (rs.next()) {
            return rs.getInt("ID_RESPUESTA");
        }
        rs.close();
        psBuscar.close();

        // 2. Obtener el siguiente ID de la secuencia manualmente
        PreparedStatement psSeq = conn.prepareStatement("SELECT SEQ_RESPUESTA.NEXTVAL FROM DUAL");
        ResultSet rsSeq = psSeq.executeQuery();
        int nuevoId = -1;
        if (rsSeq.next()) {
            nuevoId = rsSeq.getInt(1);
        }
        rsSeq.close();
        psSeq.close();

        // 3. Insertar la nueva respuesta con el ID obtenido
        PreparedStatement psInsert = conn.prepareStatement(
                "INSERT INTO RESPUESTA (ID_RESPUESTA, ID_PREGUNTA, TEXTO, ES_CORRECTA) VALUES (?, ?, ?, 'N')");
        psInsert.setInt(1, nuevoId);
        psInsert.setInt(2, idPregunta);
        psInsert.setString(3, texto);
        psInsert.executeUpdate();
        psInsert.close();

        return nuevoId;
    }

}


