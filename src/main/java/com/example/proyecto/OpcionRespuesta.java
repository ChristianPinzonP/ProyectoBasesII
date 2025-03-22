package com.example.proyecto;

public class OpcionRespuesta {
    private int id;
    private String texto;
    private boolean correcta;

    public OpcionRespuesta(int id, String texto, boolean correcta) {
        this.id = id;
        this.texto = texto;
        this.correcta = correcta;
    }

    public OpcionRespuesta(String texto, boolean correcta) {
        this.texto = texto;
        this.correcta = correcta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public boolean isCorrecta() { return correcta; }
    public void setCorrecta(boolean correcta) { this.correcta = correcta; }
}

