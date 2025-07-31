package com.cinemamanager.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerView extends VBox {

	private TableView<com.cinemamanager.model.Customer> customerTable;
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField emailField;
	private Button addButton;
	private Button updateButton;
	private Button deleteButton;

	public CustomerView() {
		setSpacing(10);
		setPadding(new Insets(15));

		Label title = new Label("Customer Management");
		title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		getChildren().add(title);

		// Table
		customerTable = new TableView<>();
		customerTable.setPrefHeight(300);

		TableColumn<com.cinemamanager.model.Customer, Integer> idCol = new TableColumn<>("ID");
		idCol.setPrefWidth(50);
		idCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

		TableColumn<com.cinemamanager.model.Customer, String> firstNameCol = new TableColumn<>("First Name");
		firstNameCol.setPrefWidth(150);
		firstNameCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));

		TableColumn<com.cinemamanager.model.Customer, String> lastNameCol = new TableColumn<>("Last Name");
		lastNameCol.setPrefWidth(150);
		lastNameCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

		TableColumn<com.cinemamanager.model.Customer, String> emailCol = new TableColumn<>("Email");
		emailCol.setPrefWidth(200);
		emailCol.setCellValueFactory(
				data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

		customerTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol);

		getChildren().add(customerTable);

		// Form
		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);

		form.add(new Label("First Name:"), 0, 0);
		firstNameField = new TextField();
		form.add(firstNameField, 1, 0);

		form.add(new Label("Last Name:"), 0, 1);
		lastNameField = new TextField();
		form.add(lastNameField, 1, 1);

		form.add(new Label("Email:"), 0, 2);
		emailField = new TextField();
		form.add(emailField, 1, 2);

		getChildren().add(form);

		// Buttons
		HBox buttons = new HBox(10);
		addButton = new Button("Add");
		updateButton = new Button("Update");
		deleteButton = new Button("Delete");

		buttons.getChildren().addAll(addButton, updateButton, deleteButton);
		getChildren().add(buttons);
	}

	// Getter methods for controller
	public TableView<com.cinemamanager.model.Customer> getCustomerTable() {
		return customerTable;
	}

	public TextField getFirstNameField() {
		return firstNameField;
	}

	public TextField getLastNameField() {
		return lastNameField;
	}

	public TextField getEmailField() {
		return emailField;
	}

	public Button getAddButton() {
		return addButton;
	}

	public Button getUpdateButton() {
		return updateButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}
}
