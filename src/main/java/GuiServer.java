/* ---------------------------------------------------------
 Project 4, Mini Chat Application
 Authors: Jennifer Le, Sebastian Kowalczyk
 Date: 04/28/2023
 Course: CS 342- Software Design

 In this program, we create a small chat application which
 functions off of server and client interaction. The graphical
 elements were accomplished using CSS and FXMl files.
 --------------------------------------------------------- */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiServer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception { //start method for GUI
        try {
            Parent root = FXMLLoader.load(getClass().getResource("startScene.fxml")); // load view into parent
            Scene scene = new Scene(root, 450, 450);
            scene.getStylesheets().add("style1.css"); //add css file to the scene
            stage.setScene(scene); //set the scene
            stage.show(); //show the scene

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}