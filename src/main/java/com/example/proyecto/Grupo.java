package com.example.proyecto;
//Clase Grupo.java
<<<<<<< HEAD
public class Grupo extends Docente{
    private int idGrupo;
    private String nombre;
=======
public class Grupo {
    private int idGrupo;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Docente docenteTitular;
>>>>>>> main

    // Getters y setters
    public int getIdGrupo() { return idGrupo; }
    public void setIdGrupo(int idGrupo) { this.idGrupo = idGrupo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
<<<<<<< HEAD
=======

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Docente getDocenteTitular() { return docenteTitular; }
    public void setDocenteTitular(Docente docenteTitular) { this.docenteTitular = docenteTitular; }
>>>>>>> main
}
