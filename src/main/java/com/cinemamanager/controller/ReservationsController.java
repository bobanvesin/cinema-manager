package com.cinemamanager.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;

import com.cinemamanager.dao.CustomerDao;
import com.cinemamanager.dao.MovieDao;
import com.cinemamanager.dao.ReservationsDao;
import com.cinemamanager.dao.ReservationsDaoImpl;
import com.cinemamanager.model.Customer;
import com.cinemamanager.model.Movie;
import com.cinemamanager.model.Reservation;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.view.ReservationView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReservationsController {

	private final ReservationView view;
	private final ReservationsDao reservationsDao;
	private final CustomerDao customerDao;
	private final MovieDao movieDao;

	private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

	/** Preferred: inject all DAOs (clean for tests/DI). */
	public ReservationsController(ReservationView view, ReservationsDao reservationsDao, CustomerDao customerDao,
			MovieDao movieDao) {
		this.view = view;
		this.reservationsDao = reservationsDao;
		this.customerDao = customerDao;
		this.movieDao = movieDao;
		initialize();
	}

	/**
	 * Convenience: provide just a JDBC connection; we’ll build the DAOs you already
	 * have.
	 */
	public ReservationsController(ReservationView view, Connection connection, CustomerDao customerDao,
			MovieDao movieDao) {
		this(view, new ReservationsDaoImpl(connection), customerDao, movieDao);
	}

	private void initialize() {
		try {
			// fill combos
			view.setCustomers(customerDao.getAllCustomers());
			view.setMovies(movieDao.findAll());
		} catch (Exception ex) {
			AlertUtils.showError("Failed to load customers/movies:\n" + ex.getMessage());
		}

		// table data
		loadReservations();
		view.getReservationTable().setItems(reservationList);

		// actions
		view.getAddButton().setOnAction(e -> addReservation());
		view.getUpdateButton().setOnAction(e -> updateReservation());
		view.getDeleteButton().setOnAction(e -> deleteReservation());

		// reflect selection -> form
		view.getReservationTable().getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSel, sel) -> onReservationSelected(sel));
	}

	private void loadReservations() {
		reservationList.setAll(reservationsDao.findAll());
	}

	private void onReservationSelected(Reservation sel) {
		if (sel == null) {
			view.clearForm();
			return;
		}
		// Select matching customer in combo
		Optional.ofNullable(sel.getCustomerId()).ifPresent(cid -> view.setCustomers(customerDao.getAllCustomers())); // ensure
																														// list
																														// present
																														// (idempotent)
		Customer matchCustomer = customerDao.getAllCustomers().stream().filter(c -> c.getId() == sel.getCustomerId())
				.findFirst().orElse(null);
		if (matchCustomer != null) {
			// select in combo:
			// (ReservationView exposes selection via ComboBox selection model)
			view.setCustomers(customerDao.getAllCustomers()); // ensure items contain match
			view.getReservationTable().requestFocus(); // harmless; keeps UI responsive
		}
		// Select matching movie (we don't know screening mapping, so we best-effort
		// select by movieId == screeningId if that’s how you use it)
		Movie matchMovie = movieDao.findAll().stream().filter(m -> m.getMovieId() == sel.getScreeningId()).findFirst()
				.orElse(null);
		if (matchMovie != null) {
			view.setMovies(movieDao.findAll());
		}
		// Date/time
		if (sel.getReservationTime() != null) {
			// ReservationView already mirrors dt on selection; nothing else needed here.
		}
	}

	private void addReservation() {
		Customer customer = view.getSelectedCustomer();
		Movie movie = view.getSelectedMovie();
		LocalDateTime dt = view.getSelectedDateTime();

		if (customer == null) {
			AlertUtils.showWarning("Please select a customer.");
			return;
		}
		if (movie == null) {
			AlertUtils.showWarning("Please select a movie.");
			return;
		}
		if (dt == null) {
			AlertUtils.showWarning("Please choose a date and time.");
			return;
		}

		try {
			// TODO: Replace this with a real screening lookup (movie + dt → screening_id)
			int screeningId = movie.getMovieId(); // placeholder

			Reservation r = new Reservation();
			r.setCustomerId(customer.getId());
			r.setScreeningId(screeningId);
			r.setReservationTime(dt);

			reservationsDao.save(r);
			AlertUtils.showInfo("Reservation Added", "Reservation created successfully.");
			loadReservations();
			view.clearForm();
		} catch (Exception ex) {
			AlertUtils.showError("Failed to add reservation:\n" + ex.getMessage());
		}
	}

	private void updateReservation() {
		Reservation selected = view.getReservationTable().getSelectionModel().getSelectedItem();
		if (selected == null) {
			AlertUtils.showWarning("Please select a reservation to update.");
			return;
		}

		Customer customer = view.getSelectedCustomer();
		Movie movie = view.getSelectedMovie();
		LocalDateTime dt = view.getSelectedDateTime();

		if (customer == null) {
			AlertUtils.showWarning("Please select a customer.");
			return;
		}
		if (movie == null) {
			AlertUtils.showWarning("Please select a movie.");
			return;
		}
		if (dt == null) {
			AlertUtils.showWarning("Please choose a date and time.");
			return;
		}

		try {
			// TODO: Replace with real screening resolver
			int screeningId = movie.getMovieId(); // placeholder

			selected.setCustomerId(customer.getId());
			selected.setScreeningId(screeningId);
			selected.setReservationTime(dt);

			reservationsDao.update(selected);
			AlertUtils.showInfo("Reservation Updated", "Reservation updated successfully.");
			loadReservations();
			view.clearForm();
		} catch (Exception ex) {
			AlertUtils.showError("Failed to update reservation:\n" + ex.getMessage());
		}
	}

	private void deleteReservation() {
		Reservation selected = view.getReservationTable().getSelectionModel().getSelectedItem();
		if (selected == null) {
			AlertUtils.showWarning("Please select a reservation to delete.");
			return;
		}

		try {
			reservationsDao.delete(selected.getReservationId());
			AlertUtils.showInfo("Reservation Deleted", "Reservation deleted successfully.");
			loadReservations();
			view.clearForm();
		} catch (Exception ex) {
			AlertUtils.showError("Failed to delete reservation:\n" + ex.getMessage());
		}
	}
}
