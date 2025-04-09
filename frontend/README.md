# Angular Frontend for Vert.x Application

This is an Angular-based frontend that connects to a Kotlin Vert.x backend.

## Requirements

- Node.js 14.x or higher
- npm 6.x or higher

## Installation

```bash
npm install
```

## Development Server

```bash
npm start
```

This will start the development server with a proxy configuration to forward API requests to the backend.

## Build

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## Features

- Bootstrap 5 UI components
- Reactive data fetching from the backend
- Angular Router for navigation
- Environment-based configuration

## Structure

- `src/app/components` - UI components
- `src/app/services` - Services for API communication
- `src/app/models` - TypeScript interfaces
- `src/environments` - Environment configuration 