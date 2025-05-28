package com.example.proyecto.reportes;

public class ReporteExamen {
    private int idExamen;
    private String nombreExamen;
    private int totalPresentaciones;
    private double promedioNota;
    private double maxNota;
    private double minNota;
    private String nombre;

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }

    public String getNombreExamen() {
        return nombreExamen;
    }

    public void setNombreExamen(String nombreExamen) {
        this.nombreExamen = nombreExamen;
    }

    public int getTotalPresentaciones() {
        return totalPresentaciones;
    }

    public void setTotalPresentaciones(int totalPresentaciones) {
        this.totalPresentaciones = totalPresentaciones;
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
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
