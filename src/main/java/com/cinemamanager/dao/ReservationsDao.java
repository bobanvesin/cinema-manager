package com.cinemamanager.dao;

import java.util.List;

import com.cinemamanager.model.Reservation;

public interface ReservationsDao {
	Reservation findById(int id);

	List<Reservation> findAll();

	List<Reservation> findByCustomerId(int customerId);

	void save(Reservation reservation);

	void update(Reservation reservation);

	void delete(int id);
}
