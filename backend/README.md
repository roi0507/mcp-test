# Vert.x Kotlin Backend

This is a backend API built with Kotlin, Gradle, and Vert.x.

## Requirements

- JDK 17 or higher
- Gradle (included via wrapper)

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
- `GET /api/data` - Sample data endpoint

## Running Tests

```bash
./gradlew test
``` 