package com.cinemamanager.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.cinemamanager.dao.HallDao;
import com.cinemamanager.dao.MovieDao;
import com.cinemamanager.dao.ScreeningDao;
import com.cinemamanager.model.Movie;
import com.cinemamanager.model.Screening;

public class ScreeningServiceImpl implements ScreeningService {

	private final ScreeningDao screeningDao;
	private final MovieDao movieDao;
	private final HallDao hallDao;

	public ScreeningServiceImpl(ScreeningDao screeningDao, MovieDao movieDao, HallDao hallDao) {
		this.screeningDao = Objects.requireNonNull(screeningDao);
		this.movieDao = Objects.requireNonNull(movieDao);
		this.hallDao = Objects.requireNonNull(hallDao);
	}

	@Override
	public Screening scheduleScreening(int movieId, int hallId, LocalDateTime startTime) {
		if (startTime == null) {
			throw new IllegalArgumentException("startTime must not be null");
		}

		Movie m = movieDao.findById(movieId);
		if (m == null) {
			throw new IllegalArgumentException("Movie not found: " + movieId);
		}
		if (hallDao.findById(hallId) == null) {
			throw new IllegalArgumentException("Hall not found: " + hallId);
		}

		LocalDateTime endTime = startTime.plusMinutes(m.getDuration());
		if (!endTime.isAfter(startTime)) {
			throw new IllegalArgumentException("Computed endTime must be after startTime");
		}

		// Optional cleaning buffer (uncomment if needed)
		// endTime = endTime.plusMinutes(10);

		if (hasOverlap(hallId, startTime, endTime)) {
			throw new IllegalStateException("Overlap detected for hall " + hallId);
		}

		Screening s = new Screening();
		s.setMovieId(movieId);
		s.setHallId(hallId);
		s.setStartTime(startTime);
		s.setEndTime(endTime);

		screeningDao.save(s);
		return s;
	}

	@Override
	public boolean hasOverlap(int hallId, LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime == null || endTime == null) {
			throw new IllegalArgumentException("startTime and endTime must not be null");
		}
		if (!endTime.isAfter(startTime)) {
			throw new IllegalArgumentException("endTime must be after startTime");
		}
		return screeningDao.existsOverlap(hallId, startTime, endTime);
	}

	@Override
	public List<Screening> findUpcomingByHall(int hallId) {
		LocalDateTime now = LocalDateTime.now();
		// DAO doesn’t have findUpcomingByHall; compose it from findByHallId and filter
		// by time
		return screeningDao.findByHallId(hallId).stream()
				.filter(s -> s.getStartTime() != null && !s.getStartTime().isBefore(now))
				.sorted(Comparator.comparing(Screening::getStartTime)).collect(Collectors.toList());
	}
}
