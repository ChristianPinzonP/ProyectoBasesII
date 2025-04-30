package com.example.proyecto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    //COMENTARIO PARA PODER SUBIR ARCHIVO AL REPOSITORIO
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "root2"; // Usuario según tu configuración en SQL Developer
    private static final String PASSWORD = "root2"; // Reemplaza con la contraseña correcta

    public static Connection getConnection() {
        try {
            // Cargar el driver manualmente
            Class.forName("oracle.jdbc.OracleDriver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa a Oracle");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver JDBC no encontrado.");
            return null;
        } catch (SQLException e) {
            System.err.println("❌ Error en la conexión: " + e.getMessage());
            return null;
        }
    }
}
