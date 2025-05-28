package com.example.proyecto.reportes;

public class ReportePregunta {
    private int idPregunta;
    private String pregunta;
    private int totalRespuestas;
    private double porcentajeAciertos;

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public int getTotalRespuestas() {
        return totalRespuestas;
    }

    public void setTotalRespuestas(int totalRespuestas) {
        this.totalRespuestas = totalRespuestas;
    }

    public double getPorcentajeAciertos() {
        return porcentajeAciertos;
    }

    public void setPorcentajeAciertos(double porcentajeAciertos) {
        this.porcentajeAciertos = porcentajeAciertos;
    }
    public String getTexto() {
        return this.pregunta;
    }

}

