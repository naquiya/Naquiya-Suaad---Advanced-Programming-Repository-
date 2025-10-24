package edu.rmit.cosc1295.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point for the Resident HealthCare System.
 * This version launches the Root Layout which includes
 * sidebar navigation for Dashboard and Appointment Management.
 */
public class MainApp extends Application {

    /** Keep a static reference to the primary stage for navigation */
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        //Loading the Root Layout (root.fxml handles sidebar + main content)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/root.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);

        // Applying window settings
        primaryStage.setTitle("Resident HealthCare System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();

        // Optional: Adding app icon (if available in resources)
        try {
            primaryStage.getIcons().add(
                new Image(getClass().getResourceAsStream("/images/app_icon.png"))
            );
        } catch (Exception e) {
            System.out.println("⚠️ No app icon found, skipping icon load.");
        }

        // Load global CSS to display the start of the system 
        try {
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
            );
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load CSS: " + e.getMessage());
        }

        primaryStage.show();
        System.out.println("✅ Resident HealthCare System started successfully!");
    }

    /** Provides access to the main stage for all controllers */
    public static Stage getStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
