package com.example.proyecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDAO {

    public static boolean agregarPregunta(Pregunta pregunta) {
        String sql = "INSERT INTO PREGUNTA (ID_PREGUNTA, TEXTO, TIPO, ID_BANCO) VALUES (SEQ_PREGUNTA.NEXTVAL, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID_PREGUNTA"})) {

            stmt.setString(1, pregunta.getTexto());
            stmt.setString(2, pregunta.getTipo());
            stmt.setInt(3, pregunta.getIdBanco());

            int filasInsertadas = stmt.executeUpdate();

            if (filasInsertadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idPregunta = rs.getInt(1);
                        System.out.println("✅ Pregunta insertada con ID: " + idPregunta);

                        if (!pregunta.getOpciones().isEmpty()) {
                            return agregarOpcionesRespuesta(idPregunta, pregunta.getTipo(), pregunta.getOpciones());
                        }
                    }
                }
            }
            return filasInsertadas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la pregunta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean agregarOpcionesRespuesta(int idPregunta, String tipoPregunta, List<OpcionRespuesta> opciones) {
        String sql = "INSERT INTO RESPUESTA (ID_RESPUESTA, ID_PREGUNTA, TEXTO, ES_CORRECTA) VALUES (SEQ_RESPUESTA.NEXTVAL, ?, ?, ?)";

        if (opciones.isEmpty()) {
            System.out.println("⚠ No hay opciones para guardar en la pregunta ID: " + idPregunta);
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("✅ Conexión establecida para guardar respuestas.");
            System.out.println("Intentando guardar respuestas para ID Pregunta: " + idPregunta);
            System.out.println("Tipo de pregunta detectado: " + tipoPregunta);

            for (OpcionRespuesta opcion : opciones) {
                System.out.println("Insertando respuesta: " + opcion.getTexto() + " - Correcta: " + opcion.isCorrecta());

                stmt.setInt(1, idPregunta);
                stmt.setString(2, opcion.getTexto());
                stmt.setString(3, opcion.isCorrecta() ? "S" : "N");  // Se asegura que ES_CORRECTA sea "S" o "N"
                stmt.addBatch();
            }

            int[] filasInsertadas = stmt.executeBatch();
            System.out.println("✅ Respuestas insertadas correctamente. Total: " + filasInsertadas.length);
            return filasInsertadas.length > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar respuestas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<OpcionRespuesta> obtenerOpcionesDePregunta(int idPregunta) {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        String sql = "SELECT ID_RESPUESTA, TEXTO, ES_CORRECTA FROM RESPUESTA WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idRespuesta = rs.getInt("ID_RESPUESTA");
                String texto = rs.getString("TEXTO");
                String esCorrectaStr = rs.getString("ES_CORRECTA");

                boolean esCorrecta = "S".equalsIgnoreCase(esCorrectaStr);  // Garantiza la correcta interpretación

                opciones.add(new OpcionRespuesta(idRespuesta, texto, esCorrecta));
                System.out.println("✅ Respuesta obtenida: " + texto + " - Correcta: " + esCorrecta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return opciones;
    }

    public static List<Pregunta> obtenerTodasLasPreguntas() {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "SELECT ID_PREGUNTA, TEXTO, TIPO, ID_BANCO FROM PREGUNTA ORDER BY ID_PREGUNTA DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idPregunta = rs.getInt("ID_PREGUNTA");
                String texto = rs.getString("TEXTO");
                String tipo = rs.getString("TIPO");
                int idBanco = rs.getInt("ID_BANCO");

                List<OpcionRespuesta> opciones = obtenerOpcionesDePregunta(idPregunta);

                Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idBanco, opciones);
                listaPreguntas.add(pregunta);

                System.out.println("✅ Pregunta obtenida: ID=" + idPregunta + ", Texto=" + texto + ", Tipo=" + tipo + ", ID Banco=" + idBanco);
                System.out.println("✅ Opciones encontradas: " + opciones.size());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaPreguntas;
    }

    public static boolean actualizarPregunta(int idPregunta, String nuevoTexto, String nuevoTipo, int nuevoIdBanco, List<OpcionRespuesta> nuevasOpciones) {
        String sql = "UPDATE PREGUNTA SET TEXTO = ?, TIPO = ?, ID_BANCO = ? WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoTexto);
            stmt.setString(2, nuevoTipo);
            stmt.setInt(3, nuevoIdBanco);
            stmt.setInt(4, idPregunta);

            int filasActualizadas = stmt.executeUpdate();

            if (filasActualizadas > 0) {
                eliminarOpcionesDePregunta(idPregunta);
                agregarOpcionesRespuesta(idPregunta, nuevoTipo, nuevasOpciones);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean eliminarPregunta(int idPregunta) {
        eliminarOpcionesDePregunta(idPregunta);

        String sql = "DELETE FROM PREGUNTA WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean eliminarOpcionesDePregunta(int idPregunta) {
        String sql = "DELETE FROM RESPUESTA WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            int filasEliminadas = stmt.executeUpdate();
            System.out.println("✅ Opciones eliminadas para la pregunta ID: " + idPregunta + ". Total: " + filasEliminadas);
            return filasEliminadas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar opciones de respuesta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}


