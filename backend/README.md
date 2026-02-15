# FinMate AI - Smart Finance Manager Backend

A production-ready Spring Boot backend application for managing personal finances with AI-powered insights.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven**
- **MySQL 8.0**
- **Spring Data JPA**
- **Spring Security**
- **JWT Authentication**
- **Lombok**
- **Swagger/OpenAPI 3.0**
- **AOP for Logging**

## Features

### 1. Authentication & Authorization
- User registration with email validation
- Secure login with JWT token generation
- Password encryption using BCrypt
- Role-based access control (USER, ADMIN)
- Profile management with language and theme preferences

### 2. Transaction Management
- Add income and expense transactions
- Update and delete transactions
- View all transactions
- Monthly summary with income/expense breakdown
- Category-wise expense analysis

### 3. Budget Management
- Set monthly budget limits
- Real-time budget tracking
- Overspending alerts
- Budget vs actual spending comparison

### 4. AI-Powered Chatbot
- Rule-based financial insights
- Natural language query processing
- Spending analysis by category
- Personalized saving tips
- Chat history storage

### 5. Professional Features
- Global exception handling
- AOP-based service layer logging
- DTO pattern for data transfer
- Layered architecture
- CORS configuration
- Swagger API documentation

## Project Structure

```
backend/
├── src/main/java/com/finmate/ai/
│   ├── FinMateAIApplication.java
│   ├── aspect/
│   │   └── LoggingAspect.java
│   ├── config/
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── OpenApiConfig.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AiController.java
│   │   ├── AuthController.java
│   │   ├── BudgetController.java
│   │   ├── TransactionController.java
│   │   └── UserController.java
│   ├── dto/
│   │   ├── ApiResponse.java
│   │   ├── AuthResponse.java
│   │   ├── BudgetDTO.java
│   │   ├── BudgetRequest.java
│   │   ├── ChatRequest.java
│   │   ├── ChatResponse.java
│   │   ├── LoginRequest.java
│   │   ├── MonthlySummaryDTO.java
│   │   ├── RegisterRequest.java
│   │   ├── TransactionDTO.java
│   │   ├── TransactionRequest.java
│   │   ├── UpdateProfileRequest.java
│   │   └── UserDTO.java
│   ├── entity/
│   │   ├── Budget.java
│   │   ├── ChatHistory.java
│   │   ├── Language.java
│   │   ├── Role.java
│   │   ├── ThemeMode.java
│   │   ├── Transaction.java
│   │   ├── TransactionType.java
│   │   └── User.java
│   ├── exception/
│   │   ├── BadRequestException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── repository/
│   │   ├── BudgetRepository.java
│   │   ├── ChatHistoryRepository.java
│   │   ├── TransactionRepository.java
│   │   └── UserRepository.java
│   ├── service/
│   │   ├── AiInsightService.java
│   │   ├── AuthService.java
│   │   ├── BudgetService.java
│   │   ├── TransactionService.java
│   │   └── UserService.java
│   └── util/
│       └── JwtUtil.java
└── src/main/resources/
    └── application.properties
```

## Database Setup

### 1. Create MySQL Database

```sql
CREATE DATABASE finmate_ai;
USE finmate_ai;
```

### 2. Tables (Auto-created by Hibernate)

The application will automatically create the following tables:

- **users** - User accounts with authentication details
- **transactions** - Income and expense records
- **budgets** - Monthly budget limits
- **chat_history** - AI chatbot conversation history

## Installation & Setup

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Steps

1. **Clone the repository**
```bash
cd backend
```

2. **Configure MySQL**
Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=Chandran@2006
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints

#### Authentication APIs

**1. Register User**
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "john@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

**2. Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "john@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

#### User APIs

**3. Get Profile**
```http
GET /api/user/profile
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "preferredLanguage": "EN",
    "themeMode": "LIGHT"
  }
}
```

**4. Update Profile**
```http
PUT /api/user/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "John Updated",
  "preferredLanguage": "TA",
  "themeMode": "DARK"
}

Response:
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "name": "John Updated",
    "email": "john@example.com",
    "role": "USER",
    "preferredLanguage": "TA",
    "themeMode": "DARK"
  }
}
```

#### Transaction APIs

**5. Add Transaction**
```http
POST /api/transactions
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 500.00,
  "description": "Lunch at restaurant",
  "date": "2024-01-15"
}

Response:
{
  "success": true,
  "message": "Transaction added successfully",
  "data": {
    "id": 1,
    "type": "EXPENSE",
    "category": "Food",
    "amount": 500.00,
    "description": "Lunch at restaurant",
    "date": "2024-01-15"
  }
}
```

**6. Get All Transactions**
```http
GET /api/transactions
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Transactions retrieved successfully",
  "data": [
    {
      "id": 1,
      "type": "EXPENSE",
      "category": "Food",
      "amount": 500.00,
      "description": "Lunch at restaurant",
      "date": "2024-01-15"
    }
  ]
}
```

**7. Update Transaction**
```http
PUT /api/transactions/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 600.00,
  "description": "Dinner at restaurant",
  "date": "2024-01-15"
}
```

**8. Delete Transaction**
```http
DELETE /api/transactions/1
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Transaction deleted successfully",
  "data": null
}
```

**9. Get Monthly Summary**
```http
GET /api/transactions/summary/2024-01
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Monthly summary retrieved successfully",
  "data": {
    "totalIncome": 50000.00,
    "totalExpense": 25000.00,
    "balance": 25000.00,
    "categoryWiseExpense": {
      "Food": 4500.00,
      "Transport": 3000.00,
      "Shopping": 8000.00,
      "Bills": 5000.00
    }
  }
}
```

#### Budget APIs

**10. Set Budget**
```http
POST /api/budget
Authorization: Bearer {token}
Content-Type: application/json

{
  "month": "2024-01",
  "limitAmount": 30000.00
}

Response:
{
  "success": true,
  "message": "Budget set successfully",
  "data": {
    "id": 1,
    "month": "2024-01",
    "limitAmount": 30000.00,
    "currentSpending": 25000.00,
    "exceeded": false
  }
}
```

**11. Get Budget Status**
```http
GET /api/budget/2024-01
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Budget status retrieved successfully",
  "data": {
    "id": 1,
    "month": "2024-01",
    "limitAmount": 30000.00,
    "currentSpending": 32000.00,
    "exceeded": true
  }
}
```

#### AI Chatbot APIs

**12. Chat with AI**
```http
POST /api/ai/chat
Authorization: Bearer {token}
Content-Type: application/json

{
  "message": "How much did I spend on food?"
}

Response:
{
  "success": true,
  "message": "Response generated successfully",
  "data": {
    "response": "You spent ₹4500.00 on Food this month."
  }
}
```

**Example Queries:**
- "How much did I spend on food?"
- "What is my total expense?"
- "How much did I earn this month?"
- "Am I overspending?"
- "Give me saving tips"
- "What is my current balance?"

## Postman Collection

### Import into Postman

1. Create a new collection named "FinMate AI"
2. Add environment variables:
   - `baseUrl`: http://localhost:8080
   - `token`: (will be set after login)

### Sample Requests

**Step 1: Register**
```
POST {{baseUrl}}/api/auth/register
Body (JSON):
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "test123"
}
```

**Step 2: Login & Save Token**
```
POST {{baseUrl}}/api/auth/login
Body (JSON):
{
  "email": "test@example.com",
  "password": "test123"
}

// Copy the token from response and set it in environment variable
```

**Step 3: Add Transaction**
```
POST {{baseUrl}}/api/transactions
Headers:
  Authorization: Bearer {{token}}
Body (JSON):
{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 500,
  "description": "Groceries",
  "date": "2024-01-15"
}
```

**Step 4: Set Budget**
```
POST {{baseUrl}}/api/budget
Headers:
  Authorization: Bearer {{token}}
Body (JSON):
{
  "month": "2024-01",
  "limitAmount": 30000
}
```

**Step 5: Chat with AI**
```
POST {{baseUrl}}/api/ai/chat
Headers:
  Authorization: Bearer {{token}}
Body (JSON):
{
  "message": "How much did I spend on food?"
}
```

## Security Features

1. **JWT Authentication**: Stateless token-based authentication
2. **Password Encryption**: BCrypt hashing algorithm
3. **Role-Based Access**: USER and ADMIN roles
4. **CORS Configuration**: Configured for frontend integration
5. **Input Validation**: Bean validation on all request DTOs
6. **Exception Handling**: Global exception handler for consistent error responses

## Logging

The application uses AOP for automatic logging of:
- Method entry with parameters
- Method exit with return values
- Exceptions with error messages
- Execution time for performance monitoring

Logs are available in the console during development.

## Error Handling

All errors return a consistent response format:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

HTTP Status Codes:
- 200: Success
- 400: Bad Request (validation errors)
- 401: Unauthorized (invalid credentials)
- 404: Not Found (resource doesn't exist)
- 500: Internal Server Error

## AI Chatbot Keywords

The AI chatbot recognizes the following keywords:

- **Expense queries**: "total expense", "how much did i spend"
- **Income queries**: "total income", "how much did i earn"
- **Category queries**: "food", "transport", "shopping", "entertainment", "bills", "health", "education"
- **Budget queries**: "overspending", "budget", "exceed"
- **Saving tips**: "saving", "save money", "tips"
- **Balance queries**: "balance", "left"

## Development Tips

1. **Hot Reload**: Use Spring Boot DevTools for automatic restart
2. **Database**: Check `spring.jpa.show-sql=true` to see generated SQL queries
3. **Swagger**: Test all APIs directly from Swagger UI
4. **Logs**: Monitor console for AOP logging output

## Production Deployment

Before deploying to production:

1. Change `jwt.secret` to a strong random value
2. Set `spring.jpa.hibernate.ddl-auto=validate`
3. Configure proper CORS origins
4. Enable HTTPS
5. Set up proper logging (file-based)
6. Configure database connection pooling
7. Add rate limiting
8. Enable actuator endpoints for monitoring

## Support

For issues and questions:
- Email: support@finmate-ai.com
- Documentation: http://localhost:8080/swagger-ui.html

## License

Copyright © 2024 FinMate AI. All rights reserved.
