package com.example.proyecto;
//Clase Docente.java
public class Docente extends Usuario {
    private String asignatura;

    // Getter y setter adicionales para atributos específicos de Docente
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    // Método conveniente para ID
    public int getIdDocente() { return getIdUsuario(); }
    public void setIdDocente(int idDocente) { setIdUsuario(idDocente); }
}