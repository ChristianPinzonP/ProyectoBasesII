module com.example.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // Necesario para conectar con Oracle (JDBC)

    opens com.example.proyecto to javafx.fxml;
    exports com.example.proyecto;
    exports com.example.proyecto.controller;
    opens com.example.proyecto.controller to javafx.fxml;
    exports com.example.proyecto.dao;
    opens com.example.proyecto.dao to javafx.fxml;
}