package com.cinemamanager.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.cinemamanager.model.Hall;
import com.cinemamanager.model.Movie;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public class ScheduleScreeningView extends VBox {

	// --- Form controls (public getters below) ---
	private final ComboBox<Movie> movieCombo = new ComboBox<>();
	private final ComboBox<Hall> hallCombo = new ComboBox<>();
	private final DatePicker datePicker = new DatePicker(LocalDate.now());
	private final Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 19);
	private final Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 30);
	private final TextField priceField = new TextField();

	private final Button scheduleBtn = new Button("Schedule");
	private final Button clearBtn = new Button("Clear");

	private final Label formMessage = new Label(); // for light validation/status

	// --- Table (preview of scheduled screenings; controller can fill it) ---
	private final TableView<ScreeningRow> table = new TableView<>();
	private final ObservableList<ScreeningRow> items = FXCollections.observableArrayList();

	public ScheduleScreeningView() {
		setSpacing(16);
		setPadding(new Insets(16));
		getChildren().addAll(buildHeader(), buildFormCard(), buildTableCard());
		configureForUX();
	}

	private Node buildHeader() {
		Label title = new Label("Schedule Screening");
		title.getStyleClass().add("h2");
		title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		return title;
	}

	private Node buildFormCard() {
		VBox card = new VBox(12);
		card.setPadding(new Insets(12));
		card.setStyle(
				"-fx-background-color: -fx-base; -fx-background-radius: 8; -fx-border-color: -fx-box-border; -fx-border-radius: 8;");

		GridPane grid = new GridPane();
		grid.setHgap(12);
		grid.setVgap(10);

		Label movieLbl = new Label("Movie");
		Label hallLbl = new Label("Hall");
		Label dateLbl = new Label("Date");
		Label timeLbl = new Label("Time");
		Label priceLbl = new Label("Price");

		// Combo display: show title/name while holding the objects
		movieCombo.setPromptText("Select movie");
		movieCombo.setConverter(new StringConverter<Movie>() {
			@Override
			public String toString(Movie m) {
				return m == null ? "" : m.getTitle();
			}

			@Override
			public Movie fromString(String s) {
				return null;
			} // not used
		});
		movieCombo.setCellFactory(list -> new ListCell<>() {
			@Override
			protected void updateItem(Movie m, boolean empty) {
				super.updateItem(m, empty);
				setText(empty || m == null ? null : m.getTitle());
			}
		});

		hallCombo.setPromptText("Select hall");
		hallCombo.setConverter(new StringConverter<Hall>() {
			@Override
			public String toString(Hall h) {
				return h == null ? "" : h.getName();
			}

			@Override
			public Hall fromString(String s) {
				return null;
			}
		});
		hallCombo.setCellFactory(list -> new ListCell<>() {
			@Override
			protected void updateItem(Hall h, boolean empty) {
				super.updateItem(h, empty);
				setText(empty || h == null ? null : h.getName());
			}
		});

		// Movies
		movieCombo.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Movie m, boolean empty) {
				super.updateItem(m, empty);
				setText(empty || m == null ? null : m.getTitle());
			}
		});

		// Halls
		hallCombo.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Hall h, boolean empty) {
				super.updateItem(h, empty);
				setText(empty || h == null ? null : h.getName());
			}
		});

		hourSpinner.setEditable(true);
		minuteSpinner.setEditable(true);
		hourSpinner.setPrefWidth(80);
		minuteSpinner.setPrefWidth(80);

		var priceFormatter = new TextFormatter<>(new DoubleStringConverter(), 120.0, c -> {
			if (c.getControlNewText().matches("\\d*(\\.\\d{0,2})?"))
				return c;
			return null;
		});
		priceField.setTextFormatter(priceFormatter);
		priceField.setPromptText("e.g. 120.00");

		HBox timeBox = new HBox(8, hourSpinner, new Label(":"), minuteSpinner);
		timeBox.setAlignment(Pos.CENTER_LEFT);

		HBox btns = new HBox(8, scheduleBtn, clearBtn);
		btns.setAlignment(Pos.CENTER_LEFT);

		formMessage.setStyle("-fx-text-fill: derive(-fx-accent, -30%);");

		int r = 0;
		grid.add(movieLbl, 0, r);
		grid.add(movieCombo, 1, r++);
		grid.add(hallLbl, 0, r);
		grid.add(hallCombo, 1, r++);
		grid.add(dateLbl, 0, r);
		grid.add(datePicker, 1, r++);
		grid.add(timeLbl, 0, r);
		grid.add(timeBox, 1, r++);
		grid.add(priceLbl, 0, r);
		grid.add(priceField, 1, r++);

		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.NEVER);

		card.getChildren().addAll(grid, btns, formMessage);
		return card;
	}

	private Node buildTableCard() {
		VBox card = new VBox(8);
		card.setPadding(new Insets(12));
		card.setStyle(
				"-fx-background-color: -fx-base; -fx-background-radius: 8; -fx-border-color: -fx-box-border; -fx-border-radius: 8;");

		TableColumn<ScreeningRow, String> movieCol = new TableColumn<>("Movie");
		movieCol.setCellValueFactory(new PropertyValueFactory<>("movie"));

		TableColumn<ScreeningRow, String> hallCol = new TableColumn<>("Hall");
		hallCol.setCellValueFactory(new PropertyValueFactory<>("hall"));

		TableColumn<ScreeningRow, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<ScreeningRow, String> timeCol = new TableColumn<>("Time");
		timeCol.setCellValueFactory(new PropertyValueFactory<>("timeFormatted"));

		TableColumn<ScreeningRow, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

		table.getColumns().addAll(movieCol, hallCol, dateCol, timeCol, priceCol);
		table.setItems(items);
		table.setPlaceholder(new Label("No scheduled screenings yet"));

		Label header = new Label("Upcoming Screenings");
		header.setStyle("-fx-font-weight: bold;");

		card.getChildren().addAll(header, table);
		VBox.setVgrow(table, Priority.ALWAYS);
		return card;
	}

	private void configureForUX() {
		clearBtn.setOnAction(e -> {
			movieCombo.getSelectionModel().clearSelection();
			hallCombo.getSelectionModel().clearSelection();
			datePicker.setValue(LocalDate.now());
			hourSpinner.getValueFactory().setValue(19);
			minuteSpinner.getValueFactory().setValue(30);
			priceField.setText("120.00");
			formMessage.setText("");
		});

		scheduleBtn.setOnAction(e -> {
			Movie movie = movieCombo.getValue();
			Hall hall = hallCombo.getValue();
			LocalDate date = datePicker.getValue();
			Integer h = hourSpinner.getValue();
			Integer m = minuteSpinner.getValue();
			String priceText = priceField.getText();

			if (movie == null || hall == null || date == null || h == null || m == null || isBlank(priceText)) {
				formMessage.setText("Please fill out all fields.");
				return;
			}

			double price;
			try {
				price = Double.parseDouble(priceText);
			} catch (NumberFormatException ex) {
				formMessage.setText("Price must be a number (e.g. 120 or 120.00).");
				return;
			}

			items.add(new ScreeningRow(movie.getTitle(), hall.getName(), date, LocalTime.of(h, m), price));
			table.scrollTo(items.size() - 1);
			formMessage.setText("Screening added to the list.");
		});
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	public void setMovies(List<Movie> movies) {
		List<Movie> safe = (movies == null) ? Collections.emptyList() : movies;
		Runnable r = () -> {
			movieCombo.setItems(FXCollections.observableArrayList(safe));
			// Optional: select first so something is visible immediately
			if (!safe.isEmpty())
				movieCombo.getSelectionModel().selectFirst();
		};
		if (Platform.isFxApplicationThread())
			r.run();
		else
			Platform.runLater(r);
	}

	public void setHalls(List<Hall> halls) {
		List<Hall> safe = (halls == null) ? Collections.emptyList() : halls;
		Runnable r = () -> {
			hallCombo.setItems(FXCollections.observableArrayList(safe));
			if (!safe.isEmpty())
				hallCombo.getSelectionModel().selectFirst();
		};
		if (Platform.isFxApplicationThread())
			r.run();
		else
			Platform.runLater(r);
	}

	// ---------- Public getters for controller wiring ----------
	public ComboBox<Movie> getMovieCombo() {
		return movieCombo;
	}

	public ComboBox<Hall> getHallCombo() {
		return hallCombo;
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public Spinner<Integer> getHourSpinner() {
		return hourSpinner;
	}

	public Spinner<Integer> getMinuteSpinner() {
		return minuteSpinner;
	}

	public TextField getPriceField() {
		return priceField;
	}

	public Button getScheduleButton() {
		return scheduleBtn;
	}

	public Button getClearButton() {
		return clearBtn;
	}

	public TableView<ScreeningRow> getTable() {
		return table;
	}

	public ObservableList<ScreeningRow> getItems() {
		return items;
	}

	public Label getFormMessageLabel() {
		return formMessage;
	}

	// ---------- Table row model ----------
	public static class ScreeningRow {
		private final StringProperty movie = new SimpleStringProperty();
		private final StringProperty hall = new SimpleStringProperty();
		private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
		private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
		private final DoubleProperty price = new SimpleDoubleProperty();

		public ScreeningRow(String movie, String hall, LocalDate date, LocalTime time, double price) {
			this.movie.set(Objects.requireNonNull(movie));
			this.hall.set(Objects.requireNonNull(hall));
			this.date.set(Objects.requireNonNull(date));
			this.time.set(Objects.requireNonNull(time));
			this.price.set(price);
		}

		public String getMovie() {
			return movie.get();
		}

		public StringProperty movieProperty() {
			return movie;
		}

		public String getHall() {
			return hall.get();
		}

		public StringProperty hallProperty() {
			return hall;
		}

		public LocalDate getDate() {
			return date.get();
		}

		public ObjectProperty<LocalDate> dateProperty() {
			return date;
		}

		public LocalTime getTime() {
			return time.get();
		}

		public ObjectProperty<LocalTime> timeProperty() {
			return time;
		}

		public double getPrice() {
			return price.get();
		}

		public DoubleProperty priceProperty() {
			return price;
		}

		public String getTimeFormatted() {
			LocalTime t = getTime();
			return (t == null) ? "" : String.format("%02d:%02d", t.getHour(), t.getMinute());
		}
	}
}
