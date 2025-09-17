package com.cinemamanager;

import com.cinemamanager.view.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Cinema Manager application.
 * 
 * <p>
 * This class extends {@link javafx.application.Application} and launches a
 * JavaFX GUI for managing cinema operations. It initializes the main view, sets
 * up the application window (stage), and displays the scene to the user.
 * </p>
 * 
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 *     java com.cinemamanager.MainApp
 * </pre>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public class MainApp extends Application {

	/**
	 * Initializes and displays the primary stage of the JavaFX application.
	 * 
	 * <p>
	 * This method is automatically called by the JavaFX runtime after the
	 * application is launched. It sets up the {@link MainView}, creates a scene,
	 * and shows it inside the primary stage (window).
	 * </p>
	 *
	 * @param primaryStage the main window of the application, provided by the
	 *                     JavaFX runtime
	 */
	@Override
	public void start(Stage primaryStage) {
		// Create the main view (layout + controllers are bootstrapped inside MainView)
		MainView mainView = new MainView();

		System.out.print("jshdgfvjy ");

		// Define the scene with specified width and height
		Scene scene = new Scene(mainView, 1200, 600);

		// Set up the main stage (window title, scene, and show the window)
		primaryStage.setTitle("Cinema Manager");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Launches the Cinema Manager application.
	 * 
	 * <p>
	 * This method delegates to {@link Application#launch(String...)} which
	 * initializes the JavaFX runtime and eventually calls {@link #start(Stage)}.
	 * </p>
	 *
	 * @param args command-line arguments (not used here, but may be passed to
	 *             JavaFX runtime)
	 */
	public static void main(String[] args) {
		launch(args); // Launch the JavaFX application
	}
}
