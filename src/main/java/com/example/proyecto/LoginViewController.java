    package com.example.proyecto;

    import javafx.fxml.FXML;
    import javafx.scene.control.Alert;
    import javafx.scene.control.PasswordField;
    import javafx.scene.control.TextField;
    import javafx.stage.Stage;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;

    public class LoginViewController {
        @FXML private TextField txtCorreo;
        @FXML private PasswordField txtContrasena;

        @FXML
        public void login() {
            String correo = txtCorreo.getText().trim();
            String contrasena = txtContrasena.getText().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                mostrarAlerta("Campos vacíos", "Por favor ingresa correo y contraseña.", Alert.AlertType.WARNING);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT tipo_usuario FROM Usuario WHERE correo = ? AND contrasena = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, correo);
                stmt.setString(2, contrasena);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String tipoUsuario = rs.getString("tipo_usuario");
                    mostrarAlerta("Éxito", "Inicio de sesión exitoso como " + tipoUsuario, Alert.AlertType.INFORMATION);

                    // Cerrar ventana de login
                    Stage stage = (Stage) txtCorreo.getScene().getWindow();
                    stage.close();

                    // Abrir la ventana según tipo de usuario
                    if (tipoUsuario.equals("Docente")) {
                        abrirVista("/com/example/proyecto/MainDocenteView.fxml", "Panel Docente");
                    } else if (tipoUsuario.equals("Estudiante")) {
                        abrirVista("/com/example/proyecto/MainEstudianteView.fxml", "Panel Estudiante"); // para el futuro
                    }
                } else {
                    mostrarAlerta("Error", "Correo o contraseña incorrectos.", Alert.AlertType.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Ocurrió un error al iniciar sesión.", Alert.AlertType.ERROR);
            }
        }

        private void abrirVista(String rutaFXML, String titulo) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        }

        private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }
