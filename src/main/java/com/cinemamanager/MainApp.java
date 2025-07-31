package com.cinemamanager;

import com.cinemamanager.controller.CustomerController;
import com.cinemamanager.view.CustomerView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		CustomerView view = new CustomerView();
		new CustomerController(view); // wires up logic

		Scene scene = new Scene(view, 600, 500);
		primaryStage.setTitle("Cinema Manager - Customer Management");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
