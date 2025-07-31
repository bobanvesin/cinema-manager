package com.cinemamanager.util;

//Util class

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

	private static final String PROPERTIES_FILE = "/db.properties";

	public static Connection getConnection() throws SQLException {
		Properties props = new Properties();
		try (InputStream input = DatabaseConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
			if (input == null) {
				throw new RuntimeException("Cannot find " + PROPERTIES_FILE);
			}
			props.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Error reading database properties", e);
		}

		String url = props.getProperty("db.url");
		String user = props.getProperty("db.user");
		String password = props.getProperty("db.password");

		return DriverManager.getConnection(url, user, password);
	}
}
