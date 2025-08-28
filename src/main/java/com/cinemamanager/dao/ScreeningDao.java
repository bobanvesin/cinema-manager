package com.cinemamanager.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.cinemamanager.model.Screening;

public interface ScreeningDao {
	Screening findById(int id);

	List<Screening> findAll();

	List<Screening> findByMovieId(int movieId);

	List<Screening> findByHallId(int hallId);

	List<Screening> findUpcoming(); // e.g. order by startTime >= now

	void save(Screening screening);

	void update(Screening screening);

	void delete(int id);

	/**
	 * Check if there is already a screening in the given hall overlapping the
	 * specified time interval.
	 */
	boolean existsOverlap(int hallId, LocalDateTime startTime, LocalDateTime endTime);
}
