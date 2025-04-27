package com.example.proyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamenPreguntaDAO {

    public static boolean asignarPreguntaAExamen(int idExamen, int idPregunta) {
        String sql = "INSERT INTO Examen_Pregunta (id_examen, id_pregunta) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idExamen);
            stmt.setInt(2, idPregunta);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al asignar pregunta: " + e.getMessage());
            return false;
        }
    }

    //Metodo para obtener las preguntas del examen
    public static List<Pregunta> obtenerPreguntasDeExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT p.id_pregunta, p.texto, p.tipo, p.id_banco, p.id_tema, t.nombre as nombre_tema " +
                "FROM Pregunta p " +
                "JOIN Examen_Pregunta ep ON p.id_pregunta = ep.id_pregunta " +
                "LEFT JOIN TEMA t ON p.id_tema = t.id_tema " +
                "WHERE ep.id_examen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idPregunta = rs.getInt("id_pregunta");
                String texto = rs.getString("texto");
                String tipo = rs.getString("tipo");
                int idBanco = rs.getInt("id_banco");
                int idTema = rs.getInt("id_tema");
                String nombreTema = rs.getString("nombre_tema");

                // 🔥 Obtener las opciones de respuesta para la pregunta
                List<OpcionRespuesta> opciones = obtenerOpcionesRespuesta(idPregunta);

                // ✅ Crear el objeto Pregunta con la lista de opciones
                Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idBanco, idTema, opciones);
                pregunta.setNombreTema(nombreTema);

                preguntas.add(pregunta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preguntas;
    }

    public static boolean eliminarPreguntaDeExamen(int idExamen, int idPregunta) {
        String sql = "DELETE FROM EXAMEN_PREGUNTA WHERE ID_EXAMEN = ? AND ID_PREGUNTA = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idExamen);
            stmt.setInt(2, idPregunta);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar pregunta del examen: " + e.getMessage());
            return false;
        }
    }


    //Metodo para obtener las opciones de respuesta
    private static List<OpcionRespuesta> obtenerOpcionesRespuesta(int idPregunta) {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        String sql = "SELECT id_respuesta, texto, ES_CORRECTA FROM RESPUESTA WHERE id_pregunta = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idRespuesta = rs.getInt("id_respuesta");
                String texto = rs.getString("texto");
                String esCorrectaStr = rs.getString("ES_CORRECTA");

                // Convertir "S" o "N" a booleano
                boolean esCorrecta = "S".equalsIgnoreCase(esCorrectaStr);

                opciones.add(new OpcionRespuesta(idRespuesta, texto, esCorrecta));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return opciones;
    }
}

