package com.example.proyecto.dao;

import com.example.proyecto.Docente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocenteDAO {

    public static boolean esDocente(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM DOCENTE WHERE id_docente = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }


    public static Docente obtenerDocenteCompleto(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, d.asignatura " +
                "FROM USUARIO u JOIN DOCENTE d ON u.id_usuario = d.id_docente WHERE u.id_usuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Docente docente = new Docente();
            docente.setIdUsuario(rs.getInt("id_usuario"));
            docente.setNombre(rs.getString("nombre"));
            docente.setCorreo(rs.getString("correo"));
            docente.setAsignatura(rs.getString("asignatura"));
            return docente;
        }
        return null;
    }
}
