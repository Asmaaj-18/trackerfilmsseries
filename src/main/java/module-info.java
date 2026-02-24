module com.example.trackerfilmsseries {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // OUVRIR les packages à JavaFX
    opens com.example.trackerfilmsseries to javafx.fxml;
    opens com.example.trackerfilmsseries.controller to javafx.fxml;
    opens com.example.trackerfilmsseries.model to javafx.base;  // ⭐ AJOUT IMPORTANT

    exports com.example.trackerfilmsseries;
    exports com.example.trackerfilmsseries.controller;
    exports com.example.trackerfilmsseries.model;
}