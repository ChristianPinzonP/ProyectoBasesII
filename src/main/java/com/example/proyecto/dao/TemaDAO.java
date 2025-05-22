package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Tema;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemaDAO {

    public static List<Tema> obtenerTemas() {
        List<Tema> temas = new ArrayList<>();
        String call = "{ call OBTENER_TEMAS(?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {

            // Registramos el parámetro de salida (el tipo TEMA_TABLA)
            stmt.registerOutParameter(1, java.sql.Types.ARRAY, "TEMA_TABLA");

            stmt.execute();

            // Obtener el array de objetos
            Array temaArray = stmt.getArray(1);
            Object[] temaData = (Object[]) temaArray.getArray();

            for (Object obj : temaData) {
                Struct row = (Struct) obj;
                Object[] attrs = row.getAttributes();
                int id = ((BigDecimal) attrs[0]).intValue(); // ID_TEMA
                String nombre = (String) attrs[1];            // NOMBRE

                temas.add(new Tema(id, nombre));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener los temas desde la base de datos:");
            e.printStackTrace();
        }

        return temas;
    }
}

