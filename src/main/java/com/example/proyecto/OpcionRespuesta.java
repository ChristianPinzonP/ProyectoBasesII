package com.example.proyecto;

public class OpcionRespuesta {
    private int id;              // ID de la respuesta
    private String texto;
    private boolean correcta;

    // Constructor completo para cargar desde la base de datos
    public OpcionRespuesta(int id, String texto, boolean correcta) {
        this.id = id;
        this.texto = texto;
        this.correcta = correcta;
    }

    // Constructor para uso en interfaz (ej: creación de pregunta)
    public OpcionRespuesta(String texto, boolean correcta) {
        this.texto = texto;
        this.correcta = correcta;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    @Override
    public String toString() {
        return texto;
    }
}
