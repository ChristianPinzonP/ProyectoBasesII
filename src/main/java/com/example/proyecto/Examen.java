package com.example.proyecto;

import java.sql.Date;

public class Examen {
    private int id;
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private int tiempoLimite;
    private int idDocente; // Relación con la tabla Usuario (Docente)
    private int idTema;

    public Examen(int id, String nombre, String descripcion, Date fechaInicio, Date fechaFin, int tiempoLimite, int idDocente) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tiempoLimite = tiempoLimite;
        this.idDocente = idDocente;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public int getTiempoLimite() { return tiempoLimite; }
    public void setTiempoLimite(int tiempoLimite) { this.tiempoLimite = tiempoLimite; }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public int getIdTema() { return idTema; }
    public void setIdTema(int idTema) { this.idTema = idTema; }

}

