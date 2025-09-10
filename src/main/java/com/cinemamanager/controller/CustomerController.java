package com.cinemamanager.controller;

import com.cinemamanager.dao.CustomerDao;
import com.cinemamanager.dao.CustomerDaoImpl;
import com.cinemamanager.model.Customer;
import com.cinemamanager.view.CustomerView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerController {

	private final CustomerView view;
	private final CustomerDao customerDao;
	private final ObservableList<Customer> customerList;

	public CustomerController(CustomerView view) {
		this.view = view;
		this.customerDao = new CustomerDaoImpl();
		this.customerList = FXCollections.observableArrayList();

		initialize();
	}

	/**
	 * 
	 */
	private void initialize() {
		loadCustomers();

		view.getCustomerTable().setItems(customerList);

		view.getAddButton().setOnAction(e -> addCustomer());
		view.getUpdateButton().setOnAction(e -> updateCustomer());
		view.getDeleteButton().setOnAction(e -> deleteCustomer());

		view.getCustomerTable().setOnMouseClicked(event -> {
			Customer selected = view.getCustomerTable().getSelectionModel().getSelectedItem();
			if (selected != null) {
				view.getFirstNameField().setText(selected.getFirstName());
				view.getLastNameField().setText(selected.getLastName());
				view.getEmailField().setText(selected.getEmail());
			}
		});
	}

	private void loadCustomers() {
		customerList.clear();
		customerList.addAll(customerDao.getAllCustomers());
	}

	private void addCustomer() {
		String first = view.getFirstNameField().getText().trim();
		String last = view.getLastNameField().getText().trim();
		String email = view.getEmailField().getText().trim();

		if (!first.isEmpty() && !last.isEmpty() && !email.isEmpty()) {
			Customer newCustomer = new Customer(first, last, email);
			customerDao.addCustomer(newCustomer);
			loadCustomers();
			clearForm();
		}
	}

	private void updateCustomer() {
		Customer selected = view.getCustomerTable().getSelectionModel().getSelectedItem();
		if (selected != null) {
			selected.setFirstName(view.getFirstNameField().getText().trim());
			selected.setLastName(view.getLastNameField().getText().trim());
			selected.setEmail(view.getEmailField().getText().trim());

			customerDao.updateCustomer(selected);
			loadCustomers();
			clearForm();
		}
	}

	private void deleteCustomer() {
		Customer selected = view.getCustomerTable().getSelectionModel().getSelectedItem();
		if (selected != null) {
			customerDao.deleteCustomer(selected.getId());
			loadCustomers();
			clearForm();
		}
	}

	private void clearForm() {
		view.getFirstNameField().clear();
		view.getLastNameField().clear();
		view.getEmailField().clear();
		view.getCustomerTable().getSelectionModel().clearSelection();
	}
}
