package com.example.proyecto.controller;

import com.example.proyecto.Estudiante;

//MainEstudianteViewController
public class MainEstudianteViewController {
    private Estudiante estudianteActual;

    // Otros elementos de la interfaz con @FXML

    public void inicializarEstudiante(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        // Inicializar la interfaz con los datos del estudiante
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        // Actualizar elementos de la interfaz con información del estudiante
        // Por ejemplo:
        // lblNombreEstudiante.setText("Bienvenido, " + estudianteActual.getNombre());

        // Mostrar información del grupo si existe
        if (estudianteActual.getGrupo() != null) {
            // lblGrupo.setText("Grupo: " + estudianteActual.getGrupo().getNombre());
        } else {
            // lblGrupo.setText("Sin grupo asignado");
        }
    }

    // Otros métodos del controlador...
    //CONSULTA PARA MOSTRAR TODOS LOS DATOS DEL ESTUDIANTE
    //SELECT ID_USUARIO, NOMBRE, CORREO FROM USUARIO WHERE tipo_usuario = 'Estudiante';
}
