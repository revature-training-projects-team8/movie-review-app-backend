# 🎬 Movie Review Application - Complete Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Database Design](#database-design)
5. [Backend API Documentation](#backend-api-documentation)
6. [Frontend Components](#frontend-components)
7. [Security Implementation](#security-implementation)
8. [Setup and Installation](#setup-and-installation)
9. [User Stories](#user-stories)
10. [Development Workflow](#development-workflow)
11. [Testing](#testing)
12. [Deployment](#deployment)

---

## Project Overview

**Movie Review App** is a full-stack web application that enables users to browse movies, view detailed information, and submit reviews. Built as a training exercise focusing on modern web development practices, the application demonstrates a complete end-to-end solution using Spring Boot and React.

### Key Features

- **Movie Catalog**: Browse and search through a collection of movies
- **User Authentication**: Secure registration and login with JWT tokens
- **Review System**: Rate and review movies with a 5-star rating system
- **Role-Based Access**: Different permissions for users and administrators
- **Real-time Updates**: Dynamic content updates without page refresh
- **Responsive Design**: Mobile-friendly interface using Tailwind CSS

### Project Context

| Detail             | Value                                             |
| ------------------ | ------------------------------------------------- |
| **Project Type**   | Group Training Project                            |
| **Duration**       | 3 Weeks                                           |
| **Team Size**      | Multiple developers                               |
| **Learning Focus** | Full-stack development with Spring Boot and React |

---

## Architecture

The application follows a **three-tier architecture** with clear separation of concerns:

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    JPA/SQL    ┌─────────────────┐
│   Frontend      │ ────────────── │   Backend       │ ──────────── │   Database      │
│   (React)       │    API Calls    │  (Spring Boot)  │   Queries     │   (MySQL)       │
│                 │                 │                 │               │                 │
│ • Components    │                 │ • Controllers   │               │ • users         │
│ • Context API   │                 │ • Services      │               │ • movies        │
│ • Router        │                 │ • Repositories  │               │ • reviews       │
│ • Axios         │                 │ • Security      │               │                 │
└─────────────────┘                 └─────────────────┘               └─────────────────┘
```

---

## Technology Stack

### Backend Technologies

- **Java 17** - Programming language
- **Spring Boot 3.5.6** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction layer
- **JWT (JSON Web Tokens)** - Stateless authentication
- **MySQL** - Production database
- **H2 Database** - Development and testing
- **Maven** - Build and dependency management
- **Lombok** - Code generation and boilerplate reduction
- **BCrypt** - Password hashing

### Frontend Technologies

- **React 19.1.1** - UI library
- **Vite 7.1.7** - Build tool and development server
- **React Router DOM 7.9.4** - Client-side routing
- **Axios 1.13.0** - HTTP client
- **Tailwind CSS 4.1.16** - Utility-first CSS framework
- **React Icons 5.5.0** - Icon library
- **React Toastify 11.0.5** - Notification system

### Development Tools

- **ESLint** - Code linting
- **Spring Boot DevTools** - Hot reloading for backend
- **Vite HMR** - Hot module replacement for frontend

---

## Database Design

### Entity Relationship Diagram

```
┌─────────────────┐    1:N     ┌─────────────────┐    N:1     ┌─────────────────┐
│      User       │ ────────── │     Review      │ ────────── │     Movie       │
├─────────────────┤            ├─────────────────┤            ├─────────────────┤
│ id (PK)         │            │ id (PK)         │            │ id (PK)         │
│ username        │            │ user_id (FK)    │            │ title           │
│ email           │            │ movie_id (FK)   │            │ description     │
│ password        │            │ rating          │            │ release_date    │
│ role            │            │ comment         │            │ director        │
│ created_at      │            │ review_date     │            │ genre           │
└─────────────────┘            └─────────────────┘            │ poster_url      │
                                                              │ duration        │
                                                              │ avg_rating      │
                                                              └─────────────────┘
```

### Table Specifications

#### Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt hashed
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Movies Table

```sql
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    director VARCHAR(255),
    genre VARCHAR(100),
    poster_url VARCHAR(500),
    duration INTEGER,
    avg_rating DECIMAL(3,2) DEFAULT 0.0
);
```

#### Reviews Table

```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_movie (user_id, movie_id)
);
```

---

## Backend API Documentation

### Base URL

```
Production: http://ec2-54-234-94-174.compute-1.amazonaws.com:8088
Development: http://localhost:8080
```

### Authentication Endpoints

#### Register User

```http
POST /auth/register
Content-Type: application/json

{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
}

Response: 201 Created
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
        "id": 1,
        "username": "johndoe",
        "email": "john@example.com",
        "role": "USER"
    }
}
```

#### Login

```http
POST /auth/login
Content-Type: application/json

{
    "username": "johndoe",
    "password": "password123"
}

Response: 200 OK
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
        "id": 1,
        "username": "johndoe",
        "email": "john@example.com",
        "role": "USER"
    }
}
```

### Movie Endpoints

#### Get All Movies

```http
GET /api/movies

Response: 200 OK
[
    {
        "id": 1,
        "title": "The Shawshank Redemption",
        "description": "Two imprisoned men bond over...",
        "releaseDate": "1994-09-23",
        "director": "Frank Darabont",
        "genre": "Drama",
        "posterUrl": "https://example.com/poster.jpg",
        "duration": 142,
        "averageRating": 4.5
    }
]
```

#### Get Movie by ID

```http
GET /api/movies/{id}

Response: 200 OK
{
    "id": 1,
    "title": "The Shawshank Redemption",
    "description": "Two imprisoned men bond over...",
    "releaseDate": "1994-09-23",
    "director": "Frank Darabont",
    "genre": "Drama",
    "posterUrl": "https://example.com/poster.jpg",
    "duration": 142,
    "averageRating": 4.5
}
```

#### Search Movies

```http
GET /api/movies/search?query=shawshank

Response: 200 OK
[...]  // Array of matching movies
```

#### Create Movie (Admin Only)

```http
POST /api/movies
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "title": "New Movie",
    "description": "Movie description...",
    "releaseDate": "2024-01-01",
    "director": "Director Name",
    "genre": "Action",
    "posterUrl": "https://example.com/poster.jpg",
    "duration": 120
}

Response: 201 Created
```

#### Update Movie (Admin Only)

```http
PUT /api/movies/{id}
Authorization: Bearer {jwt_token}
Content-Type: application/json

Response: 200 OK
```

#### Delete Movie (Admin Only)

```http
DELETE /api/movies/{id}
Authorization: Bearer {jwt_token}

Response: 204 No Content
```

### Review Endpoints

#### Get Reviews for Movie

```http
GET /api/reviews/movie/{movieId}

Response: 200 OK
[
    {
        "id": 1,
        "rating": 5,
        "comment": "Excellent movie!",
        "reviewDate": "2024-01-15T10:30:00",
        "user": {
            "id": 1,
            "username": "johndoe"
        },
        "movie": {
            "id": 1,
            "title": "The Shawshank Redemption"
        }
    }
]
```

#### Get All Reviews

```http
GET /api/reviews/all

Response: 200 OK
[...]  // Array of all reviews
```

#### Get User's Reviews

```http
GET /api/reviews/my-reviews
Authorization: Bearer {jwt_token}

Response: 200 OK
[...]  // Array of current user's reviews
```

#### Submit Review (Authenticated)

```http
POST /api/reviews
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "movieId": 1,
    "rating": 5,
    "comment": "Great movie!"
}

Response: 201 Created
```

#### Update Review (Owner Only)

```http
PUT /api/reviews/{reviewId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "rating": 4,
    "comment": "Updated review..."
}

Response: 200 OK
```

#### Delete Review (Owner/Admin)

```http
DELETE /api/reviews/{reviewId}
Authorization: Bearer {jwt_token}

Response: 204 No Content
```

---

## Frontend Components

### Component Architecture

```
src/
├── App.jsx                 # Main application component with routing
├── context.jsx            # User context and toast notification
├── main.jsx              # Application entry point
├── components/
│   ├── Home.jsx          # Homepage with movie grid and latest reviews
│   ├── Movie.jsx         # Movie detail page with reviews
│   ├── Login.jsx         # User authentication
│   ├── Register.jsx      # User registration
│   ├── Navbar.jsx        # Navigation header
│   ├── Footer.jsx        # Site footer
│   ├── Dashboard.jsx     # User dashboard
│   ├── ManageMovies.jsx  # Admin movie management
│   ├── AddMovie.jsx      # Add new movie form (Admin)
│   └── EditMovie.jsx     # Edit movie form (Admin)
└── assets/               # Static assets (images, etc.)
```

### Key Components

#### App.jsx

- **Purpose**: Main application container with routing logic
- **State Management**: Global movie/review state, user context
- **Responsibilities**:
  - Configure React Router routes
  - Manage global application state
  - Handle data fetching and updates
  - Provide user context to child components

#### Home.jsx

- **Purpose**: Landing page displaying movie catalog and latest reviews
- **Features**:
  - Movie search and genre filtering
  - Responsive movie grid layout
  - Latest reviews section with user avatars
  - Hero banner with call-to-action

#### Movie.jsx

- **Purpose**: Detailed movie information and review management
- **Features**:
  - Movie details display
  - Review submission form (authenticated users)
  - Review editing/deletion (review owners)
  - Star rating system
  - Real-time review updates

#### Login.jsx & Register.jsx

- **Purpose**: User authentication components
- **Features**:
  - Form validation
  - JWT token handling
  - Error message display
  - Redirect after successful authentication

#### Navbar.jsx

- **Purpose**: Navigation header with authentication status
- **Features**:
  - Conditional rendering based on auth status
  - Role-based menu items (Admin vs User)
  - Logout functionality
  - Responsive design

### State Management

#### UserContext

```jsx
const UserContext = React.createContext(null);

// Context structure:
{
  context: {
    currentUser: {
      id: 1,
      username: "johndoe",
      email: "john@example.com",
      role: "USER"
    },
    token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  updatedContext: function
}
```

#### Local State Management

- **Session Storage**: Persistent user authentication data
- **Component State**: Local UI state (loading, form data, etc.)
- **Props Drilling**: Parent-to-child data flow for movies and reviews

---

## Security Implementation

### Backend Security

#### JWT Authentication

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Validates JWT tokens on each request
    // Extracts user information from token
    // Sets authentication context
}
```

#### Password Security

- **BCrypt Hashing**: All passwords encrypted with BCrypt
- **Salt Rounds**: Automatic salt generation for password security
- **No Plain Text**: Passwords never stored in plain text

#### Authorization Rules

```java
@Configuration
public class SecurityConfig {
    // Public endpoints: /auth/**, /api/movies (GET), /api/reviews (GET)
    // Protected endpoints: Review CRUD operations
    // Admin endpoints: Movie management operations
}
```

#### CORS Configuration

```java
// Allowed origins for development and production
"http://localhost:3000",    // React dev server
"http://localhost:5173",    // Vite dev server
"http://trng2309-8.s3-website-us-east-1.amazonaws.com"  // Production
```

### Frontend Security

#### Token Management

```jsx
// Automatic token inclusion in requests
const token = sessionStorage.getItem("CONTEXT_APP")?.token;
axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
```

#### Route Protection

```jsx
// Conditional rendering based on authentication status
{
  context?.currentUser && <ProtectedComponent />;
}
```

#### Role-Based UI

```jsx
// Admin-only features
{
  context?.currentUser?.role === "ADMIN" && <AdminPanel />;
}
```

---

## Setup and Installation

### Prerequisites

- **Java 17 or higher**
- **Node.js 18 or higher**
- **MySQL 8.0 or higher**
- **Maven 3.6 or higher**
- **Git**

### Backend Setup

1. **Clone Repository**

```bash
git clone <repository-url>
cd movie-review-app-backend
```

2. **Database Configuration**

```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE movie_review_db;

# Copy environment template
cp .env.template .env

# Edit .env file with your database credentials
DB_URL=jdbc:mysql://localhost:3306/movie_review_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

3. **Build and Run**

```bash
# Install dependencies and compile
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Application starts on http://localhost:8080
```

### Frontend Setup

1. **Navigate to Frontend Directory**

```bash
cd movie-review-app-frontend
```

2. **Install Dependencies**

```bash
npm install
```

3. **Environment Configuration**

```bash
# Update BASE_URL in App.jsx if needed
const BASE_URL = 'http://localhost:8080';  // Development
# or
const BASE_URL = 'http://ec2-54-234-94-174.compute-1.amazonaws.com:8088';  // Production
```

4. **Run Development Server**

```bash
npm run dev

# Application starts on http://localhost:5173
```

### Database Initialization

The application automatically creates tables on startup. For initial data, you can:

1. **Create Admin User**

```sql
INSERT INTO users (username, email, password, role)
VALUES ('admin', 'admin@example.com', '$2a$10$hashedpassword', 'ADMIN');
```

2. **Add Sample Movies**

```sql
INSERT INTO movies (title, description, release_date, director, genre, poster_url, duration)
VALUES ('The Shawshank Redemption', 'Two imprisoned men bond...', '1994-09-23', 'Frank Darabont', 'Drama', 'poster_url', 142);
```

---

## User Stories

### Public User Stories (No Authentication Required)

| **Epic**          | **User Story**                                                                                         | **Acceptance Criteria**                                                                                                                   |
| ----------------- | ------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Browse Movies** | As a movie enthusiast, I want to view a list of all available movies, so that I can discover new films | • Display movie grid with posters<br>• Show title, genre, and average rating<br>• Implement search functionality<br>• Add genre filtering |
| **View Details**  | As a movie enthusiast, I want to see detailed movie information, so that I can learn more about it     | • Display movie details (title, description, director, etc.)<br>• Show average rating and reviews<br>• Include movie poster and metadata  |
| **Read Reviews**  | As a movie enthusiast, I want to read existing reviews, so that I can understand others' opinions      | • Display all reviews for a movie<br>• Show reviewer name and rating<br>• Sort reviews by date (newest first)                             |

### Authenticated User Stories (Login Required)

| **Epic**           | **User Story**                                                                          | **Acceptance Criteria**                                                                                                                                      |
| ------------------ | --------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Authentication** | As a user, I want to register and log in, so that I can access personalized features    | • Secure registration form with validation<br>• JWT-based authentication<br>• Session persistence<br>• Proper error handling                                 |
| **Submit Reviews** | As a logged-in user, I want to submit reviews, so that I can share my opinions          | • Review form with rating (1-5 stars) and comment<br>• Prevent duplicate reviews per user/movie<br>• Update movie's average rating<br>• Real-time UI updates |
| **Manage Reviews** | As a logged-in user, I want to edit/delete my reviews, so that I can update my opinions | • Edit review functionality for review owners<br>• Delete review with confirmation<br>• Update average rating after changes<br>• Proper authorization checks |

### Administrator Stories (Admin Role Required)

| **Epic**               | **User Story**                                                                   | **Acceptance Criteria**                                                                                                                           |
| ---------------------- | -------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Movie Management**   | As an administrator, I want to manage movies, so that I can maintain the catalog | • Add new movies with all metadata<br>• Edit existing movie information<br>• Delete movies with cascade deletion<br>• Upload/manage movie posters |
| **Content Moderation** | As an administrator, I want to moderate content, so that I can maintain quality  | • View all reviews<br>• Delete inappropriate reviews<br>• User management capabilities<br>• Content reporting system                              |

---

## Development Workflow

### 3-Week Development Timeline

#### Week 1: Backend Foundation & Database

- **Days 1-2**: Project setup and database design
  - Initialize Spring Boot project
  - Design and create database schema
  - Set up development environment
- **Days 3-4**: Core API development
  - Implement Movie CRUD operations
  - Create User registration/login
  - Basic security configuration
- **Days 5-7**: Review system and testing
  - Implement Review CRUD operations
  - API testing with Postman
  - Unit test development

#### Week 2: Frontend & Integration

- **Days 1-2**: React project setup
  - Initialize React/Vite project
  - Set up routing and basic components
  - Implement design system with Tailwind
- **Days 3-4**: Core UI development
  - Build movie listing and detail pages
  - Implement authentication forms
  - Create responsive layouts
- **Days 5-7**: API integration
  - Connect frontend to backend APIs
  - Implement JWT token handling
  - Error handling and loading states

#### Week 3: Advanced Features & Deployment

- **Days 1-2**: Review functionality
  - Implement review submission
  - Add edit/delete capabilities
  - Real-time rating updates
- **Days 3-4**: Admin features
  - Movie management interface
  - Content moderation tools
  - Role-based access control
- **Days 5-7**: Testing & deployment
  - Integration testing
  - Production deployment
  - Documentation completion

### Git Workflow

#### Branch Strategy

```
main (production)
├── develop (integration)
│   ├── feature/movie-crud
│   ├── feature/user-auth
│   ├── feature/review-system
│   └── feature/admin-panel
└── hotfix/security-patch
```

#### Commit Convention

```
feat: add movie search functionality
fix: resolve JWT token expiration issue
docs: update API documentation
test: add unit tests for review service
refactor: optimize database queries
```

#### Pull Request Process

1. **Feature Development**: Create feature branch from `develop`
2. **Code Review**: Mandatory PR review before merge
3. **Integration Testing**: Verify functionality in `develop` branch
4. **Production Deployment**: Merge `develop` to `main`

---

## Testing

### Backend Testing

#### Unit Tests

```java
@SpringBootTest
@Profile("test")
class MovieServiceTest {

    @Test
    void shouldCreateMovie() {
        // Test movie creation logic
    }

    @Test
    void shouldCalculateAverageRating() {
        // Test rating calculation
    }
}
```

#### Integration Tests

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MovieControllerIntegrationTest {

    @Test
    void shouldReturnMovieList() {
        // Test complete API endpoint
    }
}
```

#### Test Configuration

```java
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {
    // Disable security for testing
}
```

### Frontend Testing

#### Component Tests

```jsx
// Example with React Testing Library
import { render, screen } from "@testing-library/react";
import Home from "./Home";

test("renders movie grid", () => {
  render(<Home movies={mockMovies} />);
  expect(screen.getByText("All Movies")).toBeInTheDocument();
});
```

#### API Integration Tests

```jsx
// Mock API responses for testing
const mockMovies = [{ id: 1, title: "Test Movie", rating: 4.5 }];
```

### Testing Tools

- **Backend**: JUnit 5, Spring Boot Test, Testcontainers
- **Frontend**: Jest, React Testing Library, MSW (Mock Service Worker)
- **API Testing**: Postman, Newman CLI
- **E2E Testing**: Cypress (optional)

---

## Deployment

### Production Environment

#### Backend Deployment (AWS EC2)

```bash
# Current production URL
http://ec2-54-234-94-174.compute-1.amazonaws.com:8088

# Deployment steps:
1. Build production JAR
./mvnw clean package -DskipTests

2. Upload to EC2 instance
scp target/movie-review-app-0.0.1-SNAPSHOT.jar ec2-user@instance:/app/

3. Run with production profile
java -jar -Dspring.profiles.active=prod movie-review-app-0.0.1-SNAPSHOT.jar
```

#### Frontend Deployment (AWS S3)

```bash
# Current production URL
http://trng2309-8.s3-website-us-east-1.amazonaws.com

# Deployment steps:
1. Build production bundle
npm run build

2. Upload to S3 bucket
aws s3 sync dist/ s3://trng2309-8/ --delete

3. Configure S3 for static hosting
```

### Environment Configuration

#### Production Backend (application-prod.properties)

```properties
# Database Configuration
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# CORS
cors.allowed-origins=http://trng2309-8.s3-website-us-east-1.amazonaws.com
```

#### Production Frontend

```jsx
// Update BASE_URL for production
const BASE_URL = "http://ec2-54-234-94-174.compute-1.amazonaws.com:8088";
```

### Monitoring and Health Checks

#### Backend Health Endpoint

```http
GET /actuator/health

Response:
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

#### Frontend Monitoring

- **Error Tracking**: React Error Boundaries
- **Performance**: Lighthouse audits
- **Analytics**: User interaction tracking

### Backup and Recovery

#### Database Backup

```bash
# Create backup
mysqldump -u username -p movie_review_db > backup.sql

# Restore backup
mysql -u username -p movie_review_db < backup.sql
```

#### Application Recovery

- **Blue-Green Deployment**: Zero-downtime updates
- **Rollback Strategy**: Quick revert to previous version
- **Data Migration**: Versioned database schema changes

---

## Conclusion

The Movie Review Application demonstrates a complete full-stack solution using modern web technologies. The application successfully implements:

- **Scalable Architecture**: Clean separation of concerns with REST API design
- **Security Best Practices**: JWT authentication, password hashing, and CORS configuration
- **User Experience**: Responsive design, real-time updates, and intuitive navigation
- **Code Quality**: Comprehensive testing, documentation, and maintainable code structure

This project serves as an excellent foundation for understanding enterprise-level web application development and can be extended with additional features like:

- **Advanced Search**: Elasticsearch integration
- **File Upload**: Image management for movie posters
- **Real-time Features**: WebSocket notifications
- **Analytics**: Usage tracking and reporting
- **Mobile App**: React Native companion app

The codebase is well-documented, tested, and production-ready, making it an ideal reference for similar projects or as a starting point for more complex applications.
