package com.example.proyecto;

public class Docente extends Usuario {
    private String departamento;
    private String especialidad;

    // Getter y setter adicionales para atributos específicos de Docente
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    // Método conveniente para ID
    public int getIdDocente() { return getIdUsuario(); }
    public void setIdDocente(int idDocente) { setIdUsuario(idDocente); }
}