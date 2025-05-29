package com.example.proyecto;

import java.sql.Date;

public class Examen {
    private int id;
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private int tiempoLimite;
    private int idDocente;
    private int idTema;
    private String nombreTema;
    private int idGrupo;
    private String nombreGrupo;

    private int numeroPreguntas;
    private String modoSeleccion;
    private int tiempoPorPregunta;
    private int intentosPermitidos;

    // Constructor completo
    public Examen(int id, String nombre, String descripcion, Date fechaInicio, Date fechaFin,
                  int tiempoLimite, int idDocente, int idTema, int idGrupo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tiempoLimite = tiempoLimite;
        this.idDocente = idDocente;
        this.idTema = idTema;
        this.idGrupo = idGrupo;
    }

    // Constructor vacío
    public Examen() {}

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

    public String getNombreTema() { return nombreTema; }
    public void setNombreTema(String nombreTema) { this.nombreTema = nombreTema; }

    public int getIdGrupo() { return idGrupo; }
    public void setIdGrupo(int idGrupo) { this.idGrupo = idGrupo; }

    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public int getNumeroPreguntas() { return numeroPreguntas; }
    public void setNumeroPreguntas(int numeroPreguntas) { this.numeroPreguntas = numeroPreguntas; }

    public String getModoSeleccion() { return modoSeleccion; }
    public void setModoSeleccion(String modoSeleccion) { this.modoSeleccion = modoSeleccion; }

    public int getTiempoPorPregunta() { return tiempoPorPregunta; }
    public void setTiempoPorPregunta(int tiempoPorPregunta) { this.tiempoPorPregunta = tiempoPorPregunta; }

    public int getIntentosPermitidos() { return intentosPermitidos; }
    public void setIntentosPermitidos(int intentosPermitidos) { this.intentosPermitidos = intentosPermitidos; }
}