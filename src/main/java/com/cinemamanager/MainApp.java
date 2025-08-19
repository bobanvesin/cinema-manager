package com.cinemamanager;

import com.cinemamanager.view.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		MainView mainView = new MainView(); // controllers bootstrapped inside MainView
		Scene scene = new Scene(mainView, 1200, 600);
		primaryStage.setTitle("Cinema Manager");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
