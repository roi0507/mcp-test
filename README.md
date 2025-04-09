# Full-Stack Application with Angular and Vert.x

This is a full-stack application with an Angular frontend and a Kotlin Vert.x backend with MySQL database.

## Project Structure

The project is divided into two main parts:

- `frontend/` - Angular application
- `backend/` - Kotlin Vert.x application with MySQL integration

## Backend (Kotlin + Vert.x + MySQL)

The backend is built with Kotlin and uses the Vert.x framework to provide a reactive API with MySQL database.

### Key Features

- Reactive HTTP server with Vert.x Web
- MySQL database integration using JDBC
- Automatic database migration with Flyway
- Full CRUD operations for items
- Kotlin coroutines integration
- Structured logging with SLF4J and Logback
- Unit tests with JUnit 5 and Vert.x test tools
- Gradle build system

### Database Setup

Before running the application, you need to:

1. Install MySQL 8.0 or higher
2. Create a database named `vertx_db`
3. The default connection settings are:
   - URL: `jdbc:mysql://localhost:3306/vertx_db`
   - Username: `root`
   - Password: `password`

You can adjust these settings in the `DatabaseConfig.kt` file.

### Running the Backend

```bash
cd backend
./gradlew run
```

The server will start on port 8080.

## Frontend (Angular)

The frontend is built with Angular and uses Bootstrap for UI components.

### Key Features

- Reactive data handling with RxJS
- Bootstrap 5 for responsive UI
- Angular Router for navigation
- Environment-based configuration
- Services for API communication
- Modal dialogs for item details

### Running the Frontend

```bash
cd frontend
npm install
npm start
```

The development server will start on port 4200 and proxy API requests to the backend.

## API Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/data` - Get all items (basic info)
- `GET /api/items/{id}` - Get detailed item information by ID
- `POST /api/items` - Create a new item
- `PUT /api/items/{id}` - Update an existing item
- `DELETE /api/items/{id}` - Delete an item

## Development

For local development, start both the backend and frontend in separate terminals:

1. Start the backend:
   ```bash
   cd backend
   ./gradlew run
   ```

2. Start the frontend:
   ```bash
   cd frontend
   npm start
   ```

3. Access the application at http://localhost:4200 