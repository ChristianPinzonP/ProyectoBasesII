package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.OpcionRespuesta;
import com.example.proyecto.Pregunta;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreguntaDAO {

    public static boolean agregarPregunta(Pregunta pregunta) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "";
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

            int idGenerado = cs.getInt(8);
            pregunta.setId(idGenerado); // Guardar el ID en el objeto por si se necesita después

            agregarOpcionesRespuesta(pregunta.getId(), pregunta.getOpciones(), conn);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al agregar la pregunta: " + e.getMessage());
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

    //IMPLEMENTAR EL PLSQL
    public static boolean quitarVinculoPadre(int idPregunta) {
        String sql = "UPDATE PREGUNTA SET ID_PREGUNTA_PADRE = NULL WHERE ID_PREGUNTA = ?";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
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
                    int idPregunta = rs.getInt("P_ID_PREGUNTA");

                    if (idPregunta != idPreguntaAnterior) {
                        if (preguntaActual != null) preguntas.add(preguntaActual);

                        preguntaActual = mapearPreguntaDesdeResultSet(rs);
                        preguntaActual.setOpciones(new ArrayList<>());
                        idPreguntaAnterior = idPregunta;

                        // 🚀 Agregar preguntas hijas si es compuesta
                        if ("Compuesta".equalsIgnoreCase(preguntaActual.getTipo())) {
                            List<Pregunta> hijas = obtenerPreguntasHijas(preguntaActual.getId());
                            for (Pregunta hija : hijas) {
                                hija.setOpciones(obtenerOpcionesDePregunta(hija.getId()));
                            }
                            preguntaActual.setPreguntasHijas(hijas);
                        }
                    }

                    Integer idRespuesta = rs.getObject("R_ID_RESPUESTA", Integer.class);
                    if (idRespuesta != null && preguntaActual != null) {
                        preguntaActual.getOpciones().add(new OpcionRespuesta(
                                idRespuesta,
                                rs.getString("R_TEXTO"),
                                "S".equalsIgnoreCase(rs.getString("R_ES_CORRECTA"))
                        ));
                    }
                }

                if (preguntaActual != null) preguntas.add(preguntaActual);
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
                    Integer idPReguntaPadre = rs.getInt("ID_PREGUNTA_PADRE");

                    boolean esPublica = "S".equalsIgnoreCase(esPublicaStr);

                    Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema);
                    pregunta.setNombreTema(nombreTema);
                    pregunta.setValorNota(valorNota);
                    pregunta.setEsPublica(esPublica);
                    pregunta.setIdDocente(idDocente);
                    pregunta.setIdPreguntaPadre(rs.getObject("ID_PREGUNTA_PADRE") != null ? rs.getInt("ID_PREGUNTA_PADRE") : null);
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

    /**
     * Obtener preguntas que pueden ser padres (no son hijas de otra pregunta)
     * Agregar este método a la clase PreguntaDAO
     */
    public static List<Pregunta> obtenerPreguntasCandidatasAPadre(int idDocenteActual) {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "{call PKG_PREGUNTA.OBTENER_PREGUNTAS_CANDIDATAS_PADRE(?, ?)}";

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
                    pregunta.setIdPreguntaPadre(null); // Estas preguntas no tienen padre

                    listaPreguntas.add(pregunta);
                }
            }

            System.out.println("✅ Preguntas candidatas a padre cargadas: " + listaPreguntas.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener preguntas candidatas a padre: " + e.getMessage());
            e.printStackTrace();
        }

        return listaPreguntas;
    }

    /**
     * Obtener el ID del tema de una pregunta específica
     */
    public static int obtenerTemaDePregunta(int idPregunta) {
        String sql = "{call PKG_PREGUNTA.OBTENER_TEMA_PREGUNTA(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPregunta);
            stmt.registerOutParameter(2, java.sql.Types.INTEGER);

            stmt.execute();

            return stmt.getInt(2);

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener tema de pregunta: " + e.getMessage());
            e.printStackTrace();
            return -1; // Retorna -1 si hay error
        }
    }

    /**
     * Validar que una pregunta hija tenga el mismo tema que su padre
     */
    public static boolean validarTemaHijaPadre(int idPreguntaPadre, int idTemaHija) {
        String sql = "{call PKG_PREGUNTA.VALIDAR_TEMA_HIJA_PADRE(?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idPreguntaPadre);
            stmt.setInt(2, idTemaHija);
            stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

            stmt.execute();

            String resultado = stmt.getString(3);
            return "VALIDO".equals(resultado);

        } catch (SQLException e) {
            System.out.println("❌ Error al validar tema hija-padre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si una pregunta está siendo utilizada en exámenes que ya han sido presentados
     * @param idPregunta ID de la pregunta a verificar
     * @return true si la pregunta está en exámenes con presentaciones, false en caso contrario
     */
    public static boolean preguntaEstaEnExamenPresentado(int idPregunta) {
        String sql = "{? = call PKG_PREGUNTA.pregunta_esta_en_examen_presentado(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.setInt(2, idPregunta);
            stmt.execute();

            String resultado = stmt.getString(1);
            System.out.println(">> Pregunta " + idPregunta + " resultado: " + resultado);
            return "S".equals(resultado);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Obtiene información detallada sobre los exámenes donde se usa una pregunta
     * @param idPregunta ID de la pregunta
     * @return Lista con información de los exámenes que usan la pregunta
     */
    public static List<Map<String, Object>> obtenerExamenesQuUsanPregunta(int idPregunta) {
        List<Map<String, Object>> examenes = new ArrayList<>();
        String sql = "{call PKG_PREGUNTA.obtener_examenes_que_usan_pregunta(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Configurar parámetros
            stmt.setInt(1, idPregunta);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);

            // Ejecutar procedimiento
            stmt.execute();

            // Obtener cursor de resultados
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    Map<String, Object> examen = new HashMap<>();
                    examen.put("idExamen", rs.getInt("ID_EXAMEN"));
                    examen.put("titulo", rs.getString("TITULO"));
                    examen.put("fechaInicio", rs.getDate("FECHA_INICIO"));
                    examen.put("fechaFin", rs.getDate("FECHA_FIN"));
                    examen.put("totalPresentaciones", rs.getInt("TOTAL_PRESENTACIONES"));
                    examenes.add(examen);
                }
            }

            System.out.println(">> Encontrados " + examenes.size() + " exámenes que usan la pregunta " + idPregunta);

        } catch (SQLException e) {
            System.err.println("Error al obtener exámenes que usan pregunta: " + e.getMessage());
            e.printStackTrace();
        }

        return examenes;
    }
}