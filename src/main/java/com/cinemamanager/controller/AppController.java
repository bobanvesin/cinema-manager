package com.cinemamanager.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;

import com.cinemamanager.dao.CustomerDao;
import com.cinemamanager.dao.CustomerDaoImpl;
import com.cinemamanager.dao.MovieDao;
import com.cinemamanager.dao.MovieDaoImpl;
import com.cinemamanager.dao.ReservationsDao;
import com.cinemamanager.dao.ReservationsDaoImpl;
import com.cinemamanager.model.Customer;
import com.cinemamanager.model.Movie;
import com.cinemamanager.model.Reservation;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.util.DatabaseConnection;
import com.cinemamanager.view.MainView;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

public class AppController {

	private final MainView mainView;
	private Connection conn;

	// DAOs (used by export + reservations controller)
	private CustomerDao customerDao;
	private MovieDao movieDao;
	private ReservationsDao reservationsDao;

	public AppController(MainView mainView) {
		this.mainView = mainView;
	}

	public void start() {
		// one shared connection
		conn = DatabaseConnection.getConnection();

		// controllers
		new CustomerController(mainView.getCustomerView());

		if (conn != null) {
			movieDao = new MovieDaoImpl(conn);
			reservationsDao = new ReservationsDaoImpl(conn);
			customerDao = new CustomerDaoImpl(); // your impl didn't need a Connection

			new MovieController(mainView.getMovieView(), movieDao);
			new ReservationsController(mainView.getReservationView(), reservationsDao, customerDao, movieDao);
		} else {
			// still init DAOs we can (so "Export" can at least export customers)
			customerDao = new CustomerDaoImpl();
		}

		attachMenuHandlers();
	}

	private void attachMenuHandlers() {
		mainView.getCheckDbMenuItem().setOnAction(e -> handleCheckDb());
		mainView.getExportMenuItem().setOnAction(e -> handleExport());
		mainView.getExitMenuItem().setOnAction(e -> Platform.exit());
		mainView.getAboutMenuItem().setOnAction(e -> AlertUtils.showInfo("About Cinema Manager",
				"Cinema Manager\n\nA simple JavaFX + MySQL demo for customers, movies, and reservations."));
	}

	private void handleCheckDb() {
		try (Connection test = DatabaseConnection.getConnection()) {
			if (test != null && !test.isClosed()) {
				AlertUtils.showInfo("Database Connection", "Successfully connected to the database.");
			} else {
				AlertUtils.showError("Could not establish a database connection.");
			}
		} catch (Exception ex) {
			AlertUtils.showError("Failed to connect to the database:\n" + ex.getMessage());
		}
	}

	private void handleExport() {
		Window owner = mainView.getScene() != null ? mainView.getScene().getWindow() : null;
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Export Folder");
		File dir = chooser.showDialog(owner);
		if (dir == null)
			return;

		try {
			// Make sure DAOs exist (movies/reservations require DB)
			if (customerDao == null)
				customerDao = new CustomerDaoImpl();
			if (movieDao == null || reservationsDao == null) {
				// try to open a temporary connection for export
				try (Connection tmp = DatabaseConnection.getConnection()) {
					if (tmp != null) {
						if (movieDao == null)
							movieDao = new MovieDaoImpl(tmp);
						if (reservationsDao == null)
							reservationsDao = new ReservationsDaoImpl(tmp);
					}
				}
			}

			// Export Customers
			List<Customer> customers = customerDao.getAllCustomers();
			File customersCsv = new File(dir, "customers.csv");
			try (BufferedWriter w = Files.newBufferedWriter(customersCsv.toPath(), StandardCharsets.UTF_8)) {
				w.write("id,first_name,last_name,email");
				w.newLine();
				for (Customer c : customers) {
					w.write(csv(c.getId()) + "," + csv(c.getFirstName()) + "," + csv(c.getLastName()) + ","
							+ csv(c.getEmail()));
					w.newLine();
				}
			}

			// Export Movies (if DAO available)
			if (movieDao != null) {
				List<Movie> movies = movieDao.findAll();
				File moviesCsv = new File(dir, "movies.csv");
				try (BufferedWriter w = Files.newBufferedWriter(moviesCsv.toPath(), StandardCharsets.UTF_8)) {
					w.write("movie_id,title,description,genre,language,duration,release_year");
					w.newLine();
					for (Movie m : movies) {
						w.write(csv(m.getMovieId()) + "," + csv(m.getTitle()) + "," + csv(m.getDescription()) + ","
								+ csv(m.getGenre()) + "," + csv(m.getLanguage()) + "," + csv(m.getDuration()) + ","
								+ csv(m.getReleaseYear()));
						w.newLine();
					}
				}
			}

			// Export Reservations (if DAO available)
			if (reservationsDao != null) {
				List<Reservation> reservations = reservationsDao.findAll();
				File reservationsCsv = new File(dir, "reservations.csv");
				try (BufferedWriter w = Files.newBufferedWriter(reservationsCsv.toPath(), StandardCharsets.UTF_8)) {
					w.write("reservation_id,customer_id,screening_id,reservation_time");
					w.newLine();
					for (Reservation r : reservations) {
						String time = r.getReservationTime() != null ? r.getReservationTime().toString() : "";
						w.write(csv(r.getReservationId()) + "," + csv(r.getCustomerId()) + "," + csv(r.getScreeningId())
								+ "," + csv(time));
						w.newLine();
					}
				}
			}

			AlertUtils.showInfo("Export Complete", "CSV files were exported to:\n" + dir.getAbsolutePath());
		} catch (Exception ex) {
			AlertUtils.showError("Export failed:\n" + ex.getMessage());
		}
	}

	// tiny CSV escaper (wraps in quotes if needed, doubles internal quotes)
	private String csv(Object value) {
		String s = value == null ? "" : value.toString();
		boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
		if (s.contains("\""))
			s = s.replace("\"", "\"\"");
		return needsQuotes ? "\"" + s + "\"" : s;
	}

	public void stop() {
		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (Exception ignored) {
		}
	}
}
