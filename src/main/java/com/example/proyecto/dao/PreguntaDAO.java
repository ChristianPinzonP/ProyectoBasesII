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
        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{call PKG_PREGUNTA.agregar_pregunta(?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setString(1, pregunta.getTexto());
            cs.setString(2, pregunta.getTipo());
            cs.setInt(3, pregunta.getIdTema());
            cs.setDouble(4, pregunta.getValorNota());
            cs.setString(5, pregunta.isEsPublica() ? "S" : "N");
            cs.setInt(6, pregunta.getIdDocente());
            if (pregunta.getIdPreguntaPadre() != null)
                cs.setInt(7, pregunta.getIdPreguntaPadre());
            else
                cs.setNull(7, java.sql.Types.INTEGER);
            cs.registerOutParameter(8, java.sql.Types.INTEGER);

            cs.execute();
            pregunta.setId(cs.getInt(8));
            agregarOpcionesRespuesta(pregunta.getId(), pregunta.getOpciones(), conn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Pregunta> obtenerPreguntasHijas(int idPadre) {
        List<Pregunta> lista = new ArrayList<>();
        try (Connection conexion = DBConnection.getConnection()) {
            CallableStatement cs = conexion.prepareCall("{call PKG_PREGUNTA.obtener_preguntas_hijas(?, ?)}");
            cs.setInt(1, idPadre);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(2);
            while (rs.next()) {
                Pregunta p = new Pregunta(
                        rs.getInt("ID_PREGUNTA"),
                        rs.getString("TEXTO"),
                        rs.getString("TIPO"),
                        rs.getInt("ID_TEMA"),
                        rs.getDouble("VALOR_NOTA"),
                        "S".equals(rs.getString("ES_PUBLICA")),
                        rs.getInt("ID_DOCENTE"),
                        new ArrayList<>()
                );
                p.setIdPreguntaPadre(rs.getInt("ID_PREGUNTA_PADRE"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static boolean quitarVinculoPadre(int idPregunta) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE pregunta SET id_pregunta_padre = NULL WHERE id_pregunta = ?");
            ps.setInt(1, idPregunta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Actualizar pregunta usando el paquete PL/SQL PKG_PREGUNTA
     */
    public static boolean actualizarPregunta(int idPregunta, String nuevoTexto, String nuevoTipo, int nuevoIdTema,
                                             double nuevoValorNota, boolean nuevaEsPublica, Integer idPreguntaPadre,
                                             List<OpcionRespuesta> nuevasOpciones) {
        String sql = "{call PKG_PREGUNTA.ACTUALIZAR_PREGUNTA(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPregunta);
            stmt.setString(2, nuevoTexto);
            stmt.setString(3, nuevoTipo);
            stmt.setInt(4, nuevoIdTema);
            stmt.setDouble(5, nuevoValorNota);
            stmt.setString(6, nuevaEsPublica ? "S" : "N");

            // pasar el id_pregunta_padre
            if (idPreguntaPadre != null) {
                stmt.setInt(7, idPreguntaPadre);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.execute();

            // Actualizar opciones por separado (el procedimiento solo actualiza la pregunta)
            if (nuevasOpciones != null && !nuevasOpciones.isEmpty()) {
                eliminarOpcionesDePregunta(idPregunta);
                agregarOpcionesRespuesta(idPregunta, nuevasOpciones, conn);
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
        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call PKG_PREGUNTA.OBTENER_OPCIONES_PREGUNTA(?, ?)}");
            stmt.setInt(1, idPregunta);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    OpcionRespuesta o = new OpcionRespuesta(
                            rs.getInt("ID_RESPUESTA"),
                            rs.getString("TEXTO"),
                            "S".equalsIgnoreCase(rs.getString("ES_CORRECTA"))
                    );
                    opciones.add(o);
                }
            }
        } catch (SQLException e) {
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
        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call PKG_PREGUNTA.OBTENER_PREGUNTAS_POR_TEMA_DOCENTE(?, ?, ?)}");
            stmt.setInt(1, idTema);
            stmt.setInt(2, idDocenteActual);
            stmt.registerOutParameter(3, OracleTypes.CURSOR);

            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(3)) {
                while (rs.next()) {
                    Pregunta pregunta = new Pregunta(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO"),
                            rs.getString("TIPO"),
                            rs.getInt("ID_TEMA"),
                            rs.getDouble("VALOR_NOTA"),
                            "S".equalsIgnoreCase(rs.getString("ES_PUBLICA")),
                            rs.getInt("ID_DOCENTE"),
                            obtenerOpcionesDePregunta(rs.getInt("ID_PREGUNTA"))
                    );
                    pregunta.setNombreTema(rs.getString("NOMBRE_TEMA"));
                    listaPreguntas.add(pregunta);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaPreguntas;
    }

    public static List<Pregunta> obtenerPreguntasConOpcionesPorExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call PKG_PREGUNTA.OBTENER_PREGUNTAS_CON_OPCIONES_POR_EXAMEN(?, ?)}");
            stmt.setInt(1, idExamen);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(2);
            Pregunta actual = null;
            int anterior = -1;

            while (rs.next()) {
                int id = rs.getInt("P_ID_PREGUNTA");
                if (id != anterior) {
                    if (actual != null) preguntas.add(actual);
                    actual = new Pregunta(id, rs.getString("P_TEXTO"), rs.getString("P_TIPO"), rs.getInt("P_ID_TEMA"));
                    actual.setValorNota(rs.getDouble("P_VALOR_NOTA"));
                    actual.setOpciones(new ArrayList<>());
                    anterior = id;
                }

                Integer idResp = rs.getObject("R_ID_RESPUESTA", Integer.class);
                if (idResp != null && actual != null) {
                    actual.getOpciones().add(new OpcionRespuesta(
                            idResp,
                            rs.getString("R_TEXTO"),
                            "S".equalsIgnoreCase(rs.getString("R_ES_CORRECTA"))
                    ));
                }
            }

            if (actual != null) preguntas.add(actual);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return preguntas;
    }


    /**
     * Mapear pregunta desde ResultSet
     */
    private static Pregunta mapearPreguntaDesdeResultSet(ResultSet rs) throws SQLException {
        int idPregunta = rs.getInt("P_ID_PREGUNTA");
        String texto = rs.getString("P_TEXTO");
        String tipo = rs.getString("P_TIPO");
        int idTema = rs.getInt("P_ID_TEMA");
        double valorNota = rs.getDouble("P_VALOR_NOTA");

        Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema);
        pregunta.setValorNota(valorNota);

        return pregunta;
    }


    public static void agregarOpcionesRespuesta(int idPregunta, List<OpcionRespuesta> opciones, Connection conn) throws SQLException {
        for (OpcionRespuesta o : opciones) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO RESPUESTA (ID_RESPUESTA, ID_PREGUNTA, TEXTO, ES_CORRECTA) VALUES (SEQ_RESPUESTA.NEXTVAL, ?, ?, ?)");
            ps.setInt(1, idPregunta);
            ps.setString(2, o.getTexto());
            ps.setString(3, o.isCorrecta() ? "S" : "N");
            ps.executeUpdate();
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