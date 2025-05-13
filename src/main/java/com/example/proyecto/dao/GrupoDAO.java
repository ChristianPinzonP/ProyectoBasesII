package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Grupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public static List<Grupo> obtenerGruposPorDocente(int idDocente) {
        List<Grupo> listaGrupos = new ArrayList<>();

        String sql = "SELECT g.ID_GRUPO, g.NOMBRE " +
                "FROM GRUPO g " +
                "JOIN DOCENTE_GRUPO dg ON TO_NUMBER(g.ID_GRUPO) = TO_NUMBER(dg.ID_GRUPO) " +
                "WHERE dg.ID_DOCENTE = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 🔍 Mostrar el usuario conectado
            System.out.println("→ Ejecutando consulta como usuario: " + conn.getMetaData().getUserName());

            stmt.setInt(1, idDocente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Grupo grupo = new Grupo();
                grupo.setIdGrupo(rs.getInt("ID_GRUPO"));
                grupo.setNombre(rs.getString("NOMBRE"));
                listaGrupos.add(grupo);
            }

            // ✅ Imprimir los grupos recuperados
            System.out.println("→ Total de grupos devueltos desde Java: " + listaGrupos.size());
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
