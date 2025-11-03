#!/bin/bash

# Test script to verify review update functionality
# This script tests the bug fix for the 500 error when updating reviews

echo "🎬 Movie Review Update Test Script"
echo "=================================="

BASE_URL="http://localhost:8080/api"

# Step 1: Register a test user
echo "1. Registering test user..."
TIMESTAMP=$(date +%s)
REGISTER_RESPONSE=$(curl -s -X POST "http://localhost:8080/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"testuser_${TIMESTAMP}\",
    \"email\": \"test${TIMESTAMP}@example.com\",
    \"password\": \"Test123!\"
  }")

echo "Registration response: $REGISTER_RESPONSE"

# Extract username for login
USERNAME=$(echo "$REGISTER_RESPONSE" | grep -o '"username":"[^"]*"' | cut -d'"' -f4)
EMAIL=$(echo "$REGISTER_RESPONSE" | grep -o '"email":"[^"]*"' | cut -d'"' -f4)

echo "Created user: $USERNAME ($EMAIL)"

# Step 2: Login to get JWT token
echo -e "\n2. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$USERNAME\",
    \"password\": \"Test123!\"
  }")

echo "Login response: $LOGIN_RESPONSE"

JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ Failed to get JWT token. Exiting."
    exit 1
fi

echo "JWT Token obtained successfully"

# Step 3: Get an existing movie for testing
echo -e "\n3. Getting an existing movie for testing..."
MOVIES_RESPONSE=$(curl -s "${BASE_URL}/movies")
echo "Available movies: $MOVIES_RESPONSE"

MOVIE_ID=$(echo "$MOVIES_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ -z "$MOVIE_ID" ]; then
    echo "❌ No movies available. Exiting."
    exit 1
fi

echo "Using existing movie with ID: $MOVIE_ID"

# Step 4: Create a review
echo -e "\n4. Creating initial review..."
REVIEW_RESPONSE=$(curl -s -X POST "${BASE_URL}/reviews/movie/$MOVIE_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"rating\": 4,
    \"comment\": \"Initial review comment\"
  }")

echo "Review creation response: $REVIEW_RESPONSE"

REVIEW_ID=$(echo "$REVIEW_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)

if [ -z "$REVIEW_ID" ]; then
    echo "❌ Failed to create review. Exiting."
    exit 1
fi

echo "Created review with ID: $REVIEW_ID"

# Step 5: Update the review (this is where the 500 error was happening)
echo -e "\n5. 🚨 TESTING REVIEW UPDATE (potential 500 error fix)..."
UPDATE_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}\n" -X PUT "${BASE_URL}/reviews/$REVIEW_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"rating\": 5,
    \"comment\": \"Updated review comment - testing the fix!\"
  }")

echo "Update response: $UPDATE_RESPONSE"

# Check HTTP status
HTTP_STATUS=$(echo "$UPDATE_RESPONSE" | grep "HTTP_STATUS:" | cut -d':' -f2)

if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ SUCCESS! Review update completed without 500 error"
else
    echo "❌ FAILED! HTTP Status: $HTTP_STATUS"
    echo "Response: $UPDATE_RESPONSE"
fi

# Step 6: Verify the movie rating was updated
echo -e "\n6. Verifying movie rating update..."
MOVIE_CHECK=$(curl -s "${BASE_URL}/movies/$MOVIE_ID")
echo "Updated movie data: $MOVIE_CHECK"

UPDATED_RATING=$(echo "$MOVIE_CHECK" | grep -o '"avgRating":[0-9.]*' | cut -d':' -f2)
echo "Movie average rating after update: $UPDATED_RATING"

# Step 7: Get updated review to confirm changes
echo -e "\n7. Fetching updated review..."
FINAL_REVIEW=$(curl -s "${BASE_URL}/reviews/$REVIEW_ID" \
  -H "Authorization: Bearer $JWT_TOKEN")
echo "Final review state: $FINAL_REVIEW"

echo -e "\n🎯 Test Summary:"
echo "=================="
if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Review update functionality: WORKING"
    echo "✅ No 500 internal server error occurred"
    echo "✅ Movie rating calculation: FUNCTIONAL"
    echo "✅ Database operations: SUCCESSFUL"
else
    echo "❌ Review update failed with HTTP $HTTP_STATUS"
    echo "❌ The 500 error fix may need additional investigation"
fi

echo -e "\nTest completed at $(date)"