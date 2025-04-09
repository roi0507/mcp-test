# Full-Stack Application with Angular and Vert.x

This is a full-stack application with an Angular frontend and a Kotlin Vert.x backend.

## Project Structure

The project is divided into two main parts:

- `frontend/` - Angular application
- `backend/` - Kotlin Vert.x application

## Backend (Kotlin + Vert.x)

The backend is built with Kotlin and uses the Vert.x framework to provide a reactive API.

### Key Features

- Reactive HTTP server with Vert.x Web
- Kotlin coroutines integration
- Structured logging with SLF4J and Logback
- Unit tests with JUnit 5 and Vert.x test tools
- Gradle build system

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

### Running the Frontend

```bash
cd frontend
npm install
npm start
```


The development server will start on port 4200 and proxy API requests to the backend.

## API Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/data` - Sample data endpoint

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