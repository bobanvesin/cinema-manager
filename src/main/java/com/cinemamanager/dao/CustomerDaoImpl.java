package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Customer;
import com.cinemamanager.util.DatabaseConnection;

public class CustomerDaoImpl implements CustomerDao {

	@Override
	public void addCustomer(Customer customer) {
		String sql = "INSERT INTO customer (first_name, last_name, email) VALUES (?, ?, ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, customer.getFirstName());
			stmt.setString(2, customer.getLastName());
			stmt.setString(3, customer.getEmail());

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); // consider logging
		}
	}

	@Override
	public void updateCustomer(Customer customer) {
		String sql = "UPDATE customer SET first_name = ?, last_name = ?, email = ? WHERE id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, customer.getFirstName());
			stmt.setString(2, customer.getLastName());
			stmt.setString(3, customer.getEmail());
			stmt.setInt(4, customer.getId());

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); // consider logging
		}
	}

	@Override
	public void deleteCustomer(int customerId) {
		String sql = "DELETE FROM customer WHERE id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, customerId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); // consider logging
		}
	}

	@Override
	public Customer getCustomerById(int customerId) {
		String sql = "SELECT * FROM customer WHERE id = ?";
		Customer customer = null;

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, customerId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				customer = new Customer(rs.getInt("customer_id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getString("email"));
			}

		} catch (SQLException e) {
			e.printStackTrace(); // consider logging
		}

		return customer;
	}

	@Override
	public List<Customer> getAllCustomers() {
		String sql = "SELECT * FROM customer";
		List<Customer> customers = new ArrayList<>();

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Customer customer = new Customer(rs.getInt("customer_id"), rs.getString("first_name"),
						rs.getString("last_name"), rs.getString("email"));
				customers.add(customer);
			}

		} catch (SQLException e) {
			e.printStackTrace(); // consider logging
		}

		return customers;
	}
}
