package com.cinemamanager.view;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.cinemamanager.model.Customer;
import com.cinemamanager.model.Reservation;
import com.cinemamanager.model.Screening;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReservationView extends VBox {

	// Top controls (selection/form)
	private final ComboBox<Customer> customerCombo = new ComboBox<>();
	private final ComboBox<Screening> screeningCombo = new ComboBox<>();

	// Action buttons
	private final Button addButton = new Button("Add");
	private final Button updateButton = new Button("Update");
	private final Button deleteButton = new Button("Delete");
	private final Button refreshButton = new Button("Refresh Table");

	// Table
	private final TableView<Reservation> reservationTable = new TableView<>();
	private final ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

	// Customers list for lookup
	private List<Customer> customers;

	// Date formatting
	private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public ReservationView() {
		setSpacing(10);
		setPadding(new Insets(15));

		Label header = new Label("Reservations");
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		// --- Customer picker
		customerCombo.setPromptText("Select customer");
		customerCombo.setMinWidth(260);
		customerCombo.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Customer c, boolean empty) {
				super.updateItem(c, empty);
				setText(empty || c == null ? null
						: c.getFirstName() + " " + c.getLastName() + " (" + c.getEmail() + ")");
			}
		});
		customerCombo.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Customer c, boolean empty) {
				super.updateItem(c, empty);
				setText(empty || c == null ? "Select customer"
						: c.getFirstName() + " " + c.getLastName() + " (" + c.getEmail() + ")");
			}
		});

		// --- Screening picker
		screeningCombo.setPromptText("Select screening");
		screeningCombo.setMinWidth(360);
		screeningCombo.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Screening s, boolean empty) {
				super.updateItem(s, empty);
				setText(formatScreeningForCombo(s, empty));
			}
		});
		screeningCombo.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Screening s, boolean empty) {
				super.updateItem(s, empty);
				setText(formatScreeningForCombo(s, empty));
			}
		});

		// --- Actions
		HBox actions = new HBox(10, addButton, updateButton, deleteButton);

		// --- Form layout (top right)
		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);
		form.add(new Label("Customer:"), 0, 0);
		form.add(customerCombo, 1, 0);
		form.add(new Label("Screening:"), 0, 1);
		form.add(screeningCombo, 1, 1);

		VBox rightPane = new VBox(12, form, actions);
		rightPane.setPrefWidth(480);

		// --- Table (left)
		reservationTable.setPlaceholder(new Label("No reservations yet."));
		reservationTable.setItems(reservationData);
		reservationTable.setPrefHeight(420);

		TableColumn<Reservation, Number> idCol = new TableColumn<>("ID");
		idCol.setPrefWidth(70);
		idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getReservationId()));

		TableColumn<Reservation, String> customerNameCol = new TableColumn<>("Customer");
		customerNameCol.setPrefWidth(200);
		customerNameCol.setCellValueFactory(c -> {
			int customerId = c.getValue().getCustomerId();
			if (customers != null) {
				return new SimpleStringProperty(customers.stream().filter(cust -> cust.getId() == customerId)
						.map(cust -> cust.getFirstName() + " " + cust.getLastName()).findFirst().orElse("Unknown"));
			} else {
				return new SimpleStringProperty("Unknown");
			}
		});

		TableColumn<Reservation, Number> screeningIdCol = new TableColumn<>("Screening ID");
		screeningIdCol.setPrefWidth(110);
		screeningIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getScreeningId()));

		TableColumn<Reservation, String> timeCol = new TableColumn<>("Reservation Time");
		timeCol.setPrefWidth(180);
		timeCol.setCellValueFactory(c -> new SimpleStringProperty(
				c.getValue().getReservationTime() != null ? c.getValue().getReservationTime().toString() : ""));

		reservationTable.getColumns().addAll(idCol, customerNameCol, screeningIdCol, timeCol);

		// --- Table with refresh button below it
		HBox refreshBar = new HBox(refreshButton);
		refreshBar.setAlignment(Pos.CENTER_RIGHT);
		VBox tableWithRefresh = new VBox(10, reservationTable, refreshBar);

		// --- Layout: table + refresh (left), form (right)
		HBox center = new HBox(20, tableWithRefresh, rightPane);
		getChildren().addAll(header, center);

		// Reflect selected row -> form
		reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
			if (sel == null) {
				clearForm();
				return;
			}
			if (sel.getCustomerId() > 0 && customers != null) {
				customers.stream().filter(cust -> cust.getId() == sel.getCustomerId()).findFirst()
						.ifPresent(cust -> customerCombo.getSelectionModel().select(cust));
			}
			if (sel.getScreeningId() > 0) {
				screeningCombo.getItems().stream().filter(s -> s.getScreeningId() == sel.getScreeningId()).findFirst()
						.ifPresent(s -> screeningCombo.getSelectionModel().select(s));
			}
		});
	}

	// --- Helpers
	private String formatScreeningForCombo(Screening s, boolean empty) {
		if (empty || s == null)
			return null;

		String start = s.getStartTime() == null ? "?" : DT.format(s.getStartTime());
		String end = s.getEndTime() == null ? "?" : DT.format(s.getEndTime());

		// Better label: "Movie X @ Hall Y (start → end)"
		return "Movie #" + s.getMovieId() + " @ Hall #" + s.getHallId() + " (" + start + " → " + end + ")";
	}

	// --- Public API
	public void setReservations(List<Reservation> reservations) {
		reservationData.setAll(reservations);
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
		customerCombo.setItems(FXCollections.observableArrayList(customers));
		if (!customers.isEmpty())
			customerCombo.getSelectionModel().selectFirst();
	}

	public void setScreenings(List<Screening> screenings) {
		screeningCombo.setItems(FXCollections.observableArrayList(screenings));
		if (!screenings.isEmpty())
			screeningCombo.getSelectionModel().selectFirst();
	}

	public Customer getSelectedCustomer() {
		return customerCombo.getSelectionModel().getSelectedItem();
	}

	public Screening getSelectedScreening() {
		return screeningCombo.getSelectionModel().getSelectedItem();
	}

	public void clearForm() {
		customerCombo.getSelectionModel().clearSelection();
		screeningCombo.getSelectionModel().clearSelection();
		reservationTable.getSelectionModel().clearSelection();
	}

	// Getters
	public Button getAddButton() {
		return addButton;
	}

	public Button getUpdateButton() {
		return updateButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public Button getRefreshButton() {
		return refreshButton;
	}

	public TableView<Reservation> getReservationTable() {
		return reservationTable;
	}

	public ComboBox<Customer> getCustomerCombo() {
		return customerCombo;
	}

	public ComboBox<Screening> getScreeningCombo() {
		return screeningCombo;
	}
}
