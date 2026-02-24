package com.example.trackerfilmsseries;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.net.URL;

import com.example.trackerfilmsseries.service.ApiClient;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        // 🔥 Test connexion base de données
        try {
            Connection conn = ApiClient.getConnection();
            System.out.println("Connexion Neon réussie !");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/trackerfilmsseries/view/main-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 1000, 650);

            // 🔥 Charger le CSS (sécurisé)
            URL cssUrl = getClass().getResource(
                    "/com/example/trackerfilmsseries/view/app.css"
            );

            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS chargé avec succès !");
            } else {
                System.out.println("⚠ app.css non trouvé !");
            }

            stage.setTitle("🎬 Tracker Films & Séries");
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}