package com.cinemamanager.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.cinemamanager.dao.HallDao;
import com.cinemamanager.dao.MovieDao;
import com.cinemamanager.dao.ScreeningDao;
import com.cinemamanager.model.Hall;
import com.cinemamanager.model.Movie;
import com.cinemamanager.model.Screening;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.view.ScheduleScreeningView;

public class SchedullingController {

	private final ScheduleScreeningView view;
	private final MovieDao movieDao;
	private final HallDao hallDao;
	private final ScreeningDao screeningDao;

	public SchedullingController(ScheduleScreeningView view, MovieDao movieDao, HallDao hallDao,
			ScreeningDao screeningDao) {
		this.view = view;
		this.movieDao = movieDao;
		this.hallDao = hallDao;
		this.screeningDao = screeningDao;
		initialize();
	}

	private void initialize() {
		try {
			List<Movie> movies = movieDao.findAll();
			List<Hall> halls = hallDao.findAll();
			view.setMovies(movies);
			view.setHalls(halls);

			// 🔹 Load existing screenings into the table
			List<Screening> screenings = screeningDao.findAll();
			for (Screening s : screenings) {
				// lookup movie and hall names
				String movieTitle = movies.stream().filter(m -> m.getMovieId() == s.getMovieId()).map(Movie::getTitle)
						.findFirst().orElse("Unknown");

				String hallName = halls.stream().filter(h -> h.getHallId() == s.getHallId()).map(Hall::getName)
						.findFirst().orElse("Unknown");

				view.getItems().add(new ScheduleScreeningView.ScreeningRow(movieTitle, hallName,
						s.getStartTime().toLocalDate(), s.getStartTime().toLocalTime(), 120.0 // TODO: replace if you
																								// persist price later
				));
			}

		} catch (Exception ex) {
			AlertUtils.showError("Failed to load movies/halls/screenings:\n" + ex.getMessage());
		}

		// IMPORTANT: override the UI-only handler from the view with a DB-backed one
		view.getScheduleButton().setOnAction(e -> handleSchedule());
	}

	private void handleSchedule() {
		Movie movie = view.getMovieCombo().getValue();
		Hall hall = view.getHallCombo().getValue();
		LocalDate date = view.getDatePicker().getValue();
		Integer h = view.getHourSpinner().getValue();
		Integer m = view.getMinuteSpinner().getValue();

		// Simple validations
		if (movie == null) {
			AlertUtils.showWarning("Please select a movie.");
			return;
		}
		if (hall == null) {
			AlertUtils.showWarning("Please select a hall.");
			return;
		}
		if (date == null || h == null || m == null) {
			AlertUtils.showWarning("Please choose date and time.");
			return;
		}

		LocalDateTime start = LocalDateTime.of(date, LocalTime.of(h, m));
		LocalDateTime end = start.plusMinutes(movie.getDuration()); // end = start + movie duration

		// Optional: check overlap
		try {
			boolean overlaps = screeningDao.existsOverlap(hall.getHallId(), start, end);
			if (overlaps) {
				AlertUtils.showWarning("There is already a screening that overlaps in this hall.");
				return;
			}
		} catch (Exception ex) {
			AlertUtils.showError("Failed to check overlaps:\n" + ex.getMessage());
			return;
		}

		// Persist screening
		try {
			Screening s = new Screening();
			s.setMovieId(movie.getMovieId());
			s.setHallId(hall.getHallId());
			s.setStartTime(start);
			s.setEndTime(end);

			screeningDao.save(s);

			// Update the UI table (purely presentational)
			double price = parsePriceOrDefault(view.getPriceField().getText(), 120.0); // not saved (see note)
			view.getItems().add(new ScheduleScreeningView.ScreeningRow(movie.getTitle(), hall.getName(), date,
					LocalTime.of(h, m), price));
			view.getTable().scrollTo(view.getItems().size() - 1);
			view.getFormMessageLabel().setText("Screening saved to database.");

		} catch (Exception ex) {
			AlertUtils.showError("Failed to save screening:\n" + ex.getMessage());
		}
	}

	private double parsePriceOrDefault(String text, double def) {
		try {
			return (text == null || text.isBlank()) ? def : Double.parseDouble(text);
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
