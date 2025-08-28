package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Screening;

public class ScreeningDaoImpl implements ScreeningDao {

	private final Connection connection;

	public ScreeningDaoImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Screening findById(int id) {
		String sql = "SELECT screening_id, movie_id, hall_id, start_time, end_time FROM screening WHERE screening_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Screening> findAll() {
		String sql = "SELECT screening_id, movie_id, hall_id, start_time, end_time FROM screening ORDER BY start_time ASC";
		List<Screening> list = new ArrayList<>();
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next())
				list.add(mapRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Screening> findByMovieId(int movieId) {
		String sql = "SELECT screening_id, movie_id, hall_id, start_time, end_time FROM screening WHERE movie_id = ? ORDER BY start_time ASC";
		List<Screening> list = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, movieId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Screening> findByHallId(int hallId) {
		String sql = "SELECT screening_id, movie_id, hall_id, start_time, end_time FROM screening WHERE hall_id = ? ORDER BY start_time ASC";
		List<Screening> list = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, hallId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Screening> findUpcoming() {
		String sql = "SELECT screening_id, movie_id, hall_id, start_time, end_time "
				+ "FROM screening WHERE start_time >= NOW() ORDER BY start_time ASC";
		List<Screening> list = new ArrayList<>();
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next())
				list.add(mapRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void save(Screening s) {
		String sql = "INSERT INTO screening (movie_id, hall_id, start_time, end_time) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, s.getMovieId());
			ps.setInt(2, s.getHallId());
			ps.setTimestamp(3, Timestamp.valueOf(s.getStartTime()));
			ps.setTimestamp(4, Timestamp.valueOf(s.getEndTime()));
			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next())
					s.setScreeningId(keys.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Screening s) {
		String sql = "UPDATE screening SET movie_id = ?, hall_id = ?, start_time = ?, end_time = ? WHERE screening_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, s.getMovieId());
			ps.setInt(2, s.getHallId());
			ps.setTimestamp(3, Timestamp.valueOf(s.getStartTime()));
			ps.setTimestamp(4, Timestamp.valueOf(s.getEndTime()));
			ps.setInt(5, s.getScreeningId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id) {
		String sql = "DELETE FROM screening WHERE screening_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean existsOverlap(int hallId, LocalDateTime startTime, LocalDateTime endTime) {
		String sql = "SELECT COUNT(*) FROM screening " + "WHERE hall_id = ? " + "AND (start_time < ? AND end_time > ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, hallId);
			ps.setTimestamp(2, Timestamp.valueOf(endTime));
			ps.setTimestamp(3, Timestamp.valueOf(startTime));
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Screening mapRow(ResultSet rs) throws SQLException {
		Screening s = new Screening();
		s.setScreeningId(rs.getInt("screening_id"));
		s.setMovieId(rs.getInt("movie_id"));
		s.setHallId(rs.getInt("hall_id"));
		Timestamp tsStart = rs.getTimestamp("start_time");
		Timestamp tsEnd = rs.getTimestamp("end_time");
		s.setStartTime(tsStart != null ? tsStart.toLocalDateTime() : null);
		s.setEndTime(tsEnd != null ? tsEnd.toLocalDateTime() : null);
		return s;
	}
}
