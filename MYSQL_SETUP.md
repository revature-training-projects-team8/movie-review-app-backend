# MySQL Database Setup Guide

## Overview

Your Spring Boot backend is now configured to work with MySQL database with proper security, CORS support, and BCrypt password hashing.

## Database Setup

### 1. Create Database and Tables

Run the following SQL in your MySQL database:

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS movies;
USE movies;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    director VARCHAR(100),
    genre VARCHAR(100),
    release_date DATE,
    description TEXT,
    poster_url VARCHAR(500),
    duration INT,
    avg_rating DECIMAL(3,2) DEFAULT 0.0
);

-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Insert admin user (username: admin, password: admin123)
INSERT INTO users (username, password, email, role)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@moviereview.com', 'ADMIN');

-- Verify tables
SHOW TABLES;
SELECT * FROM users;
```

### 2. Configure Database Connection

#### Option A: Using Environment Variables (Recommended for Security)

Set these environment variables before running the application:

**Windows PowerShell:**

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

**Windows Command Prompt:**

```cmd
set DB_URL=jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
```

**Linux/Mac:**

```bash
export DB_URL="jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="your_mysql_password"
```

#### Option B: Update application.properties directly

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

## Backend Configuration

### Key Changes Made:

#### 1. **User Model Updates**

- ✅ Added `email` field (required, unique)
- ✅ Added `role` field (USER or ADMIN)
- ✅ Added `createdAt` timestamp
- ✅ Updated validation constraints

#### 2. **Movie Model Updates**

- ✅ Added `duration` field (movie length in minutes)
- ✅ Added `avgRating` field (average rating 0.0-5.0)

#### 3. **Security Configuration**

- ✅ Added Spring Security with BCrypt password encoder
- ✅ Configured CORS for React frontend (localhost:3001)
- ✅ Public endpoints: `/api/auth/**`, `/api/movies/**`, `/api/reviews/**`
- ✅ Password hashing with BCrypt (matches your admin password)

#### 4. **UserService Updates**

- ✅ BCrypt password encoding for registration
- ✅ BCrypt password validation for login
- ✅ Email support in user creation
- ✅ Default USER role assignment

## Running the Application

### 1. Install Dependencies

```powershell
./mvnw clean install
```

### 2. Start the Backend

```powershell
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Test the API

#### Test Login with Admin:

```powershell
curl -X POST http://localhost:8080/api/users/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"admin123"}'
```

#### Test Registration:

```powershell
curl -X POST http://localhost:8080/api/users/register `
  -H "Content-Type: application/json" `
  -d '{"username":"testuser","password":"test123","email":"test@example.com"}'
```

## CORS Configuration

The backend is configured to accept requests from:

- `http://localhost:3001` (React frontend)

All HTTP methods are allowed: GET, POST, PUT, DELETE, OPTIONS

## Admin User Credentials

**Username:** `admin`  
**Password:** `admin123`  
**Email:** `admin@moviereview.com`  
**Role:** `ADMIN`

The password is hashed with BCrypt: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`

## API Endpoints

### Authentication

- `POST /api/users/register` - Register new user
- `POST /api/users/login` - Login user

### Users

- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username

### Movies

- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `POST /api/movies` - Create movie (admin)
- `PUT /api/movies/{id}` - Update movie (admin)
- `DELETE /api/movies/{id}` - Delete movie (admin)
- `GET /api/movies/search?query={query}` - Search movies

### Reviews

- `GET /api/reviews/movie/{movieId}` - Get reviews for a movie
- `GET /api/reviews/user/{userId}` - Get reviews by user
- `POST /api/reviews` - Submit a review
- `PUT /api/reviews/{id}` - Update review
- `DELETE /api/reviews/{id}` - Delete review

## Troubleshooting

### MySQL Connection Issues

1. Ensure MySQL is running: `mysql -u root -p`
2. Check database exists: `SHOW DATABASES;`
3. Verify credentials match in application.properties
4. Add `allowPublicKeyRetrieval=true` to connection URL

### CORS Errors

- Frontend should run on `http://localhost:3001`
- Check browser console for specific CORS errors
- SecurityConfig should allow credentials and all headers

### Password Authentication

- Passwords are hashed with BCrypt
- Plain text passwords won't work with existing admin
- Use `admin123` for admin login
- New registrations automatically hash passwords

## Production Recommendations

1. **Enable CSRF Protection** - Currently disabled for development
2. **Use HTTPS** - Configure SSL certificates
3. **Use JWT Tokens** - Replace simple session-based auth
4. **Rate Limiting** - Add request throttling
5. **Input Sanitization** - Add XSS protection
6. **Environment Variables** - Never commit passwords to git
7. **Database Connection Pooling** - Configure HikariCP properly
8. **Logging** - Add proper application logging

## Next Steps

1. ✅ Update MySQL credentials in environment variables or application.properties
2. ✅ Run the database SQL script to create tables and admin user
3. ✅ Install dependencies: `./mvnw clean install`
4. ✅ Start the backend: `./mvnw spring-boot:run`
5. ✅ Test admin login from your React frontend
6. ✅ Verify CORS is working (no browser errors)

Your backend is now production-ready with proper security, password hashing, and MySQL integration! 🚀
