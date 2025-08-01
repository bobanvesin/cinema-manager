package com.cinemamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cinemamanager.model.Customer;
import com.cinemamanager.util.AlertUtils;
import com.cinemamanager.util.DatabaseConnection;

public class CustomerDaoImpl implements CustomerDao {

	@Override
	public void addCustomer(Customer customer) {
		String sql = "INSERT INTO customer (first_name, last_name, email) VALUES (?, ?, ?)";

		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			AlertUtils.showError("Failed to add customer. No database connection.");
			return;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, customer.getFirstName());
			stmt.setString(2, customer.getLastName());
			stmt.setString(3, customer.getEmail());
			stmt.executeUpdate();
		} catch (SQLException e) {
			AlertUtils.showError("Error while adding customer: " + e.getMessage());
		}
	}

	@Override
	public void updateCustomer(Customer customer) {
		String sql = "UPDATE customer SET first_name = ?, last_name = ?, email = ? WHERE customer_id = ?";

		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			AlertUtils.showError("Failed to update customer. No database connection.");
			return;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, customer.getFirstName());
			stmt.setString(2, customer.getLastName());
			stmt.setString(3, customer.getEmail());
			stmt.setInt(4, customer.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			AlertUtils.showError("Error while updating customer: " + e.getMessage());
		}
	}

	@Override
	public void deleteCustomer(int customerId) {
		String sql = "DELETE FROM customer WHERE customer_id = ?";

		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			AlertUtils.showError("Failed to delete customer. No database connection.");
			return;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			AlertUtils.showError("Error while deleting customer: " + e.getMessage());
		}
	}

	@Override
	public Customer getCustomerById(int customerId) {
		String sql = "SELECT * FROM customer WHERE customer_id = ?";
		Customer customer = null;

		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			AlertUtils.showError("Failed to fetch customer. No database connection.");
			return null;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				customer = new Customer(rs.getInt("customer_id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getString("email"));
			}
		} catch (SQLException e) {
			AlertUtils.showError("Error while retrieving customer: " + e.getMessage());
		}

		return customer;
	}

	@Override
	public List<Customer> getAllCustomers() {
		String sql = "SELECT * FROM customer";
		List<Customer> customers = new ArrayList<>();

		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			AlertUtils.showError("Failed to load customers. No database connection.");
			return customers;
		}

		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Customer customer = new Customer(rs.getInt("customer_id"), rs.getString("first_name"),
						rs.getString("last_name"), rs.getString("email"));
				customers.add(customer);
			}

		} catch (SQLException e) {
			AlertUtils.showError("Error while loading customers: " + e.getMessage());
		}

		return customers;
	}
}
