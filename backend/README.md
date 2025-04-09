# Vert.x Kotlin Backend

This is a backend API built with Kotlin, Gradle, Vert.x, and MySQL.

## Requirements

- JDK 17 or higher
- Gradle (included via wrapper)
- MySQL 8.0 or higher

## Database Setup

1. Make sure you have MySQL running on `localhost:3306`
2. Create a database named `vertx_db`:
   ```sql
   CREATE DATABASE vertx_db;
   ```
3. The application will automatically create tables and insert sample data using Flyway migrations when you start it.

4. Default database configuration (you can change these in `DatabaseConfig.kt`):
   - URL: `jdbc:mysql://localhost:3306/vertx_db`
   - Username: `root`
   - Password: `password`

## Building and Running

To build the project:

```bash
./gradlew build
```

To run the application:

```bash
./gradlew run
```

The server will start on port 8080.

## API Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/data` - Get all items (basic info)
- `GET /api/items/{id}` - Get detailed item information by ID
- `POST /api/items` - Create a new item
- `PUT /api/items/{id}` - Update an existing item
- `DELETE /api/items/{id}` - Delete an item

## Running Tests

```bash
./gradlew test
``` 