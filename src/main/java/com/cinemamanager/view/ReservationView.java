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

	// Table
	private final TableView<Reservation> reservationTable = new TableView<>();
	private final ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

	// Formatting for screening display
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

		// --- Screening picker (uses Screening fields directly)
		screeningCombo.setPromptText("Select screening");
		screeningCombo.setMinWidth(360);
		screeningCombo.setCellFactory(cb -> new ListCell<>() {
			@Override
			protected void updateItem(Screening s, boolean empty) {
				super.updateItem(s, empty);
				setText(formatScreeningCell(s, empty));
			}
		});
		screeningCombo.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Screening s, boolean empty) {
				super.updateItem(s, empty);
				setText(empty || s == null ? "Select screening" : basicScreeningLabel(s));
			}
		});

		// --- Actions
		HBox actions = new HBox(10, addButton, updateButton, deleteButton);

		// --- Form layout (top)
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

		// Reflect selected row -> form (best-effort by IDs)
		reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
			if (sel == null) {
				clearForm();
				return;
			}
			// Try to select matching customer
			if (sel.getCustomerId() > 0) {
				customerCombo.getItems().stream().filter(c -> c.getId() == sel.getCustomerId()).findFirst()
						.ifPresent(c -> customerCombo.getSelectionModel().select(c));
			}
			// Try to select matching screening
			if (sel.getScreeningId() > 0) {
				screeningCombo.getItems().stream().filter(s -> s.getScreeningId() == sel.getScreeningId()).findFirst()
						.ifPresent(s -> screeningCombo.getSelectionModel().select(s));
			}
		});
	}

	// --- Helpers to format screening cells ---
	private String formatScreeningCell(Screening s, boolean empty) {
		if (empty || s == null)
			return null;
		return basicScreeningLabel(s);
	}

	private String basicScreeningLabel(Screening s) {
		String start = s.getStartTime() == null ? "?" : DT.format(s.getStartTime());
		String end = s.getEndTime() == null ? "?" : DT.format(s.getEndTime());
		return "Movie #" + s.getMovieId() + " | Hall #" + s.getHallId() + " | " + start + " → " + end;
	}

	// --- Public API for controller ---

	/** Replaces all rows in the table. */
	public void setReservations(List<Reservation> reservations) {
		reservationData.setAll(reservations);
	}

	/** Provide customers to choose from. */
	public void setCustomers(List<Customer> customers) {
		customerCombo.setItems(FXCollections.observableArrayList(customers));
		if (!customers.isEmpty())
			customerCombo.getSelectionModel().selectFirst();
	}

	/** Provide screenings to choose from. */
	public void setScreenings(List<Screening> screenings) {
		screeningCombo.setItems(FXCollections.observableArrayList(screenings));
		if (!screenings.isEmpty())
			screeningCombo.getSelectionModel().selectFirst();
	}

	/** Returns the currently selected customer (may be null). */
	public Customer getSelectedCustomer() {
		return customerCombo.getSelectionModel().getSelectedItem();
	}

	/** Returns the currently selected screening (may be null). */
	public Screening getSelectedScreening() {
		return screeningCombo.getSelectionModel().getSelectedItem();
	}

	/** Clears form inputs. */
	public void clearForm() {
		customerCombo.getSelectionModel().clearSelection();
		screeningCombo.getSelectionModel().clearSelection();
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

	/** Expose the customer ComboBox for selection control in the controller. */
	public ComboBox<Customer> getCustomerCombo() {
		return customerCombo;
	}

	/** Expose the screening ComboBox for selection control in the controller. */
	public ComboBox<Screening> getScreeningCombo() {
		return screeningCombo;
	}
}
