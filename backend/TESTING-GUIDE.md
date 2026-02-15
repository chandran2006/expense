# SaveUp Backend - Feature Testing Guide

## Prerequisites
1. MySQL running on localhost:3306
2. Database 'saveup' created
3. Backend running on port 8080

## Test Sequence

### 1. Create Database
```sql
CREATE DATABASE IF NOT EXISTS saveup;
USE saveup;
```

### 2. Start Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. Test Authentication

#### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@saveup.com",
    "password": "test123"
  }'
```

Expected Response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGc...",
    "user": {
      "id": 1,
      "name": "Test User",
      "email": "test@saveup.com"
    }
  }
}
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@saveup.com",
    "password": "test123"
  }'
```

**Save the token from response for next requests**

### 4. Test Budget Feature

#### Create Budget
```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "month": "2024-01",
    "limitAmount": 30000
  }'
```

### 5. Test Transaction Feature

#### Add Income
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "INCOME",
    "category": "Salary",
    "amount": 50000,
    "description": "Monthly salary",
    "date": "2024-01-01"
  }'
```

#### Add Expense (Should trigger daily limit check)
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EXPENSE",
    "category": "Food",
    "amount": 2500,
    "description": "Groceries",
    "date": "2024-01-15"
  }'
```

#### Get All Transactions
```bash
curl -X GET http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 6. Test Reminder Feature

#### Create Reminder
```bash
curl -X POST http://localhost:8080/api/reminders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Pay Credit Card",
    "message": "Credit card payment due",
    "reminderDate": "2024-01-20T10:00:00"
  }'
```

#### Get All Reminders
```bash
curl -X GET http://localhost:8080/api/reminders \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Update Reminder
```bash
curl -X PUT http://localhost:8080/api/reminders/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Pay Credit Card - Updated",
    "message": "Payment due tomorrow",
    "reminderDate": "2024-01-21T10:00:00"
  }'
```

#### Delete Reminder
```bash
curl -X DELETE http://localhost:8080/api/reminders/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 7. Test Notification Feature

#### Get All Notifications
```bash
curl -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get Unread Notifications
```bash
curl -X GET http://localhost:8080/api/notifications/unread \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Mark Notification as Read
```bash
curl -X PUT http://localhost:8080/api/notifications/1/read \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 8. Test Analytics Features

#### Get Health Score
```bash
curl -X GET http://localhost:8080/api/analytics/health-score \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Expected Response:
```json
{
  "success": true,
  "message": "Health score calculated successfully",
  "data": {
    "score": 78.5,
    "status": "Good",
    "savingsRate": 35.0,
    "budgetDiscipline": 85.0,
    "expenseStability": 72.0
  }
}
```

#### Get Expense Prediction
```bash
curl -X GET http://localhost:8080/api/analytics/predict-expense \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Expected Response:
```json
{
  "success": true,
  "message": "Expense prediction generated successfully",
  "data": {
    "predictedExpense": 28500.0,
    "confidenceLevel": "Medium"
  }
}
```

### 9. Test Receipt OCR Feature

#### Upload Receipt (Windows PowerShell)
```powershell
$token = "YOUR_TOKEN"
$filePath = "C:\path\to\receipt.jpg"
$uri = "http://localhost:8080/api/receipt/upload"

$form = @{
    file = Get-Item -Path $filePath
}

Invoke-RestMethod -Uri $uri -Method Post -Headers @{Authorization="Bearer $token"} -Form $form
```

#### Upload Receipt (curl)
```bash
curl -X POST http://localhost:8080/api/receipt/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/receipt.jpg"
```

#### Get All Receipts
```bash
curl -X GET http://localhost:8080/api/receipt \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 10. Test Scheduler (Automatic)

The reminder scheduler runs every 5 minutes automatically.

**To verify:**
1. Create a reminder with past date/time
2. Wait 5 minutes
3. Check notifications - should see reminder notification

### 11. Test Auto-Notifications

#### Daily Limit Alert
1. Set monthly budget: 30000
2. Daily limit = 30000 / 31 = ~968
3. Add expense > 968
4. Check notifications - should see daily limit alert

#### Monthly Budget Alert
1. Add multiple expenses totaling > 30000
2. Check notifications - should see budget exceeded alert

## Verification Checklist

- [ ] User registration works
- [ ] User login works and returns JWT token
- [ ] Budget creation works
- [ ] Transaction creation works
- [ ] Daily limit notification triggered
- [ ] Monthly budget notification triggered
- [ ] Reminder CRUD operations work
- [ ] Reminder scheduler runs every 5 minutes
- [ ] Notifications are created automatically
- [ ] Mark notification as read works
- [ ] Health score calculation works
- [ ] Expense prediction works
- [ ] Receipt upload works (if Tesseract installed)
- [ ] Auto-expense creation from receipt works
- [ ] All endpoints require JWT authentication
- [ ] Swagger UI accessible at http://localhost:8080/swagger-ui.html

## Database Verification

```sql
-- Check all tables created
SHOW TABLES;

-- Check users
SELECT * FROM users;

-- Check transactions
SELECT * FROM transactions;

-- Check budgets
SELECT * FROM budgets;

-- Check reminders
SELECT * FROM reminders;

-- Check notifications
SELECT * FROM notifications;

-- Check receipts
SELECT * FROM receipts;
```

## Common Issues & Solutions

### Issue: Database connection failed
**Solution:** 
- Verify MySQL is running
- Check database name is 'saveup'
- Verify credentials in application.yml

### Issue: JWT token invalid
**Solution:**
- Login again to get fresh token
- Check token is included in Authorization header

### Issue: Tesseract not found
**Solution:**
- Install Tesseract OCR
- Windows: Download from GitHub
- Linux: `sudo apt-get install tesseract-ocr`
- macOS: `brew install tesseract`

### Issue: Scheduler not running
**Solution:**
- Check @EnableScheduling in SchedulerConfig
- Verify no exceptions in logs
- Check application logs for "Checking pending reminders..."

### Issue: CORS error from frontend
**Solution:**
- Verify frontend URL in SecurityConfig
- Check CORS configuration includes your frontend port

## Performance Testing

### Load Test Transactions
```bash
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/transactions \
    -H "Authorization: Bearer YOUR_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"type\": \"EXPENSE\",
      \"category\": \"Test\",
      \"amount\": $((RANDOM % 1000 + 100)),
      \"description\": \"Test transaction $i\",
      \"date\": \"2024-01-15\"
    }" &
done
wait
```

## Success Criteria

✅ All endpoints return proper JSON responses
✅ Authentication works correctly
✅ All CRUD operations functional
✅ Scheduler runs automatically
✅ Notifications created automatically
✅ Health score calculated correctly
✅ Predictions generated
✅ Receipt OCR processes images
✅ No errors in application logs
✅ Database tables populated correctly

## Next Steps

1. Test all endpoints using Postman collection
2. Verify scheduler execution in logs
3. Test with real receipt images
4. Monitor application performance
5. Deploy to production environment
