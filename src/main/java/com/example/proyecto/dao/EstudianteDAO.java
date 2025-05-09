package com.example.proyecto.dao;

import com.example.proyecto.Docente;
import com.example.proyecto.Estudiante;
import com.example.proyecto.Grupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstudianteDAO {

    public static boolean esEstudiante(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM ESTUDIANTE WHERE id_estudiante = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public static Estudiante obtenerEstudianteCompleto(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, g.id_grupo, g.nombre AS nombre_grupo " +
                "FROM USUARIO u JOIN ESTUDIANTE e ON u.id_usuario = e.id_estudiante " +
                "LEFT JOIN GRUPO g ON e.id_grupo = g.id_grupo " +
                "LEFT JOIN DOCENTE d ON g.id_docente_titular = d.id_docente " +
                "WHERE u.id_usuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Estudiante estudiante = new Estudiante();
            estudiante.setIdUsuario(rs.getInt("id_usuario"));
            estudiante.setNombre(rs.getString("nombre"));
            estudiante.setCorreo(rs.getString("correo"));

            // Cargar grupo
            Grupo grupo = new Grupo();
            grupo.setIdGrupo(rs.getInt("id_grupo"));
            grupo.setNombre(rs.getString("nombre_grupo"));

            //HACER EL CAMBIO PARA SABER QUÉ DOCENTE ESTÁ ACARGO DEL GRUPO
            //REVISAR DÓNDE SE DEBE HACER, SI AQUÍ O EN
            /**
            // 1) Obtener el id del doc-ente titular que viene en la consulta
            int idDocenteTitular = rs.getInt("id_docente_titular");

            // 2) Construir (o buscar) el objeto Docente
            Docente docenteTitular = new Docente();
            docenteTitular.setIdDocente(idDocenteTitular);

            // 3) Asociarlo al grupo
            grupo.setIdDocenteTitular(docenteTitular);

            estudiante.setGrupo(grupo);
            return estudiante;
             */
        }
        return null;
    }
}
