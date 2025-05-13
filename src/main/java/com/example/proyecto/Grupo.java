package com.example.proyecto;

public class Grupo {
    private int idGrupo;
    private String nombre;

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre; // Solo mostramos el nombre para que aparezca bien en el ComboBox
    }
}
