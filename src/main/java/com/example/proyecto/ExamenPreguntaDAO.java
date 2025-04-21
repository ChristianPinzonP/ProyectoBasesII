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

    public static List<Integer> obtenerPreguntasPorExamen(int idExamen) {
        List<Integer> preguntas = new ArrayList<>();
        String query = "SELECT id_pregunta FROM Examen_Pregunta WHERE id_examen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                preguntas.add(rs.getInt("id_pregunta"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preguntas;
    }
    //Metodo para obtener las preguntas del examen
    public static List<Pregunta> obtenerPreguntasDeExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT p.id_pregunta, p.texto, p.tipo, p.id_banco, p.id_tema " +
                "FROM Pregunta p " +
                "JOIN Examen_Pregunta ep ON p.id_pregunta = ep.id_pregunta " +
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
                String nombreTema = rs.getString("inombreTema");
                boolean publica = rs.getBoolean("publica");

                // 🔥 Obtener las opciones de respuesta para la pregunta
                List<OpcionRespuesta> opciones = obtenerOpcionesRespuesta(idPregunta);

                // ✅ Crear el objeto Pregunta con la lista de opciones
                Pregunta pregunta = new Pregunta(
                        rs.getInt("ID_PREGUNTA"),
                        rs.getString("TEXTO"),
                        rs.getString("TIPO"),
                        rs.getInt("ID_BANCO"),
                        rs.getInt("ID_TEMA")
                );

                preguntas.add(pregunta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preguntas;
    }

//Metodo para obtener las opciones de respuesta
    private static List<OpcionRespuesta> obtenerOpcionesRespuesta(int idPregunta) {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        String sql = "SELECT texto, correcta FROM RESPUESTA WHERE id_pregunta = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String texto = rs.getString("texto");
                boolean correcta = rs.getBoolean("correcta");
                opciones.add(new OpcionRespuesta(texto, correcta));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return opciones;
    }
}

