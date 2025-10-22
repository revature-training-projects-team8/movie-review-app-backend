# 🎬 Movie Review App: System Design Documentation

**Project Title:** Movie Review App

A full-stack web application allowing users to browse movies, view details, and submit reviews. This project serves as a training exercise focusing on modern web development practices.

| Detail                      | Value                                                                                                                 |
| :-------------------------- | :-------------------------------------------------------------------------------------------------------------------- |
| **Project Type**            | Group Project                                                                                                         |
| **Training Duration**       | 3 Weeks                                                                                                               |
| **Core Tools/Technologies** | Java, SQL, **GIT**, **HTML**, **Typescript**, **React**, **Spring**, **Spring Boot**, **Spring MVC**, **Spring Data** |
| **Outcome**                 | Associates should be able to develop a full-stack application using Spring REST and React.                            |

---

## ⚙️ 2. Detailed Component Design

The application follows a standard **three-tier architecture** (Client, Application, and Data).

### 2.1. Client-Tier (Frontend)

| Attribute            | Description                                                                                        |
| :------------------- | :------------------------------------------------------------------------------------------------- |
| **Technology Stack** | **React** (TypeScript, HTML, CSS/SCSS, JSX/TSX)                                                    |
| **Purpose**          | Provides the **User Interface (UI)** for browsing movies, viewing details, and submitting reviews. |
| **Routing**          | **React Router DOM** for client-side navigation.                                                   |
| **Styling**          | CSS Modules, Styled Components, or a CSS framework (e.g., Bootstrap, Tailwind CSS).                |

#### **Key Components**

- **User Interface (UI) Components:** Reusable components like `Header.tsx`, `MovieCard.tsx`, `ReviewForm.tsx`, and buttons.
- **Pages/Views:** Top-level components for application views:
  - `HomePage.tsx`: Displays a list of popular or recently added movies.
  - `MovieDetailPage.tsx`: Shows detailed information and a list of existing reviews.
  - `LoginPage.tsx` / `RegisterPage.tsx`: User authentication.
  - `ProfilePage.tsx` (Optional): User's submitted reviews.
- **State Management:**
  - **React Hooks** (`useState`, `useEffect`, `useContext`) for local state.
  - Potentially a global state management library (e.g., **Redux Toolkit**, Zustand, or **React Context API**) for application-wide data.
- **API Service:** A dedicated module (`apiService.ts`) to handle all **HTTP requests** to the backend API using `fetch` or Axios.

---

### 2.2. Application-Tier (Backend)

| Attribute            | Description                                                                                  |
| :------------------- | :------------------------------------------------------------------------------------------- |
| **Technology Stack** | **Java**, **Spring Boot**, **Spring MVC**, **Spring Data JPA**                               |
| **Purpose**          | Handles **business logic**, data persistence, and exposes **RESTful APIs** for the frontend. |

#### **Key Components**

- **REST Controllers (`@RestController`):** Define API endpoints for resources (movies, reviews, users). Examples:
  - `MovieController.java` - `/api/movies/**` endpoints
  - `MoviesController.java` - `/movies/**` endpoints (frontend compatibility)
  - `ReviewController.java` - `/api/reviews/**` endpoints
  - `ReviewsController.java` - `/reviews/**` endpoints (frontend compatibility)
  - `AuthController.java` - `/auth/**` endpoints for authentication
  - `UserController.java` - `/api/users/**` endpoints
- **Service Layer (`@Service`):** Contains core business logic and orchestrates operations involving multiple repositories. Examples: `MovieService.java`, `ReviewService.java`, `UserService.java`.
- **Repository Layer (`@Repository`):** Interfaces extending `JpaRepository` to abstract and manage database interactions (CRUD operations). Examples: `MovieRepository.java`, `ReviewRepository.java`, `UserRepository.java`.
- **Models/Entities:** POJOs annotated with JPA (`@Entity`, `@Table`, `@Id`) to represent database tables:
  - `Movie.java` - Includes duration field (in minutes)
  - `Review.java` - Links to Movie and User entities
  - `User.java` - BCrypt hashed passwords with role field
- **DTOs (Data Transfer Objects):** Objects used for transferring data between client and server:
  - `MovieDTO.java` - Includes duration and averageRating fields
  - `ReviewDTO.java` - Includes movieTitle and username for display
  - `LoginRequest.java` - Authentication credentials
  - `LoginResponse.java` - User info returned after login
  - `UserRegistrationRequest.java` - New user registration data
- **Security (Spring Security):**
  - `SecurityConfig.java` - BCrypt password encoding, CORS configuration
  - CORS supports both `localhost:3000` and `localhost:3001`
  - Public endpoints: `/auth/**`, `/api/auth/**`, `/movies/**`, `/api/movies/**`, `/reviews/**`, `/api/reviews/**`
  - Protected endpoints require authentication
- **Configuration:**
  - `application.properties` - Database connection (MySQL 8.0.43), port 8080
  - Environment variables: `DB_USERNAME`, `DB_PASSWORD` for secure credentials
  - Profile-based configuration (`@Profile("!test")` excludes security in tests)

---

### 2.3. Data-Tier

| Attribute            | Description                                               |
| :------------------- | :-------------------------------------------------------- |
| **Technology Stack** | **SQL** (e.g., MySQL for production, H2 for development)  |
| **Purpose**          | Stores all application data in a **relational database**. |

#### **Schema Design**

| Table       | Key Fields                                                                                                                   | Relationships                                      | Notes                                                                    |
| :---------- | :--------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------- | :----------------------------------------------------------------------- |
| **users**   | `id` (PK), `username` (UNIQUE), `email` (UNIQUE), `password` (HASHED), `role`                                                |                                                    | `role` (e.g., 'USER', 'ADMIN'). Passwords hashed with BCrypt strength 10 |
| **movies**  | `id` (PK), `title`, `description`, `release_date`, `genre`, `director`, `poster_url`, `duration`, `avg_rating` (DECIMAL 3,2) |                                                    | `duration` in minutes, `avg_rating` calculated from reviews              |
| **reviews** | `id` (PK), `rating`, `comment`, `review_date`                                                                                | `movie_id` (FK to movies), `user_id` (FK to users) | Records user-submitted ratings and comments.                             |

#### **Data Management**

- **Database:** MySQL 8.0.43 (production), H2 in-memory (testing)
- **Connection:** Configured via environment variables (`DB_USERNAME`, `DB_PASSWORD`)
- **Hibernate:** Auto-DDL update mode, MySQL dialect
- **Migrations:** Schema managed through JPA entity definitions
- **Security:**
  - Passwords stored with BCrypt hashing (strength 10)
  - Admin default password: BCrypt hash `$2a$10$MbVZUK2kd8HYdAHmuidzLeSfbV97oxp9oo3T04O8dP.zs1Ay6Cw4O`
  - Script provided: `update_admin_password.sql`

---

## 🌐 3. API Endpoints (RESTful Example)

**Base URLs:**

- `/api/v1` or `/api` (standard RESTful endpoints)
- Root endpoints (`/movies`, `/reviews`, `/auth`) for frontend compatibility

### **Authentication**

| Method | Endpoint            | Description                                | Access                                   |
| :----- | :------------------ | :----------------------------------------- | :--------------------------------------- |
| `POST` | `/auth/register`    | Register a new user.                       | Public                                   |
| `POST` | `/auth/login`       | Authenticate user and return user details. | Public                                   |
| `POST` | `/auth/test/hash`   | Generate BCrypt hash (testing only).       | Public (should be removed in production) |
| `POST` | `/auth/test/verify` | Verify BCrypt password (testing only).     | Public (should be removed in production) |

### **Movies**

| Method   | Endpoint                                               | Description                                    | Access     | Notes                                            |
| :------- | :----------------------------------------------------- | :--------------------------------------------- | :--------- | :----------------------------------------------- |
| `GET`    | `/movies`<br>`/api/movies`                             | Get a list of all movies with average ratings. | Public     | Dual endpoint support                            |
| `GET`    | `/movies/{id}`<br>`/api/movies/{id}`                   | Get detailed information for a movie by ID.    | Public     | Includes average rating                          |
| `GET`    | `/movies/search?query=keyword`<br>`/api/movies/search` | Search movies by title, genre, or director.    | Public     | Case-insensitive search                          |
| `POST`   | `/movies`<br>`/api/movies`                             | Add a new movie.                               | Admin Only | Requires all fields including duration           |
| `PUT`    | `/movies/{id}`<br>`/api/movies/{id}`                   | Update an existing movie.                      | Admin Only | Null-safe updates (only updates provided fields) |
| `DELETE` | `/movies/{id}`<br>`/api/movies/{id}`                   | Delete a movie.                                | Admin Only | Cascades to reviews                              |

### **Reviews**

| Method   | Endpoint                                                     | Description                           | Access                              | Notes                             |
| :------- | :----------------------------------------------------------- | :------------------------------------ | :---------------------------------- | :-------------------------------- |
| `GET`    | `/reviews/movie/{movieId}`<br>`/api/reviews/movie/{movieId}` | Get all reviews for a specific movie. | Public                              | Includes username and movie title |
| `GET`    | `/reviews/user/{userId}`<br>`/api/reviews/user/{userId}`     | Get all reviews by a specific user.   | Public                              | Includes movie title              |
| `POST`   | `/reviews/movie/{movieId}/user/{userId}`<br>`/api/reviews`   | Submit a new review for a movie.      | Authenticated User                  | Rating 1-5, comment optional      |
| `PUT`    | `/reviews/{reviewId}/user/{userId}`<br>`/api/reviews/{id}`   | Update an existing review.            | Authenticated User (Owner Only)     | Updates rating and/or comment     |
| `DELETE` | `/reviews/{reviewId}/user/{userId}`<br>`/api/reviews/{id}`   | Delete a review.                      | Authenticated User (Owner or Admin) | Recalculates movie average        |

### **Users**

| Method | Endpoint              | Description                                 | Access                               |
| :----- | :-------------------- | :------------------------------------------ | :----------------------------------- |
| `POST` | `/api/users/register` | Register a new user (alternative endpoint). | Public                               |
| `POST` | `/api/users/login`    | Authenticate user (alternative endpoint).   | Public                               |
| `GET`  | `/api/users/{id}`     | Get user details.                           | Authenticated (Optional: Admin Only) |

---

## 🤝 4. Group Collaboration (GIT Workflow)

- **Repository:** `https://github.com/revature-training-projects-team8/movie-review-app-backend`
- **Branching Strategy:** Feature Branch Workflow (Simplified GitHub Flow).
  - `main` (or `master`): Production-ready code.
  - `munkh-branch-be`: Active development branch with latest features.
  - `feature/enhanced-backend`: New feature branch for ongoing enhancements.
  - Feature branches for individual tasks (e.g., `feature/login-form`, `feature/movie-api`).
- **Pull Requests (PRs):** **Mandatory** for code review before merging into main branches.
- **Issue Tracking:** Use GitHub Issues, Jira, or similar to track tasks, bugs, and enhancements.
- **Recent Commits:**
  - Added authentication with BCrypt password encoding
  - Implemented dual endpoint support (`/api/*` and `/*`)
  - Enhanced CORS to support multiple frontend ports
  - Created comprehensive test suite (44 tests, all passing)

---

## 🗓️ 5. Development Workflow (3 Weeks Training Context)

### **Week 1: Backend Foundation & Database**

| Focus               | Key Activities                                                                                               | Status      |
| :------------------ | :----------------------------------------------------------------------------------------------------------- | :---------- |
| **Project Setup**   | Initialize Spring Boot project and Git repository.                                                           | ✅ Complete |
| **Database & ORM**  | Define `Movie`, `User`, `Review` JPA Entities and Repositories. Set up H2/MySQL.                             | ✅ Complete |
| **API Development** | Implement basic `MovieController` with **GET** endpoints (`/movies`, `/movies/{id}`).                        | ✅ Complete |
| **CRUD & Testing**  | Add **POST/PUT/DELETE** for movies. Test APIs using Postman/Insomnia.                                        | ✅ Complete |
| **User Setup**      | Implement user registration logic (`UserController`, `UserService`). Set up **Spring Security** with BCrypt. | ✅ Complete |

### **Week 2: Frontend & Integration**

| Focus                 | Key Activities                                                                                                       | Status      |
| :-------------------- | :------------------------------------------------------------------------------------------------------------------- | :---------- |
| **React Setup**       | Initialize React project. Design basic UI components (`Header`, `MovieCard`).                                        | ✅ Complete |
| **Integration**       | Implement `HomePage` to fetch and display movies from the backend (`GET /movies`).                                   | ✅ Complete |
| **Routing & Details** | Implement `MovieDetailPage` (display movie details, list reviews). Set up **React Router**.                          | ✅ Complete |
| **Auth Integration**  | Implement **Login** and **Registration** forms, connecting to backend `/auth` endpoints. BCrypt password validation. | ✅ Complete |
| **CORS Resolution**   | Configure CORS to support both `localhost:3000` and `localhost:3001`. Dual endpoint support.                         | ✅ Complete |

### **Week 3: Reviews, Enhancements & Deployment**

| Focus                     | Key Activities                                                                                                         | Status         |
| :------------------------ | :--------------------------------------------------------------------------------------------------------------------- | :------------- |
| **Core Feature**          | Implement the `ReviewForm` component. Connect to backend `POST /reviews`. Implement review **update/delete**.          | ✅ Complete    |
| **Refinement**            | Refine UI/UX, add responsiveness, implement **error handling** and loading states.                                     | 🔄 In Progress |
| **Security Audit**        | Enhance backend security with BCrypt, role-based authorization. Add **validation** to forms.                           | ✅ Complete    |
| **Testing**               | Complete unit and integration testing. 44 tests passing (MovieController, ReviewController, UserController, Services). | ✅ Complete    |
| **Documentation**         | Complete technical documentation (DOCU.md, BACKEND_CONFIGURATION.md, MYSQL_SETUP.md, QUICK_START.md).                  | ✅ Complete    |
| **Deployment (Optional)** | Basic deployment to a cloud platform (e.g., Heroku, AWS).                                                              | ⏳ Pending     |

---

## 📝 Movie Review App: User Stories

### A. General User Stories (Public Access)

| Goal              | User Story                                                                                                                                                                     |
| :---------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Browse Movies** | As a **movie enthusiast**, I want to **view a list of all available movies** with basic details (title, poster), so that I can discover and scan new films.                    |
| **View Details**  | As a **movie enthusiast**, I want to **see comprehensive information** (description, director, genre, average rating) for a specific movie, so that I can learn more about it. |
| **Read Reviews**  | As a **movie enthusiast**, I want to **read existing reviews** for a movie, so that I can understand what others think.                                                        |
| **Search**        | As a **movie enthusiast**, I want to **search for movies by title or genre**, so that I can find specific films easily.                                                        |

### B. Authenticated User Stories (Requires Login)

| Goal               | User Story                                                                                                                             |
| :----------------- | :------------------------------------------------------------------------------------------------------------------------------------- |
| **Authentication** | As a **user**, I want to **register** and **log in** with my credentials, so that I can access personalized and interactive features.  |
| **Submit Reviews** | As a **logged-in user**, I want to **submit a star rating and a text comment** for a movie, so that I can express my opinion.          |
| **Manage Reviews** | As a **logged-in user**, I want to **edit or delete my own previously submitted review**, so that I can correct or remove my thoughts. |

### C. Administrator User Stories (Requires Admin Role)

| Goal                   | User Story                                                                                                                       |
| :--------------------- | :------------------------------------------------------------------------------------------------------------------------------- |
| **Movie Management**   | As an **administrator**, I want to **add, edit, and delete any movie** from the database, so that I can manage the film catalog. |
| **Content Moderation** | As an **administrator**, I want to **delete any user's review**, so that I can moderate inappropriate content.                   |

---

## 🛠️ Technical Implementation Details

### **Current Technology Stack**

- **Backend:** Java 21.0.8, Spring Boot 3.5.6, Spring MVC, Spring Data JPA, Spring Security
- **Database:** MySQL 8.0.43 (production), H2 (testing)
- **ORM:** Hibernate 6.6.29.Final
- **Web Server:** Apache Tomcat 10.1.46
- **Security:** BCrypt password encoding (strength 10)
- **Testing:** JUnit 5, Mockito, MockMvc, 44 tests (all passing)
- **Build Tool:** Maven 3.x
- **Version Control:** Git, GitHub

### **Key Features Implemented**

1. **Dual Endpoint Support:** All endpoints available at both `/api/*` and `/*` paths for frontend flexibility
2. **CORS Configuration:** Supports multiple origins (`localhost:3000`, `localhost:3001`)
3. **Authentication:** BCrypt-based password hashing and validation
4. **Database Integration:** MySQL with environment-variable-based credentials
5. **Comprehensive Testing:** Full test coverage for all controllers and services
6. **Error Handling:** Custom exceptions (`ResourceNotFoundException`, `DuplicateResourceException`, `ValidationException`)
7. **Data Validation:** Bean validation with `@Valid`, `@NotBlank`, `@Size`, `@Min`, `@Max`
8. **Profile-Based Security:** Test profile excludes security configuration

### **Additional Documentation**

- **BACKEND_CONFIGURATION.md** - Detailed backend setup and configuration guide
- **MYSQL_SETUP.md** - MySQL database installation and setup instructions
- **QUICK_START.md** - Quick start guide for running the application
- **TEST_SUMMARY.md** - Comprehensive test coverage report
- **update_admin_password.sql** - SQL script for admin password setup

### **Default Credentials**

- **Admin Username:** `admin`
- **Admin Password:** `admin123`
- **BCrypt Hash:** `$2a$10$MbVZUK2kd8HYdAHmuidzLeSfbV97oxp9oo3T04O8dP.zs1Ay6Cw4O`

### **Running the Application**

```bash
# Set database credentials
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"

# Run with Maven
./mvnw spring-boot:run

# Or run the JAR
java -jar target/movie-review-app-0.0.1-SNAPSHOT.jar
```

**Server URL:** `http://localhost:8080`

### **Testing**

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw clean test

# Skip tests during build
./mvnw clean package -DskipTests
```
