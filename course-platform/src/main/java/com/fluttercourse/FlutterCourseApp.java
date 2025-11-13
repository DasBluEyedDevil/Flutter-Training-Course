package com.fluttercourse;

import com.fluttercourse.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX Application for Flutter Course Platform
 */
public class FlutterCourseApp extends Application {

    private static final String APP_TITLE = "Flutter Training Course Platform";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create main controller without FXML (simpler approach)
            MainController mainController = new MainController();
            BorderPane root = mainController.getView();

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Load CSS if available
            var cssResource = getClass().getResource("/css/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
