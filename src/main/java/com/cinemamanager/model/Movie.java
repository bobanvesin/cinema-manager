package com.cinemamanager.model;

/**
 * Represents a movie in the Cinema Manager application.
 * 
 * <p>
 * This class is a plain data holder (POJO) containing basic information about a
 * movie such as title, description, genre, language, duration, and release
 * year.
 * </p>
 * 
 * <p>
 * It provides getters and setters for all fields so that the data can be
 * accessed and modified as needed by views and controllers.
 * </p>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public class Movie {

	/** Unique identifier of the movie. */
	private int movieId;

	/** Title of the movie. */
	private String title;

	/** Short description or synopsis of the movie. */
	private String description;

	/** Genre of the movie (e.g., Action, Drama, Comedy). */
	private String genre;

	/** Language of the movie (e.g., English, French). */
	private String language;

	/** Duration of the movie in minutes. */
	private int duration;

	/** Release year of the movie. */
	private int releaseYear;

	// --- Getters and Setters ---

	/**
	 * Gets the unique identifier of the movie.
	 * 
	 * @return the movie ID
	 */
	public int getMovieId() {
		return movieId;
	}

	/**
	 * Sets the unique identifier of the movie.
	 * 
	 * @param movieId the new movie ID
	 */
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	/**
	 * Gets the title of the movie.
	 * 
	 * @return the movie title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the movie.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the description of the movie.
	 * 
	 * @return the movie description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the movie.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the genre of the movie.
	 * 
	 * @return the movie genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Sets the genre of the movie.
	 * 
	 * @param genre the new genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * Gets the language of the movie.
	 * 
	 * @return the movie language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language of the movie.
	 * 
	 * @param language the new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the duration of the movie in minutes.
	 * 
	 * @return the movie duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the movie in minutes.
	 * 
	 * @param duration the new duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Gets the release year of the movie.
	 * 
	 * @return the release year
	 */
	public int getReleaseYear() {
		return releaseYear;
	}

	/**
	 * Sets the release year of the movie.
	 * 
	 * @param releaseYear the new release year
	 */
	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}
}
