# Cinema Manager — OAP200V Running Example

A desktop application built with **Java + JavaFX + MySQL** that demonstrates end‑to‑end **Object‑Oriented Analysis, Design, and Programming**. This project is the **running example** for the course **OAP200V – Object‑Oriented Analysis, Design, and Programming**.

---

## Highlights

* Manage **Movies**, **Halls**, **Screenings**, **Customers**, and **Reservations**
* **Schedule screenings** (end time auto‑computed from movie duration; overlap check per hall)
* Make **reservations** against actual screenings
* **Export** CSV reports (customers, movies, reservations)
* Clean layering with **View (JavaFX)**, **Controller**, **DAO (JDBC)**, optional **Service** (business rules)

---

## Project Structure

```
com.cinemamanager
│
├─ MainApp.java               // JavaFX entry point
├─ controller                 // JavaFX controllers (UI orchestration)
├─ model                      // Domain entities (Movie, Hall, Screening, Customer, Reservation)
├─ dao                        // Data Access Objects (JDBC to MySQL)
├─ service                    // Business logic (e.g., overlap rules) — optional but recommended
├─ util                       // Utilities (DatabaseConnection, AlertUtils, etc.)
└─ view                       // JavaFX views (hand‑crafted UI components)
```

**Key ideas**

* **Views** render UI and expose getters/setters for values and controls.
* **Controllers** wire views to DAOs/Services and react to user actions.
* **DAOs** contain persistence only (no UI or business rules).
* **Services** centralize business rules (e.g., “no overlapping screenings”), keeping controllers thin.

---

## Tech Stack

* **Java** 17+ (11+ is possible if JavaFX deps match)
* **JavaFX** (controls, TableView)
* **MySQL** 8.x (or compatible)
* **Maven** (build & dependencies)

---

## Setup

### 1) Clone

```bash
git clone https://github.com/<your-user-or-org>/<your-repo>.git
cd <your-repo>
```

### 2) Create the database

Create a database and user in MySQL:

```sql
CREATE DATABASE cinema_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'cinema_user'@'%' IDENTIFIED BY 'cinema_pass';
GRANT ALL PRIVILEGES ON cinema_manager.* TO 'cinema_user'@'%';
FLUSH PRIVILEGES;
```

Create tables (minimal schema used by the app):

```sql
USE cinema_manager;

CREATE TABLE movie (
  movie_id     INT AUTO_INCREMENT PRIMARY KEY,
  title        VARCHAR(255) NOT NULL,
  description  TEXT,
  genre        VARCHAR(100),
  language     VARCHAR(50),
  duration     INT NOT NULL,          -- minutes
  release_year INT
);

CREATE TABLE hall (
  hall_id   INT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(100) NOT NULL,
  capacity  INT NOT NULL
);

CREATE TABLE screening (
  screening_id INT AUTO_INCREMENT PRIMARY KEY,
  movie_id     INT NOT NULL,
  hall_id      INT NOT NULL,
  start_time   DATETIME NOT NULL,
  end_time     DATETIME NOT NULL,
  CONSTRAINT fk_screening_movie FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
  CONSTRAINT fk_screening_hall  FOREIGN KEY (hall_id)  REFERENCES hall(hall_id),
  INDEX idx_hall_start (hall_id, start_time)
);

CREATE TABLE customer (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  first_name  VARCHAR(100),
  last_name   VARCHAR(100),
  email       VARCHAR(255)
);

CREATE TABLE reservation (
  reservation_id   INT AUTO_INCREMENT PRIMARY KEY,
  customer_id      INT NOT NULL,
  screening_id     INT NOT NULL,
  reservation_time DATETIME,
  CONSTRAINT fk_res_customer  FOREIGN KEY (customer_id) REFERENCES customer(id),
  CONSTRAINT fk_res_screening FOREIGN KEY (screening_id) REFERENCES screening(screening_id)
);
```

Optional seed data:

```sql
INSERT INTO hall(name, capacity) VALUES ('Hall 1', 120), ('Hall 2', 90);

INSERT INTO movie(title, duration, genre, language, release_year) VALUES
('Inception', 148, 'Sci-Fi', 'EN', 2010),
('Interstellar', 169, 'Sci-Fi', 'EN', 2014);
```

### 3) Configure JDBC

Update `com.cinemamanager.util.DatabaseConnection` with your credentials:

```java
// Example — adjust host/port/db/user/pass
String url  = "jdbc:mysql://localhost:3306/cinema_manager?useSSL=false&serverTimezone=UTC";
String user = "cinema_user";
String pass = "cinema_pass";
```

---

## Run

### From IDE (Eclipse/IntelliJ)

* Import as a **Maven** project.
* Run `MainApp`.

### From Maven (if JavaFX plugin is configured)

```bash
mvn clean verify
mvn javafx:run
```

---

## Using the Application

* **Customers**: Manage basic customer info.
* **Movies**: Manage titles, durations, genres, etc.
* **Schedule Screening**:

  1. Pick a movie, hall, and date/time.
  2. The controller computes `end_time = start_time + movie.duration`.
  3. Overlap in the same hall is checked (`ScreeningDao.existsOverlap`).
  4. On success, the screening is **saved to DB** and listed in the table.
* **Reservations**:

  1. Choose a **customer** and an existing **screening**.
  2. Add/update/delete reservations.
* **Export (File → Export)**: Creates CSV files for customers, movies, and reservations.

---

## Notable Classes

* **Views**: `ReservationView`, `ScheduleScreeningView`, `CustomerView`, `MovieView`
* **Controllers**: `ReservationsController`, `SchedullingController`, `CustomerController`, `MovieController`, `AppController`
* **DAOs**: `MovieDao/Impl`, `HallDao/Impl`, `ScreeningDao/Impl`, `ReservationsDao/Impl`, `CustomerDao/Impl`
* **Service (optional)**: `ScreeningService` / `ScreeningServiceImpl` (encapsulates business rules like overlap checks)

---

## Course Context

This repository is the **running example** used throughout **OAP200V – Object‑Oriented Analysis, Design, and Programming** to demonstrate:

* From requirements and use cases to a domain model and layered implementation
* Separation of concerns (View/Controller/DAO/Service)
* Testable business logic
* Practical JavaFX patterns and JDBC DAOs

---

## Development Notes

* Controllers accept **interfaces** (`ScreeningDao`, `HallDao`, etc.); concrete implementations are created in `AppController`.
* Keep **controllers thin**; move complex rules into **services** where it makes sense.
* The **price** field shown in `ScheduleScreeningView` is UI‑only in this version (not persisted). If needed, add a `price` column to `screening` and a `double price` field to the `Screening` model + DAO methods.

---

## Troubleshooting

* **Screening combo is empty** in Reservations tab:

  * The controller loads **upcoming** screenings; if you have none in the future, it falls back to **all**.
  * Ensure `screening.start_time` is in the future (timezone matters).
* **JavaFX not found**:

  * Ensure Maven JavaFX dependencies match your JDK and platform.
* **DB connection issues**:

  * Check JDBC URL, user/pass, and that MySQL is running and reachable.
* **Overlap check always blocking**:

  * Verify your system time vs. DB server time; check the `existsOverlap` SQL where clause.

---

## License

Choose a license (e.g., **MIT**) and add it as `LICENSE`.

---

## Acknowledgements

* JavaFX community & docs
* MySQL & JDBC docs
* Students of **OAP200V** for feedback and test‑driving the example
