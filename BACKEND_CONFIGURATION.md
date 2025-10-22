# Backend Configuration Summary

## What Was Changed

### 1. Database Migration: H2 → MySQL

- **Before:** In-memory H2 database (development only)
- **After:** MySQL production database
- **Configuration:** `application.properties` updated with MySQL settings
- **Schema:** Aligned with your MySQL database structure

### 2. User Model Enhancements

Added new fields to match your database schema:

- ✅ `email` (VARCHAR(100), UNIQUE, NOT NULL)
- ✅ `role` (VARCHAR(20), DEFAULT 'USER')
- ✅ `createdAt` (TIMESTAMP, auto-generated)

### 3. Movie Model Enhancements

Added new fields for extended movie information:

- ✅ `duration` (INTEGER - movie length in minutes)
- ✅ `avgRating` (DECIMAL(3,2) - average rating 0.00-5.00)

### 4. Review Model

Already had proper timestamp:

- ✅ `reviewDate` (TIMESTAMP, auto-generated with @PrePersist)

### 5. Spring Security Integration

Added comprehensive security configuration:

- ✅ **SecurityConfig.java** - Main security configuration
- ✅ **BCrypt Password Encoder** - Secure password hashing
- ✅ **CORS Configuration** - Integrated with Spring Security
- ✅ **Public Endpoints** - Allow unauthenticated access to browsing
- ✅ **Password Validation** - BCrypt matching for login

### 6. Dependencies Added

Updated `pom.xml` with:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 7. Service Layer Updates

**UserService.java:**

- ✅ Password encoding with BCrypt during registration
- ✅ Password validation with BCrypt during login
- ✅ Email support in user creation
- ✅ Automatic USER role assignment

**UserController.java:**

- ✅ Updated registration to include email parameter
- ✅ Login uses BCrypt password validation

## Security Configuration Details

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // BCrypt password encoder for secure password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS configuration for React frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Allows http://localhost:3001
        // Allows all HTTP methods and headers
        // Supports credentials (cookies, auth headers)
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Public: /api/auth/**, /api/movies/**, /api/reviews/**
        // CSRF disabled for REST API
        // CORS enabled
    }
}
```

## CORS Configuration

### Allowed Origins

- `http://localhost:3001` (React frontend)

### Allowed Methods

- GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD

### Allowed Headers

- All headers (`*`)

### Credentials

- Enabled (allows cookies and authorization headers)

### Exposed Headers

- Authorization
- Content-Type
- Access-Control-Allow-Origin
- Access-Control-Allow-Credentials

## Database Schema Alignment

### Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Movies Table

```sql
CREATE TABLE movies (
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
```

### Reviews Table

```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);
```

## Admin User

Your manually created admin user will work with the backend:

**Database Entry:**

- Username: `admin`
- Password: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`
- Plain Password: `admin123`
- Email: `admin@moviereview.com`
- Role: `ADMIN`

**Login Request:**

```json
POST /api/users/login
{
  "username": "admin",
  "password": "admin123"
}
```

The backend will:

1. Find user by username
2. Use BCrypt to compare `admin123` with stored hash
3. Return user object if password matches

## API Endpoints

### Public Endpoints (No Authentication Required)

- `POST /api/users/register` - Register new user
- `POST /api/users/login` - Login user
- `GET /api/movies` - Browse all movies
- `GET /api/movies/{id}` - View movie details
- `GET /api/movies/search?query=` - Search movies
- `GET /api/reviews/movie/{movieId}` - View movie reviews

### Protected Endpoints (Require Authentication in Future)

Currently all public, but designed to be secured:

- `POST /api/movies` - Create movie (should require ADMIN role)
- `PUT /api/movies/{id}` - Update movie (should require ADMIN role)
- `DELETE /api/movies/{id}` - Delete movie (should require ADMIN role)
- `POST /api/reviews` - Submit review (should require authentication)
- `PUT /api/reviews/{id}` - Update review (should require ownership)
- `DELETE /api/reviews/{id}` - Delete review (should require ownership)

## Configuration Files

### application.properties

```properties
# MySQL Database
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/movies...}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server
server.port=8080
```

### Environment Variables (Recommended)

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
```

## Testing the Setup

### 1. Start MySQL

```powershell
# Verify MySQL is running
mysql -u root -p
```

### 2. Create Database

```sql
CREATE DATABASE IF NOT EXISTS movies;
USE movies;
-- Run the full schema from MYSQL_SETUP.md
```

### 3. Set Environment Variables

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

### 4. Start Backend

```powershell
./mvnw spring-boot:run
```

### 5. Test Admin Login

```powershell
curl -X POST http://localhost:8080/api/users/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"admin123"}'
```

Expected Response:

```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@moviereview.com",
  "role": "ADMIN",
  "createdAt": "2025-10-13T..."
}
```

### 6. Test Registration

```powershell
curl -X POST http://localhost:8080/api/users/register `
  -H "Content-Type: application/json" `
  -d '{"username":"newuser","password":"password123","email":"user@example.com"}'
```

### 7. Test CORS from Frontend

Start your React app on `http://localhost:3001` and test:

```javascript
fetch("http://localhost:8080/api/movies")
  .then((res) => res.json())
  .then((data) => console.log(data));
```

Should work without CORS errors!

## File Structure

```
src/
├── main/
│   ├── java/com/moviereview/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java      ← NEW: Spring Security + CORS
│   │   │   └── CorsConfig.java          ← UPDATED: Disabled (using SecurityConfig)
│   │   ├── model/
│   │   │   ├── User.java                ← UPDATED: +email, +role, +createdAt
│   │   │   ├── Movie.java               ← UPDATED: +duration, +avgRating
│   │   │   └── Review.java              ← Already has reviewDate
│   │   ├── service/
│   │   │   └── UserService.java         ← UPDATED: BCrypt password handling
│   │   ├── controller/
│   │   │   └── UserController.java      ← UPDATED: Email in registration
│   │   └── ...
│   └── resources/
│       └── application.properties       ← UPDATED: MySQL configuration
└── ...
```

## Migration Checklist

- [x] User model updated with email, role, createdAt
- [x] Movie model updated with duration, avgRating
- [x] Spring Security added with BCrypt
- [x] CORS configured for React frontend
- [x] MySQL database configuration
- [x] UserService uses BCrypt for passwords
- [x] SecurityConfig properly configured
- [x] Dependencies added to pom.xml
- [x] Application compiles successfully

## Next Steps

1. **Configure MySQL Credentials**
   - Set environment variables or update application.properties
2. **Run Database Scripts**
   - Execute the SQL from MYSQL_SETUP.md
3. **Start Backend**
   - `./mvnw spring-boot:run`
4. **Test Authentication**
   - Login with admin/admin123
   - Register a new user
5. **Connect Frontend**

   - Ensure React runs on localhost:3001
   - Test API calls with proper CORS

6. **Update Tests**
   - Tests currently use H2 in-memory database
   - Consider updating test configuration for MySQL testing

## Troubleshooting

### Issue: "Access denied for user"

**Solution:** Check MySQL username/password in environment variables

### Issue: "Unknown database 'movies'"

**Solution:** Run `CREATE DATABASE movies;` in MySQL

### Issue: CORS errors

**Solution:** Verify React runs on localhost:3001, check SecurityConfig

### Issue: "Bad credentials" on login

**Solution:** Password must be BCrypt hashed in database, use admin123 for admin

### Issue: Tables not created

**Solution:** Set `spring.jpa.hibernate.ddl-auto=update` or run SQL manually

Your backend is now fully configured with MySQL, Spring Security, BCrypt password hashing, and proper CORS support! 🎉
