package com.cinemamanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		Label label = new Label("Hello, Cinema Manager App!");
		Scene scene = new Scene(label, 400, 200);
		primaryStage.setTitle("Cinema Manager");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
