package com.example.proyecto;

public class ReporteGrupo {
    private int idGrupo;
    private String grupo;
    private int totalExamenesPresentados;
    private double promedioGeneral;
    private double maxNota;
    private double minNota;

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public int getTotalExamenesPresentados() {
        return totalExamenesPresentados;
    }

    public void setTotalExamenesPresentados(int totalExamenesPresentados) {
        this.totalExamenesPresentados = totalExamenesPresentados;
    }

    public double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
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
