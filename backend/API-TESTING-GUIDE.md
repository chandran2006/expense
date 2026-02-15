# SaveUp - API Testing Guide

## Complete Testing Scenarios

This guide provides step-by-step testing scenarios for all API endpoints.

---

## Scenario 1: New User Registration & First Transaction

### Step 1: Register a New User
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "password": "alice123"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "alice@example.com",
    "name": "Alice Johnson",
    "role": "USER"
  }
}
```

**Save the token for subsequent requests!**

### Step 2: Add First Income
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "INCOME",
  "category": "Salary",
  "amount": 50000.00,
  "description": "January salary",
  "date": "2024-01-01"
}
```

### Step 3: Add Some Expenses
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 2500.00,
  "description": "Groceries for the week",
  "date": "2024-01-05"
}
```

### Step 4: View All Transactions
```http
GET http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
```

---

## Scenario 2: Budget Management & Tracking

### Step 1: Set Monthly Budget
```http
POST http://localhost:8080/api/budget
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "month": "2024-01",
  "limitAmount": 30000.00
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Budget set successfully",
  "data": {
    "id": 1,
    "month": "2024-01",
    "limitAmount": 30000.00,
    "currentSpending": 2500.00,
    "exceeded": false
  }
}
```

### Step 2: Add More Expenses
Add multiple expenses to test budget tracking:

```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Transport",
  "amount": 3000.00,
  "description": "Monthly fuel",
  "date": "2024-01-07"
}
```

```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Shopping",
  "amount": 8000.00,
  "description": "New clothes",
  "date": "2024-01-10"
}
```

### Step 3: Check Budget Status
```http
GET http://localhost:8080/api/budget/2024-01
Authorization: Bearer YOUR_TOKEN
```

### Step 4: Exceed Budget (Test Warning)
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Entertainment",
  "amount": 20000.00,
  "description": "Vacation trip",
  "date": "2024-01-15"
}
```

### Step 5: Check Budget Again
```http
GET http://localhost:8080/api/budget/2024-01
Authorization: Bearer YOUR_TOKEN
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Warning: Budget exceeded!",
  "data": {
    "id": 1,
    "month": "2024-01",
    "limitAmount": 30000.00,
    "currentSpending": 33500.00,
    "exceeded": true
  }
}
```

---

## Scenario 3: Monthly Summary & Analysis

### Step 1: Get Monthly Summary
```http
GET http://localhost:8080/api/transactions/summary/2024-01
Authorization: Bearer YOUR_TOKEN
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Monthly summary retrieved successfully",
  "data": {
    "totalIncome": 50000.00,
    "totalExpense": 33500.00,
    "balance": 16500.00,
    "categoryWiseExpense": {
      "Food": 2500.00,
      "Transport": 3000.00,
      "Shopping": 8000.00,
      "Entertainment": 20000.00
    }
  }
}
```

---

## Scenario 4: AI Chatbot Interactions

### Query 1: Total Expense
```http
POST http://localhost:8080/api/ai/chat
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "message": "What is my total expense this month?"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Response generated successfully",
  "data": {
    "response": "You spent ₹33500.00 in total this month."
  }
}
```

### Query 2: Category-Specific Expense
```http
POST http://localhost:8080/api/ai/chat
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "message": "How much did I spend on food?"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Response generated successfully",
  "data": {
    "response": "You spent ₹2500.00 on Food this month."
  }
}
```

### Query 3: Saving Tips
```http
POST http://localhost:8080/api/ai/chat
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "message": "Give me some saving tips"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Response generated successfully",
  "data": {
    "response": "Here are some saving tips:\n1. Track all your expenses daily\n2. Set a monthly budget and stick to it\n3. Reduce unnecessary subscriptions\n4. Cook at home instead of eating out\n5. Use the 50-30-20 rule: 50% needs, 30% wants, 20% savings"
  }
}
```

### Query 4: Current Balance
```http
POST http://localhost:8080/api/ai/chat
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "message": "What is my current balance?"
}
```

### Query 5: Budget Check
```http
POST http://localhost:8080/api/ai/chat
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "message": "Am I overspending?"
}
```

---

## Scenario 5: Profile Management

### Step 1: Get Current Profile
```http
GET http://localhost:8080/api/user/profile
Authorization: Bearer YOUR_TOKEN
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "role": "USER",
    "preferredLanguage": "EN",
    "themeMode": "LIGHT"
  }
}
```

### Step 2: Update Profile
```http
PUT http://localhost:8080/api/user/profile
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "name": "Alice J. Smith",
  "preferredLanguage": "TA",
  "themeMode": "DARK"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "name": "Alice J. Smith",
    "email": "alice@example.com",
    "role": "USER",
    "preferredLanguage": "TA",
    "themeMode": "DARK"
  }
}
```

---

## Scenario 6: Transaction CRUD Operations

### Create Transaction
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Health",
  "amount": 1500.00,
  "description": "Doctor consultation",
  "date": "2024-01-20"
}
```

### Read All Transactions
```http
GET http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
```

### Update Transaction (Replace ID with actual transaction ID)
```http
PUT http://localhost:8080/api/transactions/5
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Health",
  "amount": 2000.00,
  "description": "Doctor consultation + medicines",
  "date": "2024-01-20"
}
```

### Delete Transaction
```http
DELETE http://localhost:8080/api/transactions/5
Authorization: Bearer YOUR_TOKEN
```

---

## Scenario 7: Error Handling Tests

### Test 1: Invalid Email Format
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Test User",
  "email": "invalid-email",
  "password": "test123"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Invalid email format"
  }
}
```

### Test 2: Duplicate Email
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Another User",
  "email": "alice@example.com",
  "password": "test123"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Email already exists",
  "data": null
}
```

### Test 3: Invalid Credentials
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "wrongpassword"
}
```

**Expected Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

### Test 4: Missing Authorization Token
```http
GET http://localhost:8080/api/transactions
```

**Expected Response (403 Forbidden)**

### Test 5: Invalid Transaction Amount
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Food",
  "amount": -500.00,
  "description": "Invalid amount",
  "date": "2024-01-15"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "amount": "Amount must be positive"
  }
}
```

---

## Scenario 8: Multi-Month Testing

### January Transactions
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "INCOME",
  "category": "Salary",
  "amount": 50000.00,
  "description": "January salary",
  "date": "2024-01-01"
}
```

### February Transactions
```http
POST http://localhost:8080/api/transactions
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "type": "INCOME",
  "category": "Salary",
  "amount": 55000.00,
  "description": "February salary with bonus",
  "date": "2024-02-01"
}
```

### Set Budget for February
```http
POST http://localhost:8080/api/budget
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "month": "2024-02",
  "limitAmount": 35000.00
}
```

### Get January Summary
```http
GET http://localhost:8080/api/transactions/summary/2024-01
Authorization: Bearer YOUR_TOKEN
```

### Get February Summary
```http
GET http://localhost:8080/api/transactions/summary/2024-02
Authorization: Bearer YOUR_TOKEN
```

---

## Testing Checklist

### Authentication
- [ ] Register with valid data
- [ ] Register with invalid email
- [ ] Register with duplicate email
- [ ] Login with correct credentials
- [ ] Login with wrong password
- [ ] Use token in subsequent requests

### Transactions
- [ ] Add income transaction
- [ ] Add expense transaction
- [ ] Add transaction with negative amount (should fail)
- [ ] Get all transactions
- [ ] Update transaction
- [ ] Delete transaction
- [ ] Get monthly summary

### Budget
- [ ] Set budget for current month
- [ ] Check budget status
- [ ] Exceed budget and verify warning
- [ ] Set budget for different month

### AI Chatbot
- [ ] Ask about total expense
- [ ] Ask about total income
- [ ] Ask about specific category
- [ ] Ask for saving tips
- [ ] Ask about balance
- [ ] Ask about overspending

### Profile
- [ ] Get profile
- [ ] Update name
- [ ] Update language preference
- [ ] Update theme mode

### Error Handling
- [ ] Test without authorization token
- [ ] Test with expired token
- [ ] Test with invalid data
- [ ] Test validation errors

---

## Performance Testing

### Load Test Scenario
1. Register 10 users
2. Each user adds 50 transactions
3. Each user queries monthly summary
4. Measure response times

### Expected Performance
- Registration: < 500ms
- Login: < 300ms
- Add Transaction: < 200ms
- Get Transactions: < 500ms
- Monthly Summary: < 800ms
- AI Chat: < 300ms

---

## Security Testing

### Test Cases
1. **SQL Injection**: Try injecting SQL in email field
2. **XSS**: Try injecting scripts in description field
3. **Token Tampering**: Modify JWT token and test
4. **Unauthorized Access**: Access other user's data
5. **Brute Force**: Multiple failed login attempts

---

## Integration Testing Tips

1. **Test in Order**: Follow scenarios sequentially
2. **Clean State**: Start with fresh database for each test suite
3. **Save Tokens**: Store JWT tokens for reuse
4. **Verify Data**: Check database after each operation
5. **Test Edge Cases**: Empty strings, null values, boundary values

---

## Automated Testing Script (Bash)

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
TOKEN=""

# Register
echo "Testing Registration..."
RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"test123"}')
echo $RESPONSE

# Login
echo "Testing Login..."
RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}')
TOKEN=$(echo $RESPONSE | jq -r '.data.token')
echo "Token: $TOKEN"

# Add Transaction
echo "Testing Add Transaction..."
curl -s -X POST $BASE_URL/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","category":"Food","amount":500,"description":"Test","date":"2024-01-15"}'

echo "All tests completed!"
```

---

## Conclusion

This testing guide covers all major scenarios for the SaveUp API. Use it to ensure all features work correctly before deployment.

For automated testing, consider using:
- **Postman Collection Runner**
- **JUnit/TestNG** for unit tests
- **REST Assured** for API tests
- **JMeter** for load testing
