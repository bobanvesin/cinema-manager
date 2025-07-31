package com.cinemamanager.dao;

import java.util.List;

import com.cinemamanager.model.Customer;

public interface CustomerDao {
	void addCustomer(Customer customer);

	void updateCustomer(Customer customer);

	void deleteCustomer(int customerId);

	Customer getCustomerById(int customerId);

	List<Customer> getAllCustomers();
}
