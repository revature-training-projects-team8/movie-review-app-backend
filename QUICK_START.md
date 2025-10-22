# Quick Start Guide

## Prerequisites

- MySQL installed and running
- Java 17 or higher
- Maven (or use included mvnw)

## Step-by-Step Setup

### 1. Setup MySQL Database

Open MySQL and run:

```sql
CREATE DATABASE IF NOT EXISTS movies;
USE movies;

-- Run the full schema from MYSQL_SETUP.md or:
SOURCE path/to/schema.sql;
```

### 2. Configure Database Connection

**Windows PowerShell:**

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

### 3. Install Dependencies

```powershell
./mvnw clean install
```

### 4. Run the Application

```powershell
./mvnw spring-boot:run
```

The server will start on: `http://localhost:8080`

### 5. Test the Backend

**Test Admin Login:**

```powershell
curl -X POST http://localhost:8080/api/users/login `
  -H "Content-Type: application/json" `
  -d '{\"username\":\"admin\",\"password\":\"admin123\"}'
```

**Test Registration:**

```powershell
curl -X POST http://localhost:8080/api/users/register `
  -H "Content-Type: application/json" `
  -d '{\"username\":\"testuser\",\"password\":\"test123\",\"email\":\"test@test.com\"}'
```

**Get All Movies:**

```powershell
curl http://localhost:8080/api/movies
```

### 6. Connect React Frontend

Ensure your React app runs on: `http://localhost:3001`

The backend is configured to accept CORS requests from this origin.

## Quick Reference

### Admin Credentials

- **Username:** admin
- **Password:** admin123
- **Email:** admin@moviereview.com
- **Role:** ADMIN

### API Base URL

```
http://localhost:8080/api
```

### Main Endpoints

- `POST /api/users/register` - Register
- `POST /api/users/login` - Login
- `GET /api/movies` - List movies
- `GET /api/movies/search?query=term` - Search
- `POST /api/reviews` - Submit review

### Environment Variables

```powershell
# Required for MySQL connection
$env:DB_URL="jdbc:mysql://localhost:3306/movies?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
```

### Build Commands

```powershell
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package as JAR
./mvnw package

# Run application
./mvnw spring-boot:run
```

## Troubleshooting

### Port Already in Use

```powershell
# Kill process on port 8080 (Windows)
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process
```

### MySQL Connection Failed

1. Check MySQL is running
2. Verify credentials
3. Ensure database 'movies' exists
4. Check firewall settings

### CORS Errors

- Verify React runs on `localhost:3001`
- Check browser console for specific errors
- Ensure SecurityConfig is properly loaded

## What's Included

✅ **Spring Security** - BCrypt password hashing  
✅ **CORS Support** - React frontend integration  
✅ **MySQL Database** - Production-ready persistence  
✅ **Role-Based Access** - USER and ADMIN roles  
✅ **Input Validation** - Bean validation on all inputs  
✅ **Exception Handling** - Proper error responses  
✅ **RESTful API** - Complete CRUD operations

## Need Help?

Check these files:

- `MYSQL_SETUP.md` - Detailed database setup
- `BACKEND_CONFIGURATION.md` - All configuration details
- `TEST_SUMMARY.md` - Testing documentation
- `USER_STORIES.md` - Feature requirements

Your backend is ready to rock! 🚀
