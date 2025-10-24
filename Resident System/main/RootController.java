package edu.rmit.cosc1295.app;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

//Fade transition provides fade-in and out animation effects, 
//FXML marks methods linked to elements in the FXML file, 
//FXMLLoader loads .fxml file and creates UI elements, 
//Node is the base class for all JavaFX scene graphs, 
//StackPane, a layout container to stack nodes on top of another, 
//duration defines time intervals 

public class RootController {

    @FXML
    private StackPane contentArea; //Defines public class - RootController 
    //FXML tells JavaFX that the variable is linked to an element in the FXML file 
    //ContentArea is a StackPane used for displaying different screens/views dynamically. 

    @FXML
    public void initialize() {
        showDashboardView();   
    //Initialize() method is used to be called by the JavaFX framework after the FXML file is loaded 
    //ShowDashboardView() opens up the dashboard view on the app when it first starts. 

    }

    @FXML
    public void showMainView() {
        loadView("/fxml/main.fxml"); 
        //FXML linked method, when called, displays the main.fxml in the contentArea, and later also loads the Dashboard view on startup 
        
    }

    @FXML
    public void showDashboardView() {
        loadView("/fxml/dashboard.fxml");
    }

    @FXML
    public void showAppointmentView() {
        loadView("/fxml/appointment.fxml");
    }

    @FXML
    private void handleExit() {
        System.exit(0); //System.exit(0) terminates the application and this is linked to the exit button
    }

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);  //Creates Fae transition lasting 400 milliseconds 
            
            FadeTransition fade = new FadeTransition(Duration.millis(400), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception e) {
            e.printStackTrace(); //Catches and prints any errors while loading the FXML file 
            System.out.println("❌ Failed to load: " + fxmlPath + " (" + e.getMessage() + ")");
        }
    }
}
