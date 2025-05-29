package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.Docente;

import java.sql.*;

public class DocenteDAO {

    public static Docente obtenerDocenteCompleto(Connection conn, int idUsuario) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL PKG_DOCENTE.OBTENER_DOCENTE_COMPLETO(?, ?, ?, ?, ?)}");
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
        String sql = "{call PKG_DOCENTE.OBTENER_DOCENTE_POR_ID(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idDocente);
            stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);

            if (rs.next()) {
                docente = new Docente();
                docente.setIdUsuario(rs.getInt("id_usuario"));
                docente.setNombre(rs.getString("nombre"));
                docente.setCorreo(rs.getString("correo"));
                docente.setContrasena(rs.getString("contrasenia"));
                // Agrega más campos si los tienes
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return docente;
    }
}
