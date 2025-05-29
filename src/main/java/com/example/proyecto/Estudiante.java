package com.example.proyecto;

public class Estudiante extends Usuario {
    private Grupo grupo;

    // Getters y setters adicionales para atributos específicos de Estudiante
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    // Método conveniente para ID
    public int getIdEstudiante() { return getIdUsuario(); }
    public void setIdEstudiante(int idEstudiante) { setIdUsuario(idEstudiante); }
}