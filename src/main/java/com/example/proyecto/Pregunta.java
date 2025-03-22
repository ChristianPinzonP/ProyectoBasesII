package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

public class Pregunta {
    private int id;
    private String texto;
    private String tipo;
    private int idBanco;
    private List<OpcionRespuesta> opciones;
    // Constructor para Respuesta Corta (sin opciones)
    public Pregunta(int id, String texto, String tipo, int idBanco) {
        this.id = id;
        this.texto = texto;
        this.tipo = tipo;
        this.idBanco = idBanco;
        this.opciones = new ArrayList<>();
    }

    // Constructor para Opción Múltiple y Verdadero/Falso (con opciones)
    public Pregunta(int id, String texto, String tipo, int idBanco, List<OpcionRespuesta> opciones) {
        this.id = id;
        this.texto = texto;
        this.tipo = tipo;
        this.idBanco = idBanco;
        this.opciones = opciones;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getIdBanco() { return idBanco; }
    public void setIdBanco(int idBanco) { this.idBanco = idBanco; }

    public List<OpcionRespuesta> getOpciones() { return opciones; }
    public void setOpciones(List<OpcionRespuesta> opciones) { this.opciones = opciones; }
}
