package com.example.proyecto;
//Clase Estudiante.java
public class Estudiante extends Usuario {
    private Grupo grupo;
    private String fechaIngresoGrupo;
    private String estadoEnGrupo;

    // Getters y setters adicionales para atributos específicos de Estudiante
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    public String getFechaIngresoGrupo() { return fechaIngresoGrupo; }
    public void setFechaIngresoGrupo(String fechaIngresoGrupo) { this.fechaIngresoGrupo = fechaIngresoGrupo; }

    public String getEstadoEnGrupo() { return estadoEnGrupo; }
    public void setEstadoEnGrupo(String estadoEnGrupo) { this.estadoEnGrupo = estadoEnGrupo; }

    // Método conveniente para ID
    public int getIdEstudiante() { return getIdUsuario(); }
    public void setIdEstudiante(int idEstudiante) { setIdUsuario(idEstudiante); }
}