package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Movie;

/**
 * JDBC implementation of the {@link MovieDao} interface.
 * 
 * <p>
 * This class provides database operations for {@link Movie} entities using
 * plain JDBC (Java Database Connectivity). It performs CRUD operations against
 * a relational database table named <code>movie</code>.
 * </p>
 * 
 * <p>
 * Expected schema (simplified):
 * </p>
 * 
 * <pre>
 * CREATE TABLE movie (
 *   movie_id     INT PRIMARY KEY AUTO_INCREMENT,
 *   title        VARCHAR(255),
 *   description  TEXT,
 *   genre        VARCHAR(100),
 *   language     VARCHAR(50),
 *   duration     INT,
 *   release_year INT
 * );
 * </pre>
 *
 * <p>
 * <b>Note:</b> This implementation does not use connection pooling or
 * transactions. In a production system, those concerns should be handled at a
 * higher layer or with frameworks like JPA/Hibernate or Spring JDBC.
 * </p>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public class MovieDaoImpl implements MovieDao {

	/** The JDBC connection used for all database operations. */
	private final Connection connection;

	/**
	 * Constructs a MovieDaoImpl with the given JDBC connection.
	 * 
	 * @param connection the active JDBC connection
	 */
	public MovieDaoImpl(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Finds a movie by its ID.
	 *
	 * @param id the movie ID
	 * @return the matching {@link Movie}, or {@code null} if not found
	 */
	@Override
	public Movie findById(int id) {
		Movie movie = null;
		String query = "SELECT * FROM movie WHERE movie_id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				movie = mapRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movie;
	}

	/**
	 * Retrieves all movies from the database.
	 *
	 * @return a list of movies (empty if none found)
	 */
	@Override
	public List<Movie> findAll() {
		List<Movie> movies = new ArrayList<>();
		String query = "SELECT * FROM movie";
		try (Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				movies.add(mapRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}

	/**
	 * Inserts a new movie into the database.
	 *
	 * @param movie the movie to save
	 */
	@Override
	public void save(Movie movie) {
		String query = "INSERT INTO movie (title, description, genre, language, duration, release_year) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, movie.getTitle());
			stmt.setString(2, movie.getDescription());
			stmt.setString(3, movie.getGenre());
			stmt.setString(4, movie.getLanguage());
			stmt.setInt(5, movie.getDuration());
			stmt.setInt(6, movie.getReleaseYear());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates an existing movie in the database.
	 *
	 * @param movie the movie with updated values (must have a valid ID)
	 */
	@Override
	public void update(Movie movie) {
		String query = "UPDATE movie SET title=?, description=?, genre=?, language=?, duration=?, release_year=? "
				+ "WHERE movie_id=?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, movie.getTitle());
			stmt.setString(2, movie.getDescription());
			stmt.setString(3, movie.getGenre());
			stmt.setString(4, movie.getLanguage());
			stmt.setInt(5, movie.getDuration());
			stmt.setInt(6, movie.getReleaseYear());
			stmt.setInt(7, movie.getMovieId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes a movie from the database.
	 *
	 * @param id the ID of the movie to delete
	 */
	@Override
	public void delete(int id) {
		String query = "DELETE FROM movie WHERE movie_id=?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Maps a single row of the result set to a {@link Movie} object.
	 *
	 * @param rs the result set positioned at a row
	 * @return a populated Movie object
	 * @throws SQLException if a database access error occurs
	 */
	private Movie mapRow(ResultSet rs) throws SQLException {
		Movie movie = new Movie();
		movie.setMovieId(rs.getInt("movie_id"));
		movie.setTitle(rs.getString("title"));
		movie.setDescription(rs.getString("description"));
		movie.setGenre(rs.getString("genre"));
		movie.setLanguage(rs.getString("language"));
		movie.setDuration(rs.getInt("duration"));
		movie.setReleaseYear(rs.getInt("release_year"));
		return movie;
	}
}
