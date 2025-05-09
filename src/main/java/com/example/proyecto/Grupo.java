package com.example.proyecto;
//Clase Grupo.java
public class Grupo {
    private int idGrupo;
    private String nombre;

    // Getters y setters
    public int getIdGrupo() { return idGrupo; }
    public void setIdGrupo(int idGrupo) { this.idGrupo = idGrupo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Grupo{" +
                "idGrupo=" + idGrupo +
                ", nombre='" + nombre +
                '}';
    }
}
