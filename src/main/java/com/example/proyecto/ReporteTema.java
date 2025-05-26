package com.example.proyecto;

public class ReporteTema {
    private int idTema;
    private String tema;
    private int totalRespuestas;
    private int totalCorrectas;
    private int totalIncorrectas;
    private double promedioNota;
    private double maxNota;
    private double minNota;

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public int getTotalRespuestas() {
        return totalRespuestas;
    }

    public void setTotalRespuestas(int totalRespuestas) {
        this.totalRespuestas = totalRespuestas;
    }

    public int getTotalCorrectas() {
        return totalCorrectas;
    }

    public void setTotalCorrectas(int totalCorrectas) {
        this.totalCorrectas = totalCorrectas;
    }

    public int getTotalIncorrectas() {
        return totalIncorrectas;
    }

    public void setTotalIncorrectas(int totalIncorrectas) {
        this.totalIncorrectas = totalIncorrectas;
    }

    public double getPromedioNota() {
        return promedioNota;
    }

    public void setPromedioNota(double promedioNota) {
        this.promedioNota = promedioNota;
    }

    public double getMaxNota() {
        return maxNota;
    }

    public void setMaxNota(double maxNota) {
        this.maxNota = maxNota;
    }

    public double getMinNota() {
        return minNota;
    }

    public void setMinNota(double minNota) {
        this.minNota = minNota;
    }
}
