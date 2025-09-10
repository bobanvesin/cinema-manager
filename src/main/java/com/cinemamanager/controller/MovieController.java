package com.cinemamanager.controller;

import java.sql.Connection;

import com.cinemamanager.dao.MovieDao;
import com.cinemamanager.dao.MovieDaoImpl;
import com.cinemamanager.model.Movie;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.view.MovieView;

/**
 * Controller class for handling user interactions in the {@link MovieView}.
 * 
 * <p>
 * This controller connects the UI (view) with the data access layer (DAO). It
 * listens for button actions (Add, Update, Delete), retrieves user input from
 * the view, validates it, and delegates persistence operations to the
 * {@link MovieDao} implementation.
 * </p>
 * 
 * <p>
 * It also uses {@link AlertUtils} to display feedback to the user, such as
 * success messages, warnings, or error dialogs.
 * </p>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public class MovieController {

	/** The view (UI) this controller manages. */
	private final MovieView view;

	/** DAO used for persistence operations on movies. */
	private final MovieDao movieDao;

	/**
	 * Constructs a controller with an explicit view and DAO.
	 *
	 * @param view     the movie view
	 * @param movieDao the data access object for movies
	 */
	public MovieController(MovieView view, MovieDao movieDao) {
		this.view = view;
		this.movieDao = movieDao;
		initialize();
	}

	/**
	 * Constructs a controller with a view and a JDBC connection.
	 * 
	 * <p>
	 * This constructor creates a {@link MovieDaoImpl} internally.
	 * </p>
	 *
	 * @param view       the movie view
	 * @param connection the JDBC connection to be wrapped in a DAO
	 */
	public MovieController(MovieView view, Connection connection) {
		this(view, new MovieDaoImpl(connection));
	}

	/**
	 * Initializes the controller.
	 * 
	 * <p>
	 * Binds button actions in the view to controller methods and loads the initial
	 * movie list.
	 * </p>
	 */
	private void initialize() {
		refresh();
		view.getAddButton().setOnAction(e -> addMovie());
		view.getUpdateButton().setOnAction(e -> updateSelected());
		view.getDeleteButton().setOnAction(e -> deleteSelected());
	}

	/**
	 * Refreshes the movie list in the view.
	 * 
	 * <p>
	 * Fetches movies from the DAO and updates the table in the UI.
	 * </p>
	 * Displays an error alert if loading fails.
	 */
	public void refresh() {
		try {
			view.setMovies(movieDao.findAll());
		} catch (Exception ex) {
			AlertUtils.showError("Failed to load movies:\n" + ex.getMessage());
		}
	}

	/**
	 * Adds a new movie based on the input fields in the view.
	 * 
	 * <p>
	 * Validates that the title is provided and that duration/release year are
	 * numeric values. If validation passes, the movie is saved via the DAO, and a
	 * confirmation is shown. Otherwise, a warning or error alert is displayed.
	 * </p>
	 */
	private void addMovie() {
		try {
			String title = view.getTitleField().getText().trim();
			String desc = view.getDescriptionArea().getText().trim();
			String genre = view.getGenreField().getText().trim();
			String lang = view.getLanguageField().getText().trim();

			int duration = Integer.parseInt(view.getDurationField().getText().trim());
			int year = Integer.parseInt(view.getReleaseYearField().getText().trim());

			if (title.isEmpty()) {
				AlertUtils.showWarning("Title is required.");
				return;
			}

			Movie newMovie = new Movie();
			newMovie.setTitle(title);
			newMovie.setDescription(desc);
			newMovie.setGenre(genre);
			newMovie.setLanguage(lang);
			newMovie.setDuration(duration);
			newMovie.setReleaseYear(year);

			movieDao.save(newMovie);
			AlertUtils.showInfo("Movie Added", "Movie \"" + title + "\" added successfully.");
			refresh();

		} catch (NumberFormatException nfe) {
			AlertUtils.showWarning("Duration and Release Year must be numbers.");
		} catch (Exception ex) {
			AlertUtils.showError("Failed to add movie:\n" + ex.getMessage());
		}
	}

	/**
	 * Updates the currently selected movie in the table.
	 * 
	 * <p>
	 * If no movie is selected, a warning is shown. Otherwise, the details are read
	 * from the input fields, validated, and persisted via the DAO. A confirmation
	 * is shown on success.
	 * </p>
	 */
	private void updateSelected() {
		Movie selected = view.getMovieTable().getSelectionModel().getSelectedItem();
		if (selected == null) {
			AlertUtils.showWarning("Please select a movie to update.");
			return;
		}

		try {
			selected.setTitle(view.getTitleField().getText().trim());
			selected.setDescription(view.getDescriptionArea().getText().trim());
			selected.setGenre(view.getGenreField().getText().trim());
			selected.setLanguage(view.getLanguageField().getText().trim());
			selected.setDuration(Integer.parseInt(view.getDurationField().getText().trim()));
			selected.setReleaseYear(Integer.parseInt(view.getReleaseYearField().getText().trim()));

			movieDao.update(selected);
			AlertUtils.showInfo("Movie Updated", "Changes to \"" + selected.getTitle() + "\" saved.");
			refresh();
		} catch (NumberFormatException nfe) {
			AlertUtils.showWarning("Duration and Release Year must be numbers.");
		} catch (Exception ex) {
			AlertUtils.showError("Failed to update movie:\n" + ex.getMessage());
		}
	}

	/**
	 * Deletes the currently selected movie from the table.
	 * 
	 * <p>
	 * If no movie is selected, a warning is shown. Otherwise, the movie is deleted
	 * via the DAO, and the list is refreshed.
	 * </p>
	 */
	private void deleteSelected() {
		Movie selected = view.getMovieTable().getSelectionModel().getSelectedItem();
		if (selected == null) {
			AlertUtils.showWarning("Please select a movie to delete.");
			return;
		}
		try {
			movieDao.delete(selected.getMovieId());
			AlertUtils.showInfo("Movie Deleted", "Movie \"" + selected.getTitle() + "\" deleted successfully.");
			refresh();
		} catch (Exception ex) {
			AlertUtils.showError("Failed to delete movie:\n" + ex.getMessage());
		}
	}
}
