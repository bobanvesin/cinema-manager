package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Movie;

public class MovieDaoImpl implements MovieDao {

	private final Connection connection;

	public MovieDaoImpl(Connection connection) {
		this.connection = connection;
	}

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

	@Override
	public void save(Movie movie) {
		String query = "INSERT INTO movie (title, description, genre, language, duration, release_year) VALUES (?, ?, ?, ?, ?, ?)";
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

	@Override
	public void update(Movie movie) {
		String query = "UPDATE movie SET title=?, description=?, genre=?, language=?, duration=?, release_year=? WHERE movie_id=?";
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
