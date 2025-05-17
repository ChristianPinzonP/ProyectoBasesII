    package com.example.proyecto.controller;

    import com.example.proyecto.Estudiante;
    import com.example.proyecto.Grupo;
    import javafx.fxml.FXML;
    import javafx.scene.control.Label;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.stage.Stage;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;

    public class MainEstudianteViewController {

        @FXML private Label lblBienvenida;
        @FXML private Label lblGrupo;
        @FXML private ImageView logoImage;

        private Estudiante estudiante;

        @FXML
        public void initialize() {
            try {
                logoImage.setImage(new Image(getClass().getResourceAsStream("/images/edusoft_logo.png")));
            } catch (Exception e) {
                System.out.println("Error cargando logo: " + e.getMessage());
            }
        }

        public void inicializarEstudiante(Estudiante estudiante) {
            this.estudiante = estudiante;
            lblBienvenida.setText("Bienvenido, " + estudiante.getNombre());
            Grupo grupo = estudiante.getGrupo();
            if (grupo != null) {
                lblGrupo.setText("Grupo: " + grupo.getNombre());
            } else {
                lblGrupo.setText("Grupo: No asignado");
            }
        }

        @FXML
        public void abrirVistaExamenes() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/EstudianteExamenesView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                EstudianteExamenesController controlador = loader.getController();
                controlador.inicializar(estudiante);

                Stage stage = new Stage();
                stage.setTitle("Exámenes Asignados");
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @FXML
        public void cerrarSesion() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto/LoginView.fxml"));
                Parent root = loader.load();
                Scene loginScene = new Scene(root);

                // AÑADIR HOJA DE ESTILOS
                loginScene.getStylesheets().add(getClass().getResource("/com/example/proyecto/application.css").toExternalForm());

                Stage nuevoStage = new Stage();
                nuevoStage.setTitle("Login - Edusoft");
                nuevoStage.setScene(loginScene);
                nuevoStage.show();

                // Cerrar la ventana actual
                Stage ventanaActual = (Stage) lblBienvenida.getScene().getWindow();
                ventanaActual.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
