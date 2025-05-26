package com.example.proyecto;

public class ReporteEstudiante {
    private int idEstudiante;
    private String nombre;
    private int cantidadExamenes;
    private double promedio;
    private double maxNota;
    private double minNota;

    // Getters y Setters usados por EstadisticasDAO y la interfaz
    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadExamenes() {
        return cantidadExamenes;
    }

    public void setCantidadExamenes(int cantidadExamenes) {
        this.cantidadExamenes = cantidadExamenes;
    }

    public double getPromedio() {
        return promedio;
    }

    public void setPromedio(double promedio) {
        this.promedio = promedio;
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
