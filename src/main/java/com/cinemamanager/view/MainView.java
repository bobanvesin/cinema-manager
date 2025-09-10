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

/**
 * The main view of the Cinema Manager application.
 * 
 * <p>
 * This class defines the overall user interface layout: a menu bar on top and a
 * tab pane below containing views for customers, movies, reservations, and
 * schedule screenings.
 * </p>
 *
 * <p>
 * It also initializes the {@link AppController} once the JavaFX scene graph is
 * ready, connecting the view with the application logic.
 * </p>
 * 
 * @author Boban Vesin
 * @version 1.0
 */
public class MainView extends VBox {

	// --- Main UI components ---
	private final TabPane tabPane = new TabPane();

	// Child views for each tab
	private final CustomerView customerView = new CustomerView();
	private final MovieView movieView = new MovieView();
	private final ReservationView reservationView = new ReservationView();
	private final ScheduleScreeningView scheduleScreeningView = new ScheduleScreeningView(); // NEW tab

	// Menu bar and menus
	private final MenuBar menuBar = new MenuBar();
	private final Menu fileMenu = new Menu("File");
	private final Menu helpMenu = new Menu("Help");

	// Menu items under "File"
	private final MenuItem checkDbItem = new MenuItem("Check Database Connection");
	private final MenuItem exportItem = new MenuItem("Export");
	private final MenuItem exitItem = new MenuItem("Exit");

	// Menu item under "Help"
	private final MenuItem aboutItem = new MenuItem("About");

	/**
	 * Constructs the main view.
	 * 
	 * <p>
	 * Initializes the menu bar, creates tabs for each sub-view, and arranges them
	 * in a vertical layout. Also sets up keyboard shortcuts and launches the
	 * {@link AppController} once the scene is loaded.
	 * </p>
	 */
	public MainView() {
		// --- Menu bar setup ---
		menuBar.setUseSystemMenuBar(true);

		// Keyboard shortcuts (accelerators)
		checkDbItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
		exportItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
		exitItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));
		aboutItem.setAccelerator(KeyCombination.keyCombination("F1"));

		// Add items to menus
		fileMenu.getItems().addAll(checkDbItem, exportItem, new SeparatorMenuItem(), exitItem);
		helpMenu.getItems().addAll(aboutItem);

		// Add menus to the menu bar
		menuBar.getMenus().addAll(fileMenu, helpMenu);

		// --- Tab setup ---
		Tab customersTab = new Tab("Customers", customerView);
		customersTab.setClosable(false);

		Tab moviesTab = new Tab("Movies", movieView);
		moviesTab.setClosable(false);

		Tab reservationsTab = new Tab("Reservations", reservationView);
		reservationsTab.setClosable(false);

		Tab scheduleScreeningTab = new Tab("Schedule Screening", scheduleScreeningView);
		scheduleScreeningTab.setClosable(false);

		// Add all tabs to the tab pane
		tabPane.getTabs().addAll(customersTab, moviesTab, reservationsTab, scheduleScreeningTab);

		// Layout: menu bar on top, tab pane below
		getChildren().addAll(menuBar, tabPane);

		// Bootstrap the controller AFTER the scene graph is ready
		Platform.runLater(() -> new AppController(this).start());
	}

	// --- Getters for sub-views (for controller wiring) ---

	/**
	 * @return the customer view
	 */
	public CustomerView getCustomerView() {
		return customerView;
	}

	/**
	 * @return the movie view
	 */
	public MovieView getMovieView() {
		return movieView;
	}

	/**
	 * @return the reservation view
	 */
	public ReservationView getReservationView() {
		return reservationView;
	}

	/**
	 * @return the schedule screening view
	 */
	public ScheduleScreeningView getScheduleScreeningView() {
		return scheduleScreeningView;
	}

	/**
	 * @return the tab pane that contains all sub-views
	 */
	public TabPane getTabPane() {
		return tabPane;
	}

	// --- Getters for menu items (for attaching event handlers) ---

	/**
	 * @return the "Check Database Connection" menu item
	 */
	public MenuItem getCheckDbMenuItem() {
		return checkDbItem;
	}

	/**
	 * @return the "Export" menu item
	 */
	public MenuItem getExportMenuItem() {
		return exportItem;
	}

	/**
	 * @return the "Exit" menu item
	 */
	public MenuItem getExitMenuItem() {
		return exitItem;
	}

	/**
	 * @return the "About" menu item
	 */
	public MenuItem getAboutMenuItem() {
		return aboutItem;
	}

	/**
	 * @return the main menu bar
	 */
	public MenuBar getMenuBar() {
		return menuBar;
	}
}
