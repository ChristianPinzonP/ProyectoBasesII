package com.example.proyecto;

import javafx.beans.property.*;

public class EstadisticaExamen {
    private final StringProperty nombreExamen;
    private final DoubleProperty nota;
    private final StringProperty fecha;

    public EstadisticaExamen(String nombreExamen, double nota, String fecha) {
        this.nombreExamen = new SimpleStringProperty(nombreExamen);
        this.nota = new SimpleDoubleProperty(nota);
        this.fecha = new SimpleStringProperty(fecha);
    }

    public String getNombreExamen() { return nombreExamen.get(); }
    public double getNota() { return nota.get(); }
    public String getFecha() { return fecha.get(); }

    public StringProperty nombreExamenProperty() { return nombreExamen; }
    public DoubleProperty notaProperty() { return nota; }
    public StringProperty fechaProperty() { return fecha; }
}
