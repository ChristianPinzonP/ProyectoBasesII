package com.example.proyecto.dao;

import com.example.proyecto.DBConnection;
import com.example.proyecto.OpcionRespuesta;
import com.example.proyecto.Pregunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDAO {

    public static boolean agregarPregunta(Pregunta pregunta) {
        String sql = "INSERT INTO PREGUNTA (ID_PREGUNTA, TEXTO, TIPO, ID_TEMA, VALOR_NOTA, ES_PUBLICA, ID_DOCENTE) " +
                "VALUES (SEQ_PREGUNTA.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID_PREGUNTA"})) {

            stmt.setString(1, pregunta.getTexto());
            stmt.setString(2, pregunta.getTipo());
            stmt.setInt(3, pregunta.getIdTema());
            stmt.setDouble(4, pregunta.getValorNota());  // Aquí se asigna el valor de la nota
            stmt.setString(5, pregunta.isEsPublica() ? "S" : "N");
            stmt.setInt(6, pregunta.getIdDocente());

            int filasInsertadas = stmt.executeUpdate();

            if (filasInsertadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idPregunta = rs.getInt(1);
                        System.out.println("✅ Pregunta insertada con ID: " + idPregunta);

                        if (!pregunta.getOpciones().isEmpty()) {
                            return agregarOpcionesRespuesta(idPregunta, pregunta.getTipo(), pregunta.getOpciones());
                        }
                    }
                }
            }
            return filasInsertadas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar la pregunta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean agregarOpcionesRespuesta(int idPregunta, String tipoPregunta, List<OpcionRespuesta> opciones) {
        String sql = "INSERT INTO RESPUESTA (ID_RESPUESTA, ID_PREGUNTA, TEXTO, ES_CORRECTA) VALUES (SEQ_RESPUESTA.NEXTVAL, ?, ?, ?)";

        if (opciones.isEmpty()) {
            System.out.println("⚠ No hay opciones para guardar en la pregunta ID: " + idPregunta);
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("✅ Conexión establecida para guardar respuestas.");
            System.out.println("Intentando guardar respuestas para ID Pregunta: " + idPregunta);
            System.out.println("Tipo de pregunta detectado: " + tipoPregunta);

            for (OpcionRespuesta opcion : opciones) {
                System.out.println("Insertando respuesta: " + opcion.getTexto() + " - Correcta: " + opcion.isCorrecta());

                stmt.setInt(1, idPregunta);
                stmt.setString(2, opcion.getTexto());
                stmt.setString(3, opcion.isCorrecta() ? "S" : "N");  // Se asegura que ES_CORRECTA sea "S" o "N"
                stmt.addBatch();
            }

            int[] filasInsertadas = stmt.executeBatch();
            System.out.println("✅ Respuestas insertadas correctamente. Total: " + filasInsertadas.length);
            return filasInsertadas.length > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar respuestas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<OpcionRespuesta> obtenerOpcionesDePregunta(int idPregunta) {
        List<OpcionRespuesta> opciones = new ArrayList<>();
        String sql = "SELECT ID_RESPUESTA, TEXTO, ES_CORRECTA FROM RESPUESTA WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idRespuesta = rs.getInt("ID_RESPUESTA");
                String texto = rs.getString("TEXTO");
                String esCorrectaStr = rs.getString("ES_CORRECTA");

                boolean esCorrecta = "S".equalsIgnoreCase(esCorrectaStr);  // Garantiza la correcta interpretación

                opciones.add(new OpcionRespuesta(idRespuesta, texto, esCorrecta));
                System.out.println("✅ Respuesta obtenida: " + texto + " - Correcta: " + esCorrecta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return opciones;
    }

    public static List<Pregunta> obtenerTodasLasPreguntas() {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "SELECT p.ID_PREGUNTA, p.TEXTO, p.TIPO, p.ID_TEMA, t.NOMBRE as NOMBRE_TEMA, " +
                "NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, p.ES_PUBLICA, p.ID_DOCENTE " +
                "FROM PREGUNTA p " +
                "LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA " +
                "ORDER BY p.ID_PREGUNTA DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

                System.out.println("Pregunta ID: " + idPregunta + ", Texto: " + texto);

                Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema);
                pregunta.setNombreTema(nombreTema);
                pregunta.setValorNota(valorNota);
                pregunta.setEsPublica(esPublica);
                pregunta.setIdDocente(idDocente);
                listaPreguntas.add(pregunta);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar preguntas: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Total preguntas cargadas: " + listaPreguntas.size());
        return listaPreguntas;
    }

    public static List<Pregunta> obtenerPreguntasVisiblesParaDocente(int idDocenteActual) {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "SELECT p.ID_PREGUNTA, p.TEXTO, p.TIPO, p.ID_TEMA, t.NOMBRE as NOMBRE_TEMA, " +
                "NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, p.ES_PUBLICA, p.ID_DOCENTE " +
                "FROM PREGUNTA p " +
                "LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA " +
                "WHERE p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = ? " +
                "ORDER BY p.ID_PREGUNTA DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocenteActual);
            ResultSet rs = stmt.executeQuery();

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

        } catch (SQLException e) {
            System.out.println("Error al cargar preguntas: " + e.getMessage());
            e.printStackTrace();
        }

        return listaPreguntas;
    }

    public static boolean actualizarPregunta(int idPregunta, String nuevoTexto, String nuevoTipo, int nuevoIdTema,
                                             double nuevoValorNota, boolean nuevaEsPublica, int idDocente,
                                             List<OpcionRespuesta> nuevasOpciones)  {
        String sql = "UPDATE PREGUNTA SET TEXTO = ?, TIPO = ?, ID_TEMA = ?, VALOR_NOTA = ?, ES_PUBLICA = ? WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoTexto);
            stmt.setString(2, nuevoTipo);
            stmt.setInt(3, nuevoIdTema);
            stmt.setDouble(4, nuevoValorNota);  // Actualiza la nota
            stmt.setString(5, nuevaEsPublica ? "S" : "N");
            stmt.setInt(6, idPregunta);

            int filasActualizadas = stmt.executeUpdate();
            System.out.println("Filas actualizadas en tabla PREGUNTA: " + filasActualizadas);

            if (filasActualizadas > 0) {
                // Primero eliminamos todas las opciones existentes
                boolean opcionesEliminadas = eliminarOpcionesDePregunta(idPregunta);
                System.out.println("Opciones eliminadas: " + opcionesEliminadas);

                // Luego agregamos las nuevas opciones
                boolean opcionesAgregadas = true;
                if (nuevasOpciones != null && !nuevasOpciones.isEmpty()) {
                    opcionesAgregadas = agregarOpcionesRespuesta(idPregunta, nuevoTipo, nuevasOpciones);
                    System.out.println("Nuevas opciones agregadas: " + opcionesAgregadas);
                }

                return opcionesAgregadas;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar la pregunta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarPregunta(int idPregunta) {
        eliminarOpcionesDePregunta(idPregunta);

        String sql = "DELETE FROM PREGUNTA WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean eliminarOpcionesDePregunta(int idPregunta) {
        String sql = "DELETE FROM RESPUESTA WHERE ID_PREGUNTA = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            int filasEliminadas = stmt.executeUpdate();
            System.out.println("✅ Opciones eliminadas para la pregunta ID: " + idPregunta + ". Total: " + filasEliminadas);
            return filasEliminadas > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar opciones de respuesta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static List<Pregunta> obtenerPreguntasConOpcionesPorExamen(int idExamen) {
        List<Pregunta> preguntas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psPreguntas = conn.prepareStatement(
                    "SELECT p.id_pregunta, p.texto, p.tipo, p.id_tema, p.es_publica, p.id_docente " +
                            "FROM examen_pregunta ep " +
                            "JOIN pregunta p ON ep.id_pregunta = p.id_pregunta " +
                            "WHERE ep.id_examen = ?");
            psPreguntas.setInt(1, idExamen);
            ResultSet rsPreguntas = psPreguntas.executeQuery();

            while (rsPreguntas.next()) {
                int idPregunta = rsPreguntas.getInt("id_pregunta");
                String texto = rsPreguntas.getString("texto");
                String tipo = rsPreguntas.getString("tipo");
                int idTema = rsPreguntas.getInt("id_tema");
                String esPublicaStr = rsPreguntas.getString("es_publica");
                int idDocente = rsPreguntas.getInt("id_docente");

                boolean esPublica = "S".equalsIgnoreCase(esPublicaStr);

                List<OpcionRespuesta> opciones = new ArrayList<>();

                PreparedStatement psRespuestas = conn.prepareStatement(
                        "SELECT id_respuesta, texto FROM respuesta WHERE id_pregunta = ?");
                psRespuestas.setInt(1, idPregunta);
                ResultSet rsRespuestas = psRespuestas.executeQuery();

                while (rsRespuestas.next()) {
                    int idRespuesta = rsRespuestas.getInt("id_respuesta");
                    String textoRespuesta = rsRespuestas.getString("texto");
                    OpcionRespuesta opcion = new OpcionRespuesta(idRespuesta, textoRespuesta, false);
                    opciones.add(opcion);
                }

                rsRespuestas.close();
                psRespuestas.close();

                Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema, opciones);
                pregunta.setEsPublica(esPublica);
                pregunta.setIdDocente(idDocente);
                preguntas.add(pregunta);
            }

            rsPreguntas.close();
            psPreguntas.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return preguntas;
    }


    public static List<Pregunta> obtenerPreguntasPorTema(int idTema, int idDocenteActual) {
        List<Pregunta> listaPreguntas = new ArrayList<>();
        String sql = "SELECT p.ID_PREGUNTA, p.TEXTO, p.TIPO, p.ID_TEMA, t.NOMBRE as NOMBRE_TEMA, " +
                "p.ES_PUBLICA, p.ID_DOCENTE " +
                "FROM PREGUNTA p " +
                "LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA " +
                "WHERE p.ID_TEMA = ? AND (p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTema);
            stmt.setInt(2, idDocenteActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idPregunta = rs.getInt("ID_PREGUNTA");
                String texto = rs.getString("TEXTO");
                String tipo = rs.getString("TIPO");
                String nombreTema = rs.getString("NOMBRE_TEMA");
                String esPublicaStr = rs.getString("ES_PUBLICA");
                int idDocente = rs.getInt("ID_DOCENTE");

                boolean esPublica = "S".equalsIgnoreCase(esPublicaStr);

                // Obtener las opciones de respuesta para la pregunta
                List<OpcionRespuesta> opciones = PreguntaDAO.obtenerOpcionesDePregunta(idPregunta);


                Pregunta pregunta = new Pregunta(idPregunta, texto, tipo, idTema, opciones);
                pregunta.setNombreTema(nombreTema);
                pregunta.setEsPublica(esPublica);
                pregunta.setIdDocente(idDocente);

                listaPreguntas.add(pregunta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaPreguntas;
    }

}

