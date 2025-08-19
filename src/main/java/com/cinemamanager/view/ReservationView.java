package com.cinemamanager.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.cinemamanager.model.Customer;
import com.cinemamanager.model.Movie;
import com.cinemamanager.model.Reservation;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReservationView extends VBox {

	// Top controls (selection/form)
	private final ComboBox<Customer> customerCombo = new ComboBox<>();
	private final ComboBox<Movie> movieCombo = new ComboBox<>();
	private final DatePicker datePicker = new DatePicker();
	private final Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 19);
	private final Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);

	// Action buttons
	private final Button addButton = new Button("Add");
	private final Button updateButton = new Button("Update");
	private final Button deleteButton = new Button("Delete");

	// Table
	private final TableView<Reservation> reservationTable = new TableView<>();
	private final ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

	public ReservationView() {
		setSpacing(10);
		setPadding(new Insets(15));

		Label header = new Label("Reservations");
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		// --- Customer & Movie pickers
		customerCombo.setPromptText("Select customer");
		customerCombo.setMinWidth(220);
		// show customer nicely: First Last (email) — assuming Customer has these
		// getters
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

		movieCombo.setPromptText("Select movie");
		movieCombo.setMinWidth(220);
		movieCombo.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Movie m, boolean empty) {
				super.updateItem(m, empty);
				setText(empty || m == null ? null : m.getTitle() + " (" + m.getReleaseYear() + ")");
			}
		});
		movieCombo.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Movie m, boolean empty) {
				super.updateItem(m, empty);
				setText(empty || m == null ? "Select movie" : m.getTitle() + " (" + m.getReleaseYear() + ")");
			}
		});

		// --- Date & Time
		datePicker.setPromptText("Date");
		hourSpinner.setEditable(true);
		minuteSpinner.setEditable(true);

		HBox whenRow = new HBox(10, new Label("Date:"), datePicker, new Label("Time:"), hourSpinner, new Label(":"),
				minuteSpinner);

		// --- Actions
		HBox actions = new HBox(10, addButton, updateButton, deleteButton);

		// --- Form layout (top)
		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);
		form.add(new Label("Customer:"), 0, 0);
		form.add(customerCombo, 1, 0);
		form.add(new Label("Movie:"), 0, 1);
		form.add(movieCombo, 1, 1);
		form.add(whenRow, 1, 2);

		VBox rightPane = new VBox(12, form, actions);
		rightPane.setPrefWidth(420);

		// --- Table (left)
		reservationTable.setPlaceholder(new Label("No reservations yet."));
		reservationTable.setItems(reservationData);
		reservationTable.setPrefHeight(420);

		TableColumn<Reservation, Number> idCol = new TableColumn<>("ID");
		idCol.setPrefWidth(70);
		idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getReservationId()));

		TableColumn<Reservation, Number> customerIdCol = new TableColumn<>("Customer ID");
		customerIdCol.setPrefWidth(110);
		customerIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCustomerId()));

		TableColumn<Reservation, Number> screeningIdCol = new TableColumn<>("Screening ID");
		screeningIdCol.setPrefWidth(110);
		screeningIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getScreeningId()));

		TableColumn<Reservation, String> timeCol = new TableColumn<>("Reservation Time");
		timeCol.setPrefWidth(180);
		timeCol.setCellValueFactory(c -> new SimpleStringProperty(
				c.getValue().getReservationTime() != null ? c.getValue().getReservationTime().toString() : ""));

		reservationTable.getColumns().addAll(idCol, customerIdCol, screeningIdCol, timeCol);

		// --- Layout: table left, form right
		HBox center = new HBox(20, reservationTable, rightPane);
		getChildren().addAll(header, center);

		// When a row is selected, reflect it into the form (best-effort; needs
		// controller to look up objects)
		reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
			if (sel == null) {
				clearForm();
				return;
			}
			// Leave customer/movie selection to controller (it can map IDs to actual
			// objects).
			// We only set date/time here:
			if (sel.getReservationTime() != null) {
				LocalDateTime dt = sel.getReservationTime();
				datePicker.setValue(dt.toLocalDate());
				hourSpinner.getValueFactory().setValue(dt.getHour());
				minuteSpinner.getValueFactory().setValue(dt.getMinute());
			}
		});
	}

	// --- Public API for controller ---

	/** Replaces all rows in the table. */
	public void setReservations(List<Reservation> reservations) {
		reservationData.setAll(reservations);
	}

	/** Provide customers to choose from. */
	public void setCustomers(List<Customer> customers) {
		customerCombo.setItems(FXCollections.observableArrayList(customers));
	}

	/** Provide movies to choose from. */
	public void setMovies(List<Movie> movies) {
		movieCombo.setItems(FXCollections.observableArrayList(movies));
	}

	/** Returns the currently selected customer (may be null). */
	public Customer getSelectedCustomer() {
		return customerCombo.getSelectionModel().getSelectedItem();
	}

	/** Returns the currently selected movie (may be null). */
	public Movie getSelectedMovie() {
		return movieCombo.getSelectionModel().getSelectedItem();
	}

	/**
	 * Returns a composed LocalDateTime from the pickers (may be null if date
	 * missing).
	 */
	public LocalDateTime getSelectedDateTime() {
		LocalDate date = datePicker.getValue();
		if (date == null)
			return null;
		Integer h = hourSpinner.getValue();
		Integer m = minuteSpinner.getValue();
		if (h == null)
			h = 0;
		if (m == null)
			m = 0;
		return LocalDateTime.of(date, LocalTime.of(h, m));
	}

	/** Clears form inputs. */
	public void clearForm() {
		customerCombo.getSelectionModel().clearSelection();
		movieCombo.getSelectionModel().clearSelection();
		datePicker.setValue(null);
		hourSpinner.getValueFactory().setValue(0);
		minuteSpinner.getValueFactory().setValue(0);
		reservationTable.getSelectionModel().clearSelection();
	}

	// Getters for buttons & table
	public Button getAddButton() {
		return addButton;
	}

	public Button getUpdateButton() {
		return updateButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public TableView<Reservation> getReservationTable() {
		return reservationTable;
	}
}
