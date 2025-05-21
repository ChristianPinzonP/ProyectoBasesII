package com.example.proyecto.sesion;

import com.example.proyecto.Docente;

public class SesionUsuario {
    private static Docente docenteActual;

    public static void setDocenteActual(Docente docente) {
        docenteActual = docente;
    }

    public static Docente getDocenteActual() {
        return docenteActual;
    }

    public static void cerrarSesion() {
        docenteActual = null;
    }
}
