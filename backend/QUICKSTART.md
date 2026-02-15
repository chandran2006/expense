# SaveUp - Quick Start Guide

## Prerequisites Check

Before starting, ensure you have:
- ✅ JDK 17 or higher installed
- ✅ Maven 3.6+ installed
- ✅ MySQL 8.0+ installed and running
- ✅ IDE (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

## 5-Minute Setup

### Step 1: Database Setup (2 minutes)

Open MySQL command line or MySQL Workbench and run:

```sql
CREATE DATABASE finmate_ai;
```

That's it! Tables will be created automatically.

### Step 2: Configure Application (1 minute)

The application is already configured with your MySQL credentials:
- Username: `root`
- Password: `Chandran@2006`
- Database: `finmate_ai`

If you need to change these, edit `src/main/resources/application.properties`

### Step 3: Build & Run (2 minutes)

Open terminal in the `backend` folder and run:

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Wait for the message: `Started SaveUpApplication in X seconds`

### Step 4: Verify Installation

Open your browser and visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

You should see the interactive API documentation!

## Testing the API

### Option 1: Using Swagger UI (Recommended for Beginners)

1. Go to http://localhost:8080/swagger-ui.html
2. Click on "Authentication" section
3. Try the "Register User" endpoint:
   - Click "Try it out"
   - Fill in the example values
   - Click "Execute"
4. Copy the token from the response
5. Click "Authorize" button at the top
6. Enter: `Bearer YOUR_TOKEN_HERE`
7. Now you can test all other endpoints!

### Option 2: Using Postman

1. Import the collection: `SaveUp-Postman-Collection.json`
2. Create an environment with:
   - `baseUrl`: http://localhost:8080
   - `token`: (leave empty, will be auto-filled)
3. Run "Register User" or "Login" request
4. Token will be automatically saved
5. Test other endpoints!

### Option 3: Using cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"test123\"}"
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"test123\"}"
```

**Add Transaction (replace YOUR_TOKEN):**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"EXPENSE\",\"category\":\"Food\",\"amount\":500,\"description\":\"Lunch\",\"date\":\"2024-01-15\"}"
```

## Common Issues & Solutions

### Issue 1: Port 8080 already in use
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

### Issue 2: MySQL connection refused
**Solution:** 
- Check if MySQL is running: `mysql --version`
- Verify credentials in `application.properties`
- Ensure database `finmate_ai` exists

### Issue 3: Maven build fails
**Solution:**
```bash
# Clean and rebuild
mvn clean
mvn install -U
```

### Issue 4: JWT token expired
**Solution:** Login again to get a new token. Tokens expire after 24 hours.

## Project Structure Overview

```
backend/
├── src/main/java/com/finmate/ai/
│   ├── controller/      → REST API endpoints
│   ├── service/         → Business logic
│   ├── repository/      → Database access
│   ├── entity/          → Database models
│   ├── dto/             → Request/Response objects
│   ├── config/          → Security & Swagger config
│   ├── exception/       → Error handling
│   ├── util/            → JWT utilities
│   └── aspect/          → AOP logging
└── src/main/resources/
    └── application.properties → Configuration
```

## Key Features to Test

### 1. Authentication Flow
1. Register a new user
2. Login with credentials
3. Get JWT token
4. Use token for all other requests

### 2. Transaction Management
1. Add income transaction
2. Add expense transaction
3. View all transactions
4. Get monthly summary
5. Update a transaction
6. Delete a transaction

### 3. Budget Management
1. Set monthly budget (e.g., "2024-01", 30000)
2. Add some expenses
3. Check budget status
4. See if budget is exceeded

### 4. AI Chatbot
Try these questions:
- "What is my total expense?"
- "How much did I spend on food?"
- "Give me saving tips"
- "What is my current balance?"
- "Am I overspending?"

### 5. Profile Management
1. Get user profile
2. Update language preference (EN, TA, HI)
3. Update theme mode (LIGHT, DARK)

## Development Workflow

### Running in IDE

**IntelliJ IDEA:**
1. Open the `backend` folder as a project
2. Wait for Maven to download dependencies
3. Right-click on `SaveUpApplication.java`
4. Select "Run SaveUpApplication"

**VS Code:**
1. Install "Extension Pack for Java"
2. Open the `backend` folder
3. Press F5 or click "Run" on `SaveUpApplication.java`

### Hot Reload (Optional)

Add Spring Boot DevTools dependency (already included):
- Any code change will automatically restart the application
- No need to manually stop and start

### Viewing Logs

All logs appear in the console with:
- Method execution details (AOP logging)
- SQL queries (if `spring.jpa.show-sql=true`)
- Request/Response information
- Error stack traces

## API Testing Checklist

- [ ] Register new user
- [ ] Login and get token
- [ ] Get user profile
- [ ] Update profile
- [ ] Add income transaction
- [ ] Add expense transaction
- [ ] Get all transactions
- [ ] Get monthly summary
- [ ] Set budget
- [ ] Check budget status
- [ ] Chat with AI - total expense
- [ ] Chat with AI - category expense
- [ ] Chat with AI - saving tips
- [ ] Update transaction
- [ ] Delete transaction

## Next Steps

1. **Frontend Integration**: Use the token in Authorization header
2. **Add More Categories**: Customize transaction categories
3. **Enhance AI**: Add more keywords and responses
4. **Add Reports**: Create detailed financial reports
5. **Email Notifications**: Send budget alerts via email

## Support & Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Full README**: See `README.md` for detailed documentation
- **Database Schema**: See `database.sql` for table structures
- **Postman Collection**: Import `SaveUp-Postman-Collection.json`

## Production Checklist

Before deploying to production:
- [ ] Change JWT secret key
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Configure proper CORS origins
- [ ] Enable HTTPS
- [ ] Set up database backups
- [ ] Configure logging to files
- [ ] Add rate limiting
- [ ] Set up monitoring
- [ ] Use environment variables for sensitive data

## Troubleshooting Commands

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check MySQL status
mysql -u root -p

# Clean Maven cache
mvn clean

# Skip tests during build
mvn clean install -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Check running processes on port 8080
netstat -ano | findstr :8080
```

## Happy Coding! 🚀

Your SaveUp backend is now ready to use. Start building amazing financial management features!

For questions or issues, check the logs or refer to the detailed README.md file.
