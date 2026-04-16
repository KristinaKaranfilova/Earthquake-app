# Earthquake Monitor

A full-stack web application for fetching, filtering, storing, and visualizing recent earthquake data from the [USGS Earthquake Hazards Program](https://earthquake.usgs.gov).

The backend pulls live GeoJSON data from the USGS public API (last hour, magnitude > 2.0), persists it to PostgreSQL, and exposes a REST API. The frontend provides a table view with magnitude-color coding, magnitude and time filters, and per-record deletion.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.5 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Frontend | React 18, Vite, Bootstrap 5 |
| HTTP Client (FE) | Axios |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc, H2 |

---

## Project Structure

```
Earthquake-app/
├── backend/                  # Spring Boot project
│   ├── src/main/java/com/example/earthquake/
│   │   ├── config/           # AppConfig (RestTemplate bean), CorsConfig
│   │   ├── controller/       # EarthquakeController
│   │   ├── dto/              # EarthquakeResponseDTO
│   │   ├── exception/        # Custom exceptions + GlobalExceptionHandler
│   │   ├── model/            # Earthquake entity
│   │   ├── repository/       # EarthquakeRepository (Spring Data JPA)
│   │   └── service/          # EarthquakeService, GeoJsonParserService, EarthquakeMapper
│   ├── src/test/             # Unit + web layer tests
│   └── pom.xml
├── frontend/                 # React / Vite project
│   └── src/
│       ├── components/       # EarthquakeTable, FilterPanel, FetchButton
│       ├── services/         # earthquakeService.js (axios wrappers)
│       └── App.jsx
├── docker-compose.yml        # PostgreSQL container
└── README.md
```

---

## Prerequisites

- Java 17+
- Maven (or use the included `./mvnw` wrapper)
- Node.js 18+
- Docker (for the database)

---

## Database Setup

The project includes a `docker-compose.yml` that spins up a PostgreSQL 16 instance with the correct database and credentials pre-configured.

```bash
# From the repo root
docker compose up -d
```

This starts a container with:
- **Host:** `localhost:5432`
- **Database:** `earthquake_db`
- **Username:** `smart_user`
- **Password:** `smart_pass`

Hibernate's `ddl-auto=update` will create the `earthquakes` table automatically on first backend startup — no SQL scripts needed.

To stop the database:
```bash
docker compose down          # stop container, keep data
docker compose down -v       # stop container and wipe data
```

---

## Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on **`http://localhost:8080`**.

### Running Tests

Tests use an H2 in-memory database — no running PostgreSQL required.

```bash
cd backend
./mvnw test
```

Expected output: **14 tests, 0 failures**.

---

## Running the Frontend

```bash
cd frontend
npm install      # first time only
npm run dev
```

The UI starts on **`http://localhost:5173`**.

All `/api` requests are proxied to `localhost:8080` via Vite's dev server proxy, so no CORS issues during development.

---

## API Reference

Interactive docs available at **`http://localhost:8080/swagger-ui/index.html`** when the backend is running.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/earthquakes/fetch` | Fetch latest data from USGS and store it |
| `GET` | `/api/earthquakes` | Return all stored earthquakes |
| `GET` | `/api/earthquakes/filter/magnitude?min=3.0` | Filter by minimum magnitude |
| `GET` | `/api/earthquakes/filter/time?after={epochMs}` | Filter by time (epoch milliseconds) |
| `DELETE` | `/api/earthquakes/{id}` | Delete a specific record |

### Example responses

**`GET /api/earthquakes`**
```json
[
  {
    "id": 1,
    "magnitude": 4.8,
    "magType": "mb",
    "place": "Volcano Islands, Japan region",
    "title": "M 4.8 - Volcano Islands, Japan region",
    "time": 1713290361000
  }
]
```

**`GET /api/earthquakes/fetch`**
```json
{
  "message": "Data fetched",
  "count": 3
}
```

**Error response (e.g. 404)**
```json
{
  "timestamp": "2026-04-16T18:00:00Z",
  "status": 404,
  "error": "Earthquake not found with id: 99"
}
```

---

## Features

- **Live data** — fetches from the USGS GeoJSON feed (last hour)
- **Filtering** — by minimum magnitude or after a specific date/time
- **Atomic refresh** — each fetch replaces all records in a single transaction, preventing duplicates
- **Delete** — remove individual records via the UI or API
- **Error handling** — typed exceptions map to correct HTTP status codes (404, 500, 502)
- **Swagger UI** — full interactive API documentation
- **Color-coded magnitudes** — grey (<3.0), yellow (3.0–4.9), red (≥5.0)

---

## Configuration

All backend configuration is in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/earthquake_db
spring.datasource.username=smart_user
spring.datasource.password=smart_pass
spring.jpa.hibernate.ddl-auto=update
```

To use different database credentials, update these values and match them in `docker-compose.yml`.

---

## Assumptions & Notes

- The USGS feed only returns events from the **last hour** — data changes with every fetch
- Only earthquakes with **magnitude > 2.0** are stored (minor tremors are filtered out)
- The `time` field is stored as epoch milliseconds (Unix timestamp × 1000), as provided by USGS
- `ddl-auto=update` is used for simplicity — a production setup would use Flyway or Liquibase migrations
