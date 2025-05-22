package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.OpcionRespuesta;
import com.example.proyecto.Pregunta;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDAO {

    /**
     * Agregar pregunta usando el paquete PL/SQL PKG_PREGUNTA
     */
    public static boolean agregarPregunta(Pregunta pregunta) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción manual

            // 1. Agregar la pregunta
            String sqlPregunta = "{call PKG_PREGUNTA.AGREGAR_PREGUNTA(?, ?, ?, ?, ?, ?, ?)}";

            int idGenerado;
            try (CallableStatement stmtPregunta = conn.prepareCall(sqlPregunta)) {
                stmtPregunta.setString(1, pregunta.getTexto());
                stmtPregunta.setString(2, pregunta.getTipo());
                stmtPregunta.setInt(3, pregunta.getIdTema());
                stmtPregunta.setDouble(4, pregunta.getValorNota());
                stmtPregunta.setString(5, pregunta.isEsPublica() ? "S" : "N");
                stmtPregunta.setInt(6, pregunta.getIdDocente());
                stmtPregunta.registerOutParameter(7, Types.NUMERIC);

                stmtPregunta.execute();
                idGenerado = stmtPregunta.getInt(7);
            }

            // 2. Agregar las opciones de respuesta
            if (pregunta.getOpciones() != null && !pregunta.getOpciones().isEmpty()) {
                String sqlOpcion = "{call PKG_PREGUNTA.AGREGAR_OPCION_RESPUESTA(?, ?, ?)}";

                try (CallableStatement stmtOpcion = conn.prepareCall(sqlOpcion)) {
                    for (OpcionRespuesta opcion : pregunta.getOpciones()) {
                        stmtOpcion.setInt(1, idGenerado);
                        stmtOpcion.setString(2, opcion.getTexto());
                        stmtOpcion.setString(3, opcion.isCorrecta() ? "S" : "N");
                        stmtOpcion.execute();
                    }
                }
            }

            conn.commit(); // Confirmar transacción
            System.out.println("✅ Pregunta insertada con ID: " + idGenerado);
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback en caso de error
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al insertar la pregunta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Actualizar pregunta usando el paquete PL/SQL PKG_PREGUNTA
     */
    public static boolean actualizarPregunta(int idPregunta, String nuevoTexto, String nuevoTipo, int nuevoIdTema,
                                             double nuevoValorNota, boolean nuevaEsPublica, int idDocente,
                                             List<OpcionRespuesta> nuevasOpciones) {
        String sql = "{call PKG_PREGUNTA.ACTUALIZAR_PREGUNTA(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPregunta);
            stmt.setString(2, nuevoTexto);
            stmt.setString(3, nuevoTipo);
            stmt.setInt(4, nuevoIdTema);
            stmt.setDouble(5, nuevoValorNota);
            stmt.setString(6, nuevaEsPublica ? "S" : "N");

            stmt.execute();

            // Actualizar opciones por separado (el procedimiento solo actualiza la pregunta)
            if (nuevasOpciones != null && !nuevasOpciones.isEmpty()) {
                eliminarOpcionesDePregunta(idPregunta);
                return agregarOpcionesRespuesta(idPregunta, nuevasOpciones);
            }

            System.out.println("✅ Pregunta actualizada correctamente");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar la pregunta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar pregunta usando el paquete PL/SQL PKG_PREGUNTA
     */
    public static boolean eliminarPregunta(int idPregunta) {
        String sql = "{call PKG_PREGUNTA.ELIMINAR_PREGUNTA(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPregunta);
            stmt.execute();

            System.out.println("✅ Pregunta eliminada correctamente");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar la pregunta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener opciones de pregunta usando el paquete PL/SQL PKG_PREGUNTA
     */
    public static List<OpcionRespuesta> obtenerOpcionesDePregunta(int idPregunta) {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        String sql = "{call PKG_PREGUNTA.OBTENER_OPCIONES_PREGUNTA(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPregunta);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    int idRespuesta = rs.getInt("ID_RESPUESTA");
                    String texto = rs.getString("TEXTO");
                    String esCorrectaStr = rs.getString("ES_CORRECTA");

                    boolean esCorrecta = "S".equalsIgnoreCase(esCorrectaStr);

                    opciones.add(new OpcionRespuesta(idRespuesta, texto, esCorrecta));
                    System.out.println("✅ Respuesta obtenida: " + texto + " - Correcta: " + esCorrecta);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener opciones: " + e.getMessage());
            e.printStackTrace();
        }

        return opciones;
    }

    /**
     * Obtener preguntas por tema con filtro de docente actual usando PL/SQL
     * Mantiene la lógica de seguridad para mostrar solo preguntas públicas o del docente actual
     */
    public static List<Pregunta> obtenerPreguntasPorTema(int idTema, int idDocenteActual) {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "{call PKG_PREGUNTA.OBTENER_PREGUNTAS_POR_TEMA_DOCENTE(?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idTema);
            stmt.setInt(2, idDocenteActual);
            stmt.registerOutParameter(3, OracleTypes.CURSOR);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(3)) {
                while (rs.next()) {
                    int idPregunta = rs.getInt("ID_PREGUNTA");
                    String texto = rs.getString("TEXTO");
                    String tipo = rs.getString("TIPO");
                    String nombreTema = rs.getString("NOMBRE_TEMA");
                    double valorNota = rs.getDouble("VALOR_NOTA");
                    String esPublicaStr = rs.getString("ES_PUBLICA");
                    int idDocente = rs.getInt("ID_DOCENTE");

                    boolean esPublica = "S".equalsIgnoreCase(esPublicaStr);

                    // Obtener las opciones de respuesta para la pregunta usando el paquete PL/SQL
                    List<OpcionRespuesta> opciones = obtenerOpcionesDePregunta(idPregunta);

                    Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema, opciones);
                    pregunta.setNombreTema(nombreTema);
                    pregunta.setValorNota(valorNota);
                    pregunta.setEsPublica(esPublica);
                    pregunta.setIdDocente(idDocente);

                    listaPreguntas.add(pregunta);
                }
            }

            System.out.println("✅ Preguntas cargadas por tema con filtro docente: " + listaPreguntas.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener preguntas por tema: " + e.getMessage());
            e.printStackTrace();
        }

        return listaPreguntas;
    }

    /**
     * Obtener preguntas con opciones por examen usando el paquete PL/SQL PKG_PREGUNTA
     */
    public static List<Pregunta> obtenerPreguntasConOpcionesPorExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "{call PKG_PREGUNTA.OBTENER_PREGUNTAS_CON_OPCIONES_POR_EXAMEN(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idExamen);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                Pregunta preguntaActual = null;
                int idPreguntaAnterior = -1;

                while (rs.next()) {
                    int idPregunta = rs.getInt("ID_PREGUNTA");

                    // Si es una nueva pregunta
                    if (idPregunta != idPreguntaAnterior) {
                        // Agregar la pregunta anterior a la lista si existe
                        if (preguntaActual != null) {
                            preguntas.add(preguntaActual);
                        }

                        // Crear nueva pregunta
                        preguntaActual = mapearPreguntaDesdeResultSet(rs);
                        preguntaActual.setOpciones(new ArrayList<>());
                        idPreguntaAnterior = idPregunta;
                    }

                    // Agregar opción de respuesta si existe
                    if (rs.getString("TEXTO_RESPUESTA") != null) {
                        int idRespuesta = rs.getInt("ID_RESPUESTA");
                        String textoRespuesta = rs.getString("TEXTO_RESPUESTA");
                        String esCorrectaStr = rs.getString("ES_CORRECTA_RESPUESTA");

                        boolean esCorrecta = "S".equalsIgnoreCase(esCorrectaStr);
                        OpcionRespuesta opcion = new OpcionRespuesta(idRespuesta, textoRespuesta, esCorrecta);
                        preguntaActual.getOpciones().add(opcion);
                    }
                }

                // Agregar la última pregunta
                if (preguntaActual != null) {
                    preguntas.add(preguntaActual);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener preguntas por examen: " + e.getMessage());
            e.printStackTrace();
        }

        return preguntas;
    }

    /**
     * Mapear pregunta desde ResultSet
     */
    private static Pregunta mapearPreguntaDesdeResultSet(ResultSet rs) throws SQLException {
        int idPregunta = rs.getInt("ID_PREGUNTA");
        String texto = rs.getString("TEXTO");
        String tipo = rs.getString("TIPO");
        int idTema = rs.getInt("ID_TEMA");
        double valorNota = rs.getDouble("VALOR_NOTA");
        String esPublicaStr = rs.getString("ES_PUBLICA");
        int idDocente = rs.getInt("ID_DOCENTE");

        boolean esPublica = "S".equalsIgnoreCase(esPublicaStr);

        Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema);
        pregunta.setValorNota(valorNota);
        pregunta.setEsPublica(esPublica);
        pregunta.setIdDocente(idDocente);

        return pregunta;
    }

    public static boolean agregarOpcionesRespuesta(int idPregunta, List<OpcionRespuesta> opciones) {
        if (opciones.isEmpty()) {
            System.out.println("⚠ No hay opciones para guardar en la pregunta ID: " + idPregunta);
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             OracleConnection oracleConn = conn.unwrap(OracleConnection.class)) {

            // Crear los STRUCTs de OPCION_RESPUESTA_OBJ
            Struct[] structOpciones = new Struct[opciones.size()];
            for (int i = 0; i < opciones.size(); i++) {
                OpcionRespuesta op = opciones.get(i);
                Object[] atributos = new Object[]{
                        null, // id_respuesta lo genera la secuencia
                        op.getTexto(),
                        op.isCorrecta() ? "S" : "N"
                };
                structOpciones[i] = oracleConn.createStruct("OPCION_RESPUESTA_OBJ", atributos);
            }

            // Crear el ARRAY tipo OPCION_RESPUESTA_TABLE
            Array arrayOpciones = oracleConn.createOracleArray("OPCION_RESPUESTA_TABLE", structOpciones);

            // Llamar al procedimiento almacenado
            CallableStatement cs = conn.prepareCall("{call PKG_PREGUNTA.agregar_opciones_respuesta(?, ?)}");
            cs.setInt(1, idPregunta);
            cs.setArray(2, arrayOpciones);
            cs.execute();

            System.out.println("✅ Opciones insertadas correctamente desde PL/SQL.");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al llamar al procedimiento PL/SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarOpcionesDePregunta(int idPregunta) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call PKG_PREGUNTA.eliminar_opciones_respuesta(?)}")) {

            cs.setInt(1, idPregunta);
            cs.execute();

            System.out.println("✅ Opciones eliminadas correctamente para la pregunta ID: " + idPregunta);
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar opciones desde PL/SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener preguntas visibles para un docente específico usando PL/SQL
     * Incluye preguntas públicas + preguntas privadas del docente
     */
    public static List<Pregunta> obtenerPreguntasVisiblesParaDocente(int idDocenteActual) {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "{call PKG_PREGUNTA.OBTENER_PREGUNTAS_VISIBLES_DOCENTE(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idDocenteActual);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    int idPregunta = rs.getInt("ID_PREGUNTA");
                    String texto = rs.getString("TEXTO");
                    String tipo = rs.getString("TIPO");
                    int idTema = rs.getInt("ID_TEMA");
                    String nombreTema = rs.getString("NOMBRE_TEMA");
                    double valorNota = rs.getDouble("VALOR_NOTA");
                    String esPublicaStr = rs.getString("ES_PUBLICA");
                    int idDocente = rs.getInt("ID_DOCENTE");

                    boolean esPublica = "S".equalsIgnoreCase(esPublicaStr);

                    Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema);
                    pregunta.setNombreTema(nombreTema);
                    pregunta.setValorNota(valorNota);
                    pregunta.setEsPublica(esPublica);
                    pregunta.setIdDocente(idDocente);
                    listaPreguntas.add(pregunta);
                }
            }

            System.out.println("✅ Preguntas visibles para docente cargadas: " + listaPreguntas.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener preguntas visibles para docente: " + e.getMessage());
            e.printStackTrace();
        }

        return listaPreguntas;
    }
}