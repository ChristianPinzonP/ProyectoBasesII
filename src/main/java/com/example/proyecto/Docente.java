package com.example.proyecto;
//Clase Docente.java
public class Docente extends Usuario {
<<<<<<< HEAD
    private String asignatura;

    // Getter y setter adicionales para atributos específicos de Docente
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
=======
    private String departamento;
    private String especialidad;

    // Getter y setter adicionales para atributos específicos de Docente
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
>>>>>>> main

    // Método conveniente para ID
    public int getIdDocente() { return getIdUsuario(); }
    public void setIdDocente(int idDocente) { setIdUsuario(idDocente); }
}