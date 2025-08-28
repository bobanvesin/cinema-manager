package com.cinemamanager.service;

import java.time.LocalDateTime;
import java.util.List;

import com.cinemamanager.model.Screening;

public interface ScreeningService {
	Screening scheduleScreening(int movieId, int hallId, LocalDateTime startTime);

	boolean hasOverlap(int hallId, LocalDateTime startTime, LocalDateTime endTime);

	List<Screening> findUpcomingByHall(int hallId);
}
