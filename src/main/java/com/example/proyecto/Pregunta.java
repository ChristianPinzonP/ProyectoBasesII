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

    private List<OpcionRespuesta> opciones;

    // Constructor para Respuesta Corta (sin opciones)
    public Pregunta(int id, String texto, String tipo, int idTema) {
        this.id = id;
        this.texto = texto;
        this.tipo = tipo;
        this.idTema = idTema;
    }

    // Constructor para Opción Múltiple y Verdadero/Falso (con opciones)
    public Pregunta(int id, String texto, String tipo, int idTema, List<OpcionRespuesta> opciones) {
        this.id = id;
        this.texto = texto;
        this.tipo = tipo;
        this.idTema = idTema;
        this.opciones = opciones;
    }

    // Getters y Setters
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

    public List<OpcionRespuesta> getOpciones() { return opciones; }
    public void setOpciones(List<OpcionRespuesta> opciones) { this.opciones = opciones; }

    @Override
    public String toString() {
        return "[" + id + "] " + texto + " - Tema: " + nombreTema;
    }
}
