package com.cinemamanager.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import com.cinemamanager.dao.CustomerDao;
import com.cinemamanager.dao.ReservationsDao;
import com.cinemamanager.dao.ReservationsDaoImpl;
import com.cinemamanager.dao.ScreeningDao;
import com.cinemamanager.dao.ScreeningDaoImpl;
import com.cinemamanager.model.Customer;
import com.cinemamanager.model.Reservation;
import com.cinemamanager.model.Screening;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.view.ReservationView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReservationsController {

	private final ReservationView view;
	private final ReservationsDao reservationsDao;
	private final CustomerDao customerDao;
	private final ScreeningDao screeningDao;

	private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

	public ReservationsController(ReservationView view, ReservationsDao reservationsDao, CustomerDao customerDao,
			ScreeningDao screeningDao) {
		this.view = view;
		this.reservationsDao = reservationsDao;
		this.customerDao = customerDao;
		this.screeningDao = screeningDao;
		initialize();
	}

	public static ReservationsController withConnection(ReservationView view, Connection connection,
			CustomerDao customerDao) {
		return new ReservationsController(view, new ReservationsDaoImpl(connection), customerDao,
				new ScreeningDaoImpl(connection));
	}

	private void initialize() {
		try {
			view.setCustomers(customerDao.getAllCustomers());

			// Try upcoming first; if none, show all so the combo isn’t empty
			List<Screening> screenings = screeningDao.findUpcoming();
			if (screenings == null || screenings.isEmpty()) {
				screenings = screeningDao.findAll();
			}
			view.setScreenings(screenings);

		} catch (Exception ex) {
			AlertUtils.showError("Failed to load customers/screenings:\n" + ex.getMessage());
		}

		loadReservations();
		view.getReservationTable().setItems(reservationList);

		view.getAddButton().setOnAction(e -> addReservation());
		view.getUpdateButton().setOnAction(e -> updateReservation());
		view.getDeleteButton().setOnAction(e -> deleteReservation());

		view.getReservationTable().getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSel, sel) -> onReservationSelected(sel));
	}

	private void loadReservations() {
		reservationList.setAll(reservationsDao.findAll());
	}

	/** If screenings change elsewhere, call this to repopulate the combo. */
	public void refreshScreenings() {
		List<Screening> screenings = screeningDao.findUpcoming();
		if (screenings == null || screenings.isEmpty()) {
			screenings = screeningDao.findAll();
		}
		view.setScreenings(screenings);
	}

	private void onReservationSelected(Reservation sel) {
		if (sel == null) {
			view.clearForm();
			return;
		}

		// Select matching customer from current combo items
		view.getCustomerCombo().getItems().stream().filter(c -> c.getId() == sel.getCustomerId()).findFirst()
				.ifPresent(c -> view.getCustomerCombo().getSelectionModel().select(c));

		// Select matching screening from current combo items
		view.getScreeningCombo().getItems().stream().filter(s -> s.getScreeningId() == sel.getScreeningId()).findFirst()
				.ifPresent(s -> view.getScreeningCombo().getSelectionModel().select(s));
	}

	private void addReservation() {
		Customer customer = view.getSelectedCustomer();
		Screening screening = view.getSelectedScreening();

		if (customer == null) {
			AlertUtils.showWarning("Please select a customer.");
			return;
		}
		if (screening == null) {
			AlertUtils.showWarning("Please select a screening.");
			return;
		}

		try {
			Reservation r = new Reservation();
			r.setCustomerId(customer.getId());
			r.setScreeningId(screening.getScreeningId());
			r.setReservationTime(LocalDateTime.now());

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
		Screening screening = view.getSelectedScreening();

		if (customer == null) {
			AlertUtils.showWarning("Please select a customer.");
			return;
		}
		if (screening == null) {
			AlertUtils.showWarning("Please select a screening.");
			return;
		}

		try {
			selected.setCustomerId(customer.getId());
			selected.setScreeningId(screening.getScreeningId());
			selected.setReservationTime(LocalDateTime.now());

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
