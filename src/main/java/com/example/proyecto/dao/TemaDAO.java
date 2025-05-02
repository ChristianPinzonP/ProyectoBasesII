package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Tema;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemaDAO {

    public static List<Tema> obtenerTemas() {
        List<Tema> temas = new ArrayList<>();
        String sql = "SELECT ID_TEMA, NOMBRE FROM TEMA ORDER BY NOMBRE ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("ID_TEMA");
                String nombre = rs.getString("NOMBRE");
                temas.add(new Tema(id, nombre));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return temas;
    }

    public static boolean agregarTema(Tema tema) {
        String sql = "INSERT INTO TEMA (ID_TEMA, NOMBRE) VALUES (SEQ_TEMA.NEXTVAL, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tema.getNombre());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
