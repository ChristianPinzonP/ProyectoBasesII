package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Pregunta;
import oracle.jdbc.internal.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenPreguntaDAO {

    public static boolean asignarPreguntaAExamen(int idExamen, int idPregunta, double valorNota) {
        String sql = "INSERT INTO EXAMEN_PREGUNTA (ID_EXAMEN, ID_PREGUNTA, VALOR_NOTA) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            stmt.setInt(2, idPregunta);
            stmt.setDouble(3, valorNota);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarPreguntaDeExamen(int idExamen, int idPregunta) {
        String sql = "DELETE FROM EXAMEN_PREGUNTA WHERE ID_EXAMEN = ? AND ID_PREGUNTA = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            stmt.setInt(2, idPregunta);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Pregunta> obtenerPreguntasDeExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT p.ID_PREGUNTA, p.TEXTO, p.TIPO, p.ID_TEMA, t.NOMBRE AS NOMBRE_TEMA, ep.VALOR_NOTA " +
                "FROM PREGUNTA p " +
                "JOIN EXAMEN_PREGUNTA ep ON ep.ID_PREGUNTA = p.ID_PREGUNTA " +
                "LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA " +
                "WHERE ep.ID_EXAMEN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ID_PREGUNTA");
                String texto = rs.getString("TEXTO");
                String tipo = rs.getString("TIPO");
                int idTema = rs.getInt("ID_TEMA");
                String nombreTema = rs.getString("NOMBRE_TEMA");
                double valorNota = rs.getDouble("VALOR_NOTA");

                Pregunta p = new Pregunta(id, texto, tipo, idTema);
                p.setNombreTema(nombreTema);
                p.setValorNota(valorNota);
                preguntas.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preguntas;
    }

    public static List<Pregunta> obtenerPreguntasPorTemaYDisponibles(int idTema, int idDocente, int idExamen) throws SQLException {
        List<Pregunta> lista = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        CallableStatement cs = conn.prepareCall("{ call PKG_PREGUNTA.obtener_preguntas_por_tema_y_disponibles(?, ?, ?, ?) }");
        cs.setInt(1, idTema);
        cs.setInt(2, idDocente);
        cs.setInt(3, idExamen);
        cs.registerOutParameter(4, OracleTypes.CURSOR);
        cs.execute();

        ResultSet rs = (ResultSet) cs.getObject(4);
        while (rs.next()) {
            Pregunta p = new Pregunta();
            p.setId(rs.getInt("ID_PREGUNTA"));
            p.setTexto(rs.getString("TEXTO"));
            p.setTipo(rs.getString("TIPO"));
            p.setIdTema(rs.getInt("ID_TEMA"));
            p.setNombreTema(rs.getString("NOMBRE_TEMA"));
            p.setValorNota(rs.getDouble("VALOR_NOTA"));
            p.setEsPublica("S".equals(rs.getString("ES_PUBLICA")));
            p.setIdDocente(rs.getInt("ID_DOCENTE"));
            p.setIdPreguntaPadre(rs.getInt("ID_PREGUNTA_PADRE"));
            lista.add(p);
        }

        rs.close();
        cs.close();
        conn.close();
        return lista;
    }


}
