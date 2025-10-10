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

- **REST Controllers (`@RestController`):** Define API endpoints for resources (movies, reviews, users). Examples: `MovieController.java`, `ReviewController.java`.
- **Service Layer (`@Service`):** Contains core business logic and orchestrates operations involving multiple repositories. Examples: `MovieService.java`, `ReviewService.java`.
- **Repository Layer (`@Repository`):** Interfaces extending `JpaRepository` to abstract and manage database interactions (CRUD operations). Examples: `MovieRepository.java`.
- **Models/Entities:** POJOs annotated with JPA (`@Entity`, `@Table`, `@Id`) to represent database tables. Examples: `Movie.java`, `Review.java`, `User.java`.
- **DTOs (Data Transfer Objects):** Objects used for transferring data between client and server, separating presentation from JPA entities.
- **Security (Spring Security):** Authentication (e.g., JWT) and Authorization (role-based access control).
- **Configuration:** `application.properties` or `application.yml` for database connection, port, etc.

---

### 2.3. Data-Tier

| Attribute            | Description                                               |
| :------------------- | :-------------------------------------------------------- |
| **Technology Stack** | **SQL** (e.g., MySQL for production, H2 for development)  |
| **Purpose**          | Stores all application data in a **relational database**. |

#### **Schema Design**

| Table       | Key Fields                                                                                             | Relationships                                      | Notes                                        |
| :---------- | :----------------------------------------------------------------------------------------------------- | :------------------------------------------------- | :------------------------------------------- |
| **users**   | `id` (PK), `username` (UNIQUE), `email` (UNIQUE), `password` (HASHED), `role`                          |                                                    | `role` (e.g., 'USER', 'ADMIN')               |
| **movies**  | `id` (PK), `title`, `description`, `release_date`, `genre`, `director`, `poster_url`, `average_rating` |                                                    |                                              |
| **reviews** | `id` (PK), `rating`, `comment`, `created_at`                                                           | `movie_id` (FK to movies), `user_id` (FK to users) | Records user-submitted ratings and comments. |

#### **Data Management**

- **Migrations:** Use tools like Flyway or Liquibase for database schema management, or simple `schema.sql` / `data.sql`.
- **Security:** Store user passwords securely using strong hashing algorithms (e.g., **BCrypt**).

---

## 🌐 3. API Endpoints (RESTful Example)

**Base URL:** `/api/v1`

### **Movies**

| Method   | Endpoint       | Description                                 | Access     |
| :------- | :------------- | :------------------------------------------ | :--------- |
| `GET`    | `/movies`      | Get a list of all movies.                   | Public     |
| `GET`    | `/movies/{id}` | Get detailed information for a movie by ID. | Public     |
| `POST`   | `/movies`      | Add a new movie.                            | Admin Only |
| `PUT`    | `/movies/{id}` | Update an existing movie.                   | Admin Only |
| `DELETE` | `/movies/{id}` | Delete a movie.                             | Admin Only |

### **Reviews**

| Method   | Endpoint                   | Description                           | Access                              |
| :------- | :------------------------- | :------------------------------------ | :---------------------------------- |
| `GET`    | `/reviews/movie/{movieId}` | Get all reviews for a specific movie. | Public                              |
| `POST`   | `/reviews`                 | Submit a new review for a movie.      | Authenticated User                  |
| `PUT`    | `/reviews/{id}`            | Update an existing review.            | Authenticated User (Owner Only)     |
| `DELETE` | `/reviews/{id}`            | Delete a review.                      | Authenticated User (Owner or Admin) |

### **Users / Authentication**

| Method | Endpoint         | Description                                | Access                               |
| :----- | :--------------- | :----------------------------------------- | :----------------------------------- |
| `POST` | `/auth/register` | Register a new user.                       | Public                               |
| `POST` | `/auth/login`    | Authenticate user and receive a JWT token. | Public                               |
| `GET`  | `/users/{id}`    | Get user details.                          | Authenticated (Optional: Admin Only) |

---

## 🤝 4. Group Collaboration (GIT Workflow)

- **Repository:** A single central Git repository.
- **Branching Strategy:** Feature Branch Workflow (Simplified GitHub Flow).
  - `main` (or `master`): Production-ready code.
  - `develop`: Integration branch for ongoing development.
  - `feature/your-feature-name`: Branches for individual tasks (e.g., `feature/login-form`, `feature/movie-api`).
- **Pull Requests (PRs):** **Mandatory** for code review before merging into `develop`.
- **Issue Tracking:** Use GitHub Issues, Jira, or similar to track tasks, bugs, and enhancements.

---

## 🗓️ 5. Development Workflow (3 Weeks Training Context)

### **Week 1: Backend Foundation & Database**

| Focus               | Key Activities                                                                                                     |
| :------------------ | :----------------------------------------------------------------------------------------------------------------- |
| **Project Setup**   | Initialize Spring Boot project and Git repository.                                                                 |
| **Database & ORM**  | Define `Movie`, `User`, `Review` JPA Entities and Repositories. Set up H2/MySQL.                                   |
| **API Development** | Implement basic `MovieController` with **GET** endpoints (`/movies`, `/movies/{id}`).                              |
| **CRUD & Testing**  | Add **POST/PUT/DELETE** for movies. Test APIs using Postman/Insomnia.                                              |
| **User Setup**      | Implement user registration logic (`UserController`, `UserService`). Set up basic **Spring Security** placeholder. |

### **Week 2: Frontend & Integration**

| Focus                 | Key Activities                                                                                                     |
| :-------------------- | :----------------------------------------------------------------------------------------------------------------- |
| **React Setup**       | Initialize React project. Design basic UI components (`Header`, `MovieCard`).                                      |
| **Integration**       | Implement `HomePage` to fetch and display movies from the backend (`GET /movies`).                                 |
| **Routing & Details** | Implement `MovieDetailPage` (display movie details, list reviews). Set up **React Router**.                        |
| **Auth Integration**  | Implement **Login** and **Registration** forms, connecting to backend `/auth` endpoints. Handle JWT token storage. |

### **Week 3: Reviews, Enhancements & Deployment**

| Focus                     | Key Activities                                                                                                     |
| :------------------------ | :----------------------------------------------------------------------------------------------------------------- |
| **Core Feature**          | Implement the `ReviewForm` component. Connect to backend `POST /reviews`. Implement review **update/delete**.      |
| **Refinement**            | Refine UI/UX, add responsiveness, implement **error handling** and loading states.                                 |
| **Security Audit**        | Enhance backend security (e.g., proper JWT implementation, role-based authorization). Add **validation** to forms. |
| **Finalization**          | Final testing (unit, integration). Complete documentation.                                                         |
| **Deployment (Optional)** | Basic deployment to a cloud platform (e.g., Heroku, AWS).                                                          |

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
