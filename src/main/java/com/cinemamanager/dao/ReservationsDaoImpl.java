package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Reservation;

public class ReservationsDaoImpl implements ReservationsDao {

	private final Connection connection;

	public ReservationsDaoImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Reservation findById(int id) {
		String sql = "SELECT reservation_id, customer_id, screening_id, reservation_time FROM reservation WHERE reservation_id = ?";
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
	public List<Reservation> findAll() {
		List<Reservation> list = new ArrayList<>();
		String sql = "SELECT reservation_id, customer_id, screening_id, reservation_time FROM reservation ORDER BY reservation_time DESC";
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next())
				list.add(mapRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Reservation> findByCustomerId(int customerId) {
		List<Reservation> list = new ArrayList<>();
		String sql = "SELECT reservation_id, customer_id, screening_id, reservation_time FROM reservation WHERE customer_id = ? ORDER BY reservation_time DESC";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, customerId);
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
	public void save(Reservation r) {
		String sql = "INSERT INTO reservation (customer_id, screening_id, reservation_time) VALUES (?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, r.getCustomerId());
			ps.setInt(2, r.getScreeningId());
			// LocalDateTime -> Timestamp
			ps.setTimestamp(3, Timestamp.valueOf(r.getReservationTime()));

			ps.executeUpdate();

			// set generated id back on model (optional, but useful)
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next())
					r.setReservationId(keys.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Reservation r) {
		String sql = "UPDATE reservation SET customer_id = ?, screening_id = ?, reservation_time = ? WHERE reservation_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, r.getCustomerId());
			ps.setInt(2, r.getScreeningId());
			ps.setTimestamp(3, Timestamp.valueOf(r.getReservationTime()));
			ps.setInt(4, r.getReservationId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id) {
		String sql = "DELETE FROM reservation WHERE reservation_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Reservation mapRow(ResultSet rs) throws SQLException {
		Reservation r = new Reservation();
		r.setReservationId(rs.getInt("reservation_id"));
		r.setCustomerId(rs.getInt("customer_id"));
		r.setScreeningId(rs.getInt("screening_id"));
		Timestamp ts = rs.getTimestamp("reservation_time");
		r.setReservationTime(ts != null ? ts.toLocalDateTime() : null);
		return r;
	}
}
