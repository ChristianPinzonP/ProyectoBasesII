package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Docente;
import com.example.proyecto.Estudiante;
import com.example.proyecto.Grupo;

import java.sql.*;

public class EstudianteDAO {

    public static boolean esEstudiante(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM ESTUDIANTE WHERE id_estudiante = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public static Estudiante obtenerEstudianteCompleto(Connection conn, int idUsuario) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL OBTENER_ESTUDIANTE_COMPLETO(?, ?, ?, ?, ?, ?)}");
        stmt.setInt(1, idUsuario);
        stmt.registerOutParameter(2, Types.VARCHAR); // nombre
        stmt.registerOutParameter(3, Types.VARCHAR); // correo
        stmt.registerOutParameter(4, Types.INTEGER); // id_grupo
        stmt.registerOutParameter(5, Types.VARCHAR); // nombre_grupo
        stmt.registerOutParameter(6, Types.VARCHAR); // estado

        stmt.execute();

        String estado = stmt.getString(6);
        if ("OK".equals(estado)) {
            Estudiante estudiante = new Estudiante();
            estudiante.setIdUsuario(idUsuario);
            estudiante.setNombre(stmt.getString(2));
            estudiante.setCorreo(stmt.getString(3));

            Grupo grupo = new Grupo();
            grupo.setIdGrupo(stmt.getInt(4));
            grupo.setNombre(stmt.getString(5));

            estudiante.setGrupo(grupo);
            return estudiante;
        } else {
            return null;
        }
    }

}
