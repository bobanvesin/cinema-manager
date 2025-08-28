package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Hall;

public class HallDaoImpl implements HallDao {

	private final Connection connection;

	public HallDaoImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Hall findById(int id) {
		String sql = "SELECT hall_id, name, capacity FROM hall WHERE hall_id = ?";
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
	public List<Hall> findAll() {
		List<Hall> list = new ArrayList<>();
		String sql = "SELECT hall_id, name, capacity FROM hall ORDER BY name ASC";
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next())
				list.add(mapRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void save(Hall h) {
		String sql = "INSERT INTO hall (name, capacity) VALUES (?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, h.getName());
			ps.setInt(2, h.getCapacity());
			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next())
					h.setHallId(keys.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Hall h) {
		String sql = "UPDATE hall SET name = ?, capacity = ? WHERE hall_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, h.getName());
			ps.setInt(2, h.getCapacity());
			ps.setInt(3, h.getHallId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id) {
		String sql = "DELETE FROM hall WHERE hall_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Hall mapRow(ResultSet rs) throws SQLException {
		Hall h = new Hall();
		h.setHallId(rs.getInt("hall_id"));
		h.setName(rs.getString("name"));
		h.setCapacity(rs.getInt("capacity"));
		return h;
	}
}
