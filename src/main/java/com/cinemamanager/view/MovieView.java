package com.cinemamanager.view;

import java.util.List;

import com.cinemamanager.model.Movie;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A JavaFX view for displaying and editing movies.
 * 
 * <p>
 * This class provides a table with search functionality and a details panel
 * where users can add, update, or delete movies. The backing data is stored in
 * an observable list with a {@link FilteredList} for live searching.
 * </p>
 * 
 * <p>
 * Typical usage:
 * </p>
 * 
 * <pre>
 * MovieView view = new MovieView();
 * view.setMovies(movieList); // load movies
 * </pre>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public class MovieView extends VBox {

	// --- UI components ---

	/** Table displaying movies. */
	private final TableView<Movie> movieTable = new TableView<>();

	/** Text field for live searching by title. */
	private final TextField searchField = new TextField();

	// Detail fields for the selected movie (editable)
	private final TextField titleField = new TextField();
	private final TextArea descriptionArea = new TextArea();
	private final TextField genreField = new TextField();
	private final TextField languageField = new TextField();
	private final TextField durationField = new TextField();
	private final TextField releaseYearField = new TextField();

	// Action buttons
	private final Button addButton = new Button("Add");
	private final Button updateButton = new Button("Update");
	private final Button deleteButton = new Button("Delete");

	// Backing data
	private final ObservableList<Movie> masterData = FXCollections.observableArrayList();
	private final FilteredList<Movie> filteredData = new FilteredList<>(masterData, p -> true);

	/**
	 * Constructs the movie view.
	 * 
	 * <p>
	 * Initializes the layout with a header, a search bar, a movie table, and a
	 * details panel with input fields and action buttons.
	 * </p>
	 */
	public MovieView() {
		setSpacing(10);
		setPadding(new Insets(15));

		Label header = new Label("Movies");
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		// --- Search box setup ---
		searchField.setPromptText("Search by title...");
		searchField.textProperty().addListener((obs, old, val) -> {
			String query = (val == null) ? "" : val.trim().toLowerCase();
			filteredData.setPredicate(movie -> query.isEmpty() || movie.getTitle().toLowerCase().contains(query));
		});

		movieTable.setPlaceholder(new Label("No movies found (or not loaded yet)."));

		// --- Table setup ---
		TableColumn<Movie, Number> idCol = new TableColumn<>("ID");
		idCol.setPrefWidth(50);
		idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMovieId()));

		TableColumn<Movie, String> titleCol = new TableColumn<>("Title");
		titleCol.setPrefWidth(200);
		titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

		TableColumn<Movie, String> genreCol = new TableColumn<>("Genre");
		genreCol.setPrefWidth(100);
		genreCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

		TableColumn<Movie, String> langCol = new TableColumn<>("Language");
		langCol.setPrefWidth(100);
		langCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLanguage()));

		movieTable.getColumns().addAll(idCol, titleCol, genreCol, langCol);
		movieTable.setItems(filteredData);
		movieTable.setPrefWidth(500);
		movieTable.setPrefHeight(400);

		// When a row is selected, show details on the right
		movieTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			if (selected != null) {
				showMovieDetails(selected);
			} else {
				clearDetails();
			}
		});

		// --- Details panel ---
		GridPane details = new GridPane();
		details.setHgap(10);
		details.setVgap(10);
		details.setPadding(new Insets(10));

		int row = 0;
		details.add(new Label("Title:"), 0, row);
		details.add(titleField, 1, row++);

		details.add(new Label("Description:"), 0, row);
		descriptionArea.setWrapText(true);
		descriptionArea.setPrefRowCount(4);
		details.add(descriptionArea, 1, row++);

		details.add(new Label("Genre:"), 0, row);
		details.add(genreField, 1, row++);

		details.add(new Label("Language:"), 0, row);
		details.add(languageField, 1, row++);

		details.add(new Label("Duration (min):"), 0, row);
		details.add(durationField, 1, row++);

		details.add(new Label("Release Year:"), 0, row);
		details.add(releaseYearField, 1, row++);

		// --- Action buttons ---
		HBox actions = new HBox(10, addButton, updateButton, deleteButton);
		actions.setPadding(new Insets(10, 0, 0, 0));
		VBox rightPane = new VBox(10, details, actions);

		// Layout: table on the left, details+buttons on the right
		HBox centerBox = new HBox(20, movieTable, rightPane);
		centerBox.setPadding(new Insets(10));

		getChildren().addAll(header, searchField, centerBox);
	}

	/**
	 * Replaces the current list of movies with a new list.
	 * 
	 * @param movies list of movies to display
	 */
	public void setMovies(List<Movie> movies) {
		masterData.setAll(movies);
	}

	// --- Private helpers for detail panel ---

	/**
	 * Displays the details of a selected movie in the input fields.
	 * 
	 * @param m the selected movie
	 */
	private void showMovieDetails(Movie m) {
		titleField.setText(m.getTitle());
		descriptionArea.setText(m.getDescription());
		genreField.setText(m.getGenre());
		languageField.setText(m.getLanguage());
		durationField.setText(String.valueOf(m.getDuration()));
		releaseYearField.setText(String.valueOf(m.getReleaseYear()));
	}

	/**
	 * Clears all detail input fields.
	 */
	private void clearDetails() {
		titleField.clear();
		descriptionArea.clear();
		genreField.clear();
		languageField.clear();
		durationField.clear();
		releaseYearField.clear();
	}

	// --- Getters (for controller wiring) ---

	/**
	 * @return the movie table
	 */
	public TableView<Movie> getMovieTable() {
		return movieTable;
	}

	/**
	 * @return the title text field
	 */
	public TextField getTitleField() {
		return titleField;
	}

	/**
	 * @return the description text area
	 */
	public TextArea getDescriptionArea() {
		return descriptionArea;
	}

	/**
	 * @return the genre text field
	 */
	public TextField getGenreField() {
		return genreField;
	}

	/**
	 * @return the language text field
	 */
	public TextField getLanguageField() {
		return languageField;
	}

	/**
	 * @return the duration text field
	 */
	public TextField getDurationField() {
		return durationField;
	}

	/**
	 * @return the release year text field
	 */
	public TextField getReleaseYearField() {
		return releaseYearField;
	}

	/**
	 * @return the "Add" button
	 */
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * @return the "Update" button
	 */
	public Button getUpdateButton() {
		return updateButton;
	}

	/**
	 * @return the "Delete" button
	 */
	public Button getDeleteButton() {
		return deleteButton;
	}
}
