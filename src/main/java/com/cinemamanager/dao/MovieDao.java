package com.cinemamanager.dao;

import java.util.List;

import com.cinemamanager.model.Movie;

/**
 * Data Access Object (DAO) interface for {@link Movie} entities.
 * 
 * <p>
 * This interface defines the standard CRUD (Create, Read, Update, Delete)
 * operations for managing movies in the persistence layer (e.g., database, file
 * storage, or an in-memory collection).
 * </p>
 * 
 * <p>
 * Concrete implementations of this interface will provide the actual storage
 * and retrieval logic.
 * </p>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public interface MovieDao {

	/**
	 * Finds a movie by its unique identifier.
	 * 
	 * @param id the unique ID of the movie
	 * @return the movie with the given ID, or {@code null} if not found
	 */
	Movie findById(int id);

	/**
	 * Retrieves all movies from the data store.
	 * 
	 * @return a list of all movies, or an empty list if none are found
	 */
	List<Movie> findAll();

	/**
	 * Saves a new movie to the data store.
	 * 
	 * @param movie the movie to save
	 */
	void save(Movie movie);

	/**
	 * Updates an existing movie in the data store.
	 * 
	 * @param movie the movie with updated information
	 */
	void update(Movie movie);

	/**
	 * Deletes a movie from the data store.
	 * 
	 * @param id the ID of the movie to delete
	 */
	void delete(int id);
}
