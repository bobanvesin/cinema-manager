package com.cinemamanager.view;

import com.cinemamanager.controller.AppController;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;

public class MainView extends VBox {

	private final TabPane tabPane = new TabPane();
	private final CustomerView customerView = new CustomerView();
	private final MovieView movieView = new MovieView();
	private final ReservationView reservationView = new ReservationView();
	private final ScheduleScreeningView scheduleScreeningView = new ScheduleScreeningView(); // NEW

	// Menu + items
	private final MenuBar menuBar = new MenuBar();
	private final Menu fileMenu = new Menu("File");
	private final Menu helpMenu = new Menu("Help");

	private final MenuItem checkDbItem = new MenuItem("Check Database Connection");
	private final MenuItem exportItem = new MenuItem("Export");
	private final MenuItem exitItem = new MenuItem("Exit");

	private final MenuItem aboutItem = new MenuItem("About");

	public MainView() {
		// --- Menu bar ---
		menuBar.setUseSystemMenuBar(true);

		// Keyboard shortcuts
		checkDbItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
		exportItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
		exitItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));
		aboutItem.setAccelerator(KeyCombination.keyCombination("F1"));

		fileMenu.getItems().addAll(checkDbItem, exportItem, new SeparatorMenuItem(), exitItem);
		helpMenu.getItems().addAll(aboutItem);
		menuBar.getMenus().addAll(fileMenu, helpMenu);

		// --- Tabs ---
		Tab customersTab = new Tab("Customers", customerView);
		customersTab.setClosable(false);

		Tab moviesTab = new Tab("Movies", movieView);
		moviesTab.setClosable(false);

		Tab reservationsTab = new Tab("Reservations", reservationView);
		reservationsTab.setClosable(false);

		Tab scheduleScreeningTab = new Tab("Schedule Screening", scheduleScreeningView); // NEW
		scheduleScreeningTab.setClosable(false);

		tabPane.getTabs().addAll(customersTab, moviesTab, reservationsTab, scheduleScreeningTab);

		// Layout: menu on top, tabs below
		getChildren().addAll(menuBar, tabPane);

		// Bootstrap controllers after the scene is ready.
		Platform.runLater(() -> new AppController(this).start());
	}

	// --- Getters for controllers/wiring ---
	public CustomerView getCustomerView() {
		return customerView;
	}

	public MovieView getMovieView() {
		return movieView;
	}

	public ReservationView getReservationView() {
		return reservationView;
	}

	public ScheduleScreeningView getScheduleScreeningView() { // NEW
		return scheduleScreeningView;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	// Menu getters so AppController can attach handlers
	public MenuItem getCheckDbMenuItem() {
		return checkDbItem;
	}

	public MenuItem getExportMenuItem() {
		return exportItem;
	}

	public MenuItem getExitMenuItem() {
		return exitItem;
	}

	public MenuItem getAboutMenuItem() {
		return aboutItem;
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}
}
