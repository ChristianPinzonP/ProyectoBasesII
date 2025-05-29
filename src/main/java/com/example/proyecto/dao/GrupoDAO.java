package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Grupo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    /**
     * Obtiene los grupos asociados a un docente usando PL/SQL
     * @param idDocente ID del docente
     * @return Lista de grupos del docente
     */
    public static List<Grupo> obtenerGruposPorDocente(int idDocente) {
        List<Grupo> listaGrupos = new ArrayList<>();

        String sqlCall = "{? = call PKG_GRUPO.OBTENER_GRUPOS_POR_DOCENTE(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sqlCall)) {

            // Mostrar el usuario conectado
            System.out.println("→ Ejecutando consulta como usuario: " + conn.getMetaData().getUserName());

            // Registrar el cursor de salida
            stmt.registerOutParameter(1, Types.REF_CURSOR);
            stmt.setInt(2, idDocente);

            // Ejecutar la función
            stmt.execute();

            // Obtener el cursor y procesar resultados
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    Grupo grupo = new Grupo();
                    grupo.setIdGrupo(rs.getInt("ID_GRUPO"));
                    grupo.setNombre(rs.getString("NOMBRE"));
                    listaGrupos.add(grupo);
                }
            }

            System.out.println("→ Total de grupos devueltos desde PL/SQL: " + listaGrupos.size());
            for (Grupo g : listaGrupos) {
                System.out.println("   - " + g.getIdGrupo() + ": " + g.getNombre());
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener grupos por docente: " + e.getMessage());
            e.printStackTrace();
        }

        return listaGrupos;
    }
}
