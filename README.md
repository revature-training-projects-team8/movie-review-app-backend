# Movie Review Backend

A simplified Spring Boot backend for managing movies and reviews.

## Features

- **Movies Management**: CRUD operations for movies
- **Reviews Management**: Users can create, read, update, and delete reviews
- **User Management**: Simple user registration and authentication
- **Validation**: Input validation with custom error messages
- **Exception Handling**: Global exception handling with custom exceptions
- **CORS Support**: Frontend integration ready

## Tech Stack

- Spring Boot 3.5.6
- Spring Data JPA
- MySQL Database
- Maven
- Java 17

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd movie-review-app-backend
```

### 2. Database Setup

1. Install MySQL and create a database named `movie_review_db`
2. Copy `.env.template` to `.env` and update with your database credentials:

```properties
DB_URL=jdbc:mysql://localhost:3306/movie_review_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### 3. Environment Variables

Set the following environment variables or update `application.properties`:

- `DB_URL`: Your MySQL database URL
- `DB_USERNAME`: Your MySQL username
- `DB_PASSWORD`: Your MySQL password

### 4. Build and Run

```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Movies

- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies/search?query={term}` - Search movies
- `POST /api/movies` - Create new movie
- `PUT /api/movies/{id}` - Update movie
- `DELETE /api/movies/{id}` - Delete movie

### Reviews

- `GET /api/reviews/movie/{movieId}` - Get reviews for a movie
- `GET /api/reviews/user/{userId}` - Get reviews by user
- `POST /api/reviews/movie/{movieId}/user/{userId}` - Submit review
- `PUT /api/reviews/{reviewId}/user/{userId}` - Update review
- `DELETE /api/reviews/{reviewId}/user/{userId}` - Delete review

### Users

- `POST /api/users/register` - Register new user
- `POST /api/users/login` - Simple login
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username

## Project Structure

```
src/
├── main/
│   ├── java/com/moviereview/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom exceptions and handlers
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # JPA repositories
│   │   └── service/        # Business logic
│   └── resources/
│       └── application.properties
└── test/
```

## Features Implemented

✅ Removed Spring Security for simplicity  
✅ Fixed package structure and naming  
✅ Added validation annotations  
✅ Created custom exception handling  
✅ Simplified authentication (basic user operations)  
✅ Added CORS configuration for frontend  
✅ Secured database credentials with environment variables  
✅ Clean project structure

## Notes

- This is a simplified version without JWT authentication for ease of development
- Database credentials are externalized for security
- CORS is configured for local frontend development
- All endpoints include proper validation and error handling
- The application uses environment variables for database connection

## Development

To run in development mode with hot reload:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
```

For production deployment, make sure to:

1. Use proper authentication/authorization
2. Use HTTPS
3. Configure production database settings
4. Set appropriate CORS origins
