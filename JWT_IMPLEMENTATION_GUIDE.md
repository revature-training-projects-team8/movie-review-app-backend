# JWT Authentication Implementation Guide

## Overview
This document describes the JWT (JSON Web Token) authentication and authorization system implemented in the Movie Review Application.

## What Was Implemented

### 1. Dependencies Added (pom.xml)
```xml
<!-- JWT dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### 2. New Security Components

#### JwtUtil.java (`src/main/java/com/moviereview/security/JwtUtil.java`)
- **Purpose**: Handles JWT token generation, validation, and extraction
- **Key Methods**:
  - `generateToken(String username, String role)` - Creates a new JWT token
  - `validateToken(String token, UserDetails userDetails)` - Validates token authenticity
  - `extractUsername(String token)` - Extracts username from token
  - `extractRole(String token)` - Extracts user role from token

#### CustomUserDetailsService.java (`src/main/java/com/moviereview/security/CustomUserDetailsService.java`)
- **Purpose**: Implements Spring Security's UserDetailsService interface
- **Functionality**: Loads user details from database for authentication
- **Integration**: Works with Spring Security's authentication mechanism

#### JwtAuthenticationFilter.java (`src/main/java/com/moviereview/security/JwtAuthenticationFilter.java`)
- **Purpose**: Intercepts HTTP requests and validates JWT tokens
- **Flow**:
  1. Extracts JWT token from Authorization header (`Bearer <token>`)
  2. Validates token and loads user details
  3. Sets authentication in Spring Security context
  4. Allows request to proceed

### 3. Updated Components

#### SecurityConfig.java
**Changes**:
- Added `@RequiredArgsConstructor` for dependency injection
- Injected `JwtAuthenticationFilter` and `UserDetailsService`
- Changed session management to `STATELESS` (no server-side sessions)
- Added `AuthenticationProvider` bean configuration
- Added `AuthenticationManager` bean configuration
- Registered `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`

**Security Flow**:
```
Request → CORS Filter → JWT Filter → Authorization Rules → Controller
```

#### AuthController.java
**Changes**:
- Injected `JwtUtil` and `AuthenticationManager`
- Updated `login()` endpoint to:
  - Authenticate user using Spring Security's `AuthenticationManager`
  - Generate JWT token on successful authentication
  - Return token in `LoginResponse`
- Updated `register()` endpoint to:
  - Create new user
  - Generate JWT token immediately
  - Return token in response

#### LoginResponse.java (DTO)
**Changes**:
- Added `String token` field to include JWT in response

### 4. Configuration (application.properties)
```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000
# jwt.expiration in milliseconds (86400000 ms = 24 hours)
```

**⚠️ IMPORTANT**: Change the `jwt.secret` before deploying to production!

### 5. Test Configuration Updates
**TestSecurityConfig.java**:
- Added `@MockBean` for `JwtUtil`, `AuthenticationManager`, and `CustomUserDetailsService`
- Ensures tests run without requiring actual JWT components

## How to Use JWT Authentication

### 1. User Registration
**Endpoint**: `POST /auth/register`

**Request**:
```json
{
  "username": "johndoe",
  "password": "password123",
  "email": "john@example.com"
}
```

**Response**:
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "USER",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInN1YiI6ImpvaG5kb2UiLCJpYXQiOjE3Mjk1NzQ0MDAsImV4cCI6MTcyOTY2MDgwMH0.example..."
}
```

### 2. User Login
**Endpoint**: `POST /auth/login`

**Request**:
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response**:
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "USER",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInN1YiI6ImpvaG5kb2UiLCJpYXQiOjE3Mjk1NzQ0MDAsImV4cCI6MTcyOTY2MDgwMH0.example..."
}
```

### 3. Making Authenticated Requests
For any endpoint requiring authentication, include the JWT token in the Authorization header:

**Header**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInN1YiI6ImpvaG5kb2UiLCJpYXQiOjE3Mjk1NzQ0MDAsImV4cCI6MTcyOTY2MDgwMH0.example...
```

**Example with curl**:
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**Example with JavaScript/Fetch**:
```javascript
fetch('http://localhost:8080/api/users/1', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

## Current Endpoint Access Rules

### Public Endpoints (No Authentication Required):
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `GET /auth/test/**` - Test endpoints
- `POST /api/users/register` - Alternative registration
- `POST /api/users/login` - Alternative login
- `GET /api/movies/**` - Browse movies (public viewing)
- `GET /api/reviews/**` - View reviews (public viewing)
- `POST /api/reviews/**` - Submit reviews (public)
- `PUT /api/reviews/**` - Update reviews (public)
- `DELETE /api/reviews/**` - Delete reviews (public)
- `GET /actuator/health` - Health check
- `GET /error` - Error pages

### Protected Endpoints (Authentication Required):
- All other endpoints require valid JWT token

## JWT Token Structure

### Token Claims:
- `sub` (Subject): Username
- `role`: User role (USER, ADMIN, etc.)
- `iat` (Issued At): Timestamp when token was created
- `exp` (Expiration): Timestamp when token expires (24 hours by default)

### Token Example (decoded):
```json
{
  "role": "USER",
  "sub": "johndoe",
  "iat": 1729574400,
  "exp": 1729660800
}
```

## Security Features

### 1. Stateless Authentication
- No server-side session storage
- JWT token contains all necessary information
- Scales horizontally easily

### 2. Token Expiration
- Tokens expire after 24 hours (configurable)
- Users must re-login after expiration
- Prevents unauthorized access with old tokens

### 3. Password Security
- Passwords hashed with BCrypt
- Never stored or transmitted in plain text
- Secure password validation

### 4. CORS Configuration
- Allows requests from localhost:3000 and localhost:3001
- Supports credentials (cookies, authorization headers)
- Exposes Authorization header to frontend

## Testing JWT Authentication

### Manual Testing Steps:

1. **Start the application**:
```bash
./mvnw spring-boot:run
```

2. **Register a new user**:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123","email":"test@example.com"}'
```

3. **Copy the token from the response**

4. **Test authenticated endpoint**:
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Common Issues & Solutions

### Issue 1: "401 Unauthorized" on authenticated requests
**Solution**: Ensure you're including the token in the Authorization header with "Bearer " prefix

### Issue 2: Token expired
**Solution**: Login again to get a new token. Tokens expire after 24 hours by default.

### Issue 3: "Invalid username or password"
**Solution**: 
- Verify credentials are correct
- Check that user exists in database
- Ensure password was properly hashed during registration

### Issue 4: CORS errors in browser
**Solution**: 
- Verify frontend URL is in allowed origins list in SecurityConfig
- Ensure `allowCredentials = "true"` is set in CORS configuration

## Production Considerations

### 1. Change JWT Secret
⚠️ **CRITICAL**: Change the `jwt.secret` in application.properties to a strong, random value:
```properties
jwt.secret=${JWT_SECRET:your-production-secret-key-minimum-256-bits}
```

### 2. Use Environment Variables
```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

Set in production environment:
```bash
export JWT_SECRET="your-very-long-and-random-secret-key-here"
export JWT_EXPIRATION=3600000  # 1 hour
```

### 3. Enable HTTPS
- JWT tokens should only be transmitted over HTTPS in production
- Update CORS configuration to allow only production domains

### 4. Implement Token Refresh
Consider implementing refresh tokens for better user experience:
- Short-lived access tokens (15 minutes)
- Long-lived refresh tokens (7 days)
- Refresh endpoint to get new access token

### 5. Add Rate Limiting
Protect login endpoint from brute force attacks:
- Limit login attempts per IP
- Implement account lockout after failed attempts
- Add CAPTCHA for suspicious activity

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         Client                              │
│                    (Browser/Mobile App)                     │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ 1. POST /auth/login (username, password)
               ▼
┌──────────────────────────────────────────────────────────────┐
│                      AuthController                          │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  authenticationManager.authenticate()                  │ │
│  │  jwtUtil.generateToken(username, role)                │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ 2. Returns JWT token
               ▼
┌──────────────────────────────────────────────────────────────┐
│                         Client                              │
│              Stores token (localStorage/memory)             │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ 3. GET /api/movies (Authorization: Bearer <token>)
               ▼
┌──────────────────────────────────────────────────────────────┐
│                 JwtAuthenticationFilter                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Extract token from Authorization header              │ │
│  │  jwtUtil.validateToken()                              │ │
│  │  Set SecurityContext authentication                   │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ 4. Authenticated request proceeds
               ▼
┌──────────────────────────────────────────────────────────────┐
│                     MovieController                          │
│                   (Protected endpoint)                       │
└──────────────────────────────────────────────────────────────┘
```

## Files Created/Modified

### Created:
- `src/main/java/com/moviereview/security/JwtUtil.java`
- `src/main/java/com/moviereview/security/CustomUserDetailsService.java`
- `src/main/java/com/moviereview/security/JwtAuthenticationFilter.java`

### Modified:
- `pom.xml` - Added JWT dependencies
- `src/main/java/com/moviereview/config/SecurityConfig.java` - Added JWT filter and stateless session
- `src/main/java/com/moviereview/controller/AuthController.java` - Added JWT token generation
- `src/main/java/com/moviereview/dto/LoginResponse.java` - Added token field
- `src/main/resources/application.properties` - Added JWT configuration
- `src/test/java/com/moviereview/config/TestSecurityConfig.java` - Added JWT mocks for tests

## Next Steps

1. **Test the JWT authentication** by running the application and trying login/register
2. **Update frontend** to:
   - Store JWT token after login/register
   - Include token in Authorization header for all API requests
   - Handle token expiration (redirect to login)
3. **Consider implementing**:
   - Token refresh mechanism
   - Remember me functionality
   - Role-based authorization (@PreAuthorize annotations)
   - Token blacklist for logout

## Conclusion

JWT authentication has been successfully implemented in the Movie Review Application. The system is now:
- ✅ Stateless and scalable
- ✅ Secure with BCrypt password hashing
- ✅ Token-based with 24-hour expiration
- ✅ Ready for production (after changing JWT secret)
- ✅ Compatible with modern frontend frameworks

For questions or issues, refer to this documentation or check the source code comments.
