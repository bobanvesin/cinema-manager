package com.cinemamanager.dao;

import java.util.List;

import com.cinemamanager.model.Hall;

public interface HallDao {
	Hall findById(int id);

	List<Hall> findAll();

	void save(Hall hall);

	void update(Hall hall);

	void delete(int id);
}
