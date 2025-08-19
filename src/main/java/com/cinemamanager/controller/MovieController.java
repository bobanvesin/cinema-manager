package com.cinemamanager.controller;

import java.sql.Connection;

import com.cinemamanager.dao.MovieDao;
import com.cinemamanager.dao.MovieDaoImpl;
import com.cinemamanager.model.Movie;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.view.MovieView;

public class MovieController {

	private final MovieView view;
	private final MovieDao movieDao;

	public MovieController(MovieView view, MovieDao movieDao) {
		this.view = view;
		this.movieDao = movieDao;
		initialize();
	}

	public MovieController(MovieView view, Connection connection) {
		this(view, new MovieDaoImpl(connection));
	}

	private void initialize() {
		refresh();
		view.getAddButton().setOnAction(e -> addMovie());
		view.getUpdateButton().setOnAction(e -> updateSelected());
		view.getDeleteButton().setOnAction(e -> deleteSelected());
	}

	public void refresh() {
		try {
			view.setMovies(movieDao.findAll());
		} catch (Exception ex) {
			AlertUtils.showError("Failed to load movies:\n" + ex.getMessage());
		}
	}

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
