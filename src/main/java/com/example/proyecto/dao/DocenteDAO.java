package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Docente;

import java.sql.*;

public class DocenteDAO {

    public static Docente obtenerDocenteCompleto(Connection conn, int idUsuario) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL OBTENER_DOCENTE_COMPLETO(?, ?, ?, ?, ?)}");
        stmt.setInt(1, idUsuario);
        stmt.registerOutParameter(2, Types.VARCHAR); // nombre
        stmt.registerOutParameter(3, Types.VARCHAR); // correo
        stmt.registerOutParameter(4, Types.VARCHAR); // asignatura
        stmt.registerOutParameter(5, Types.VARCHAR); // estado

        stmt.execute();

        String estado = stmt.getString(5);
        if ("OK".equals(estado)) {
            Docente docente = new Docente();
            docente.setIdUsuario(idUsuario);
            docente.setNombre(stmt.getString(2));
            docente.setCorreo(stmt.getString(3));
            docente.setAsignatura(stmt.getString(4));
            return docente;
        } else {
            return null;
        }
    }

    public static Docente obtenerDocentePorId(int idDocente) {
        Docente docente = null;
        String sql = "SELECT * FROM DOCENTE d JOIN USUARIO u ON d.id_usuario = u.id_usuario WHERE d.id_usuario = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    docente = new Docente();
                    docente.setIdUsuario(rs.getInt("id_usuario"));
                    docente.setNombre(rs.getString("nombre"));
                    docente.setCorreo(rs.getString("correo"));
                    docente.setContrasena(rs.getString("contrasenia"));
                    // Agrega más campos si los tienes
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return docente;
    }


}
