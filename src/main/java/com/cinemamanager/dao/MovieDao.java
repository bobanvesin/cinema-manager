package com.cinemamanager.dao;

import java.util.List;

import com.cinemamanager.model.Movie;

public interface MovieDao {
	Movie findById(int id);

	List<Movie> findAll();

	void save(Movie movie);

	void update(Movie movie);

	void delete(int id);
}
