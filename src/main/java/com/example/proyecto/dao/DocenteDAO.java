package com.example.proyecto.dao;

import com.example.proyecto.Docente;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

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

}
