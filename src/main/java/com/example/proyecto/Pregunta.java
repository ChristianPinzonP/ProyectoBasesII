package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class Pregunta {
    private int id;
    private String texto;
    private String tipo;
    private String nombreTema;
    private int idTema;
    private double valorNota;
    private boolean esPublica;
    private int idDocente;
    private Integer idPreguntaPadre;

    private List<OpcionRespuesta> opciones;

    public Pregunta(int id, String texto, String tipo, int idTema, double valorNota,
                    boolean esPublica, int idDocente, List<OpcionRespuesta> opciones) {
        this.id = id;
        this.texto = texto;
        this.tipo = tipo;
        this.idTema = idTema;
        this.valorNota = valorNota;
        this.esPublica = esPublica;
        this.idDocente = idDocente;
        this.opciones = opciones;
    }

    public Pregunta(int id, String texto, String tipo, int idTema) {
        this(id, texto, tipo, idTema, 0.0, false, 0, null);
    }

    public Pregunta(int id, String texto, String tipo, int idTema, List<OpcionRespuesta> opciones) {
        this(id, texto, tipo, idTema, 0.0, false, 0, opciones);
    }

    public Integer getIdPreguntaPadre() {
        return idPreguntaPadre;
    }

    public void setIdPreguntaPadre(Integer idPreguntaPadre) {
        this.idPreguntaPadre = idPreguntaPadre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getIdTema() { return idTema; }
    public void setIdTema(int idTema) { this.idTema = idTema; }

    public double getValorNota() { return valorNota; }
    public void setValorNota(double valorNota) { this.valorNota = valorNota; }

    public String getNombreTema() { return nombreTema; }
    public void setNombreTema(String nombreTema) { this.nombreTema = nombreTema; }

    public boolean isEsPublica() { return esPublica; }
    public void setEsPublica(boolean esPublica) { this.esPublica = esPublica; }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public List<OpcionRespuesta> getOpciones() {
        if (opciones == null) {
            opciones = new ArrayList<>();
        }
        return opciones;
    }

    public void setOpciones(List<OpcionRespuesta> opciones) {
        this.opciones = opciones;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + texto + " - Tema: " + nombreTema;
    }
}
