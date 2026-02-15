# SaveUp - Advanced Features API Documentation

## 🚀 New Features Added

### 1. Notification & Reminder System
### 2. Daily Spending Limit Alert
### 3. Monthly Financial Health Score
### 4. Smart Expense Prediction
### 5. Receipt Scanner (OCR Integration)

---

## 📋 API Endpoints

### **Reminder Management**

#### Create Reminder
```http
POST /api/reminders
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Pay Credit Card Bill",
  "message": "Credit card payment due",
  "reminderDate": "2024-01-15T10:00:00"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reminder created successfully",
  "data": {
    "id": 1,
    "title": "Pay Credit Card Bill",
    "message": "Credit card payment due",
    "reminderDate": "2024-01-15T10:00:00",
    "isCompleted": false,
    "createdAt": "2024-01-10T14:30:00"
  }
}
```

#### Update Reminder
```http
PUT /api/reminders/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "message": "Updated message",
  "reminderDate": "2024-01-16T10:00:00"
}
```

#### Delete Reminder
```http
DELETE /api/reminders/{id}
Authorization: Bearer <token>
```

#### Get All Reminders
```http
GET /api/reminders
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Reminders retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Pay Credit Card Bill",
      "message": "Credit card payment due",
      "reminderDate": "2024-01-15T10:00:00",
      "isCompleted": false,
      "createdAt": "2024-01-10T14:30:00"
    }
  ]
}
```

---

### **Notification Management**

#### Get All Notifications
```http
GET /api/notifications
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Notifications retrieved successfully",
  "data": [
    {
      "id": 1,
      "message": "Daily spending limit exceeded! Today: ₹2500.00, Limit: ₹2000.00",
      "type": "ALERT",
      "isRead": false,
      "createdAt": "2024-01-10T18:30:00"
    },
    {
      "id": 2,
      "message": "Reminder: Pay Credit Card Bill - Credit card payment due",
      "type": "INFO",
      "isRead": false,
      "createdAt": "2024-01-10T10:00:00"
    }
  ]
}
```

#### Get Unread Notifications
```http
GET /api/notifications/unread
Authorization: Bearer <token>
```

#### Mark Notification as Read
```http
PUT /api/notifications/{id}/read
Authorization: Bearer <token>
```

---

### **Analytics & Predictions**

#### Get Financial Health Score
```http
GET /api/analytics/health-score
Authorization: Bearer <token>
```

**Response:**
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

**Health Score Breakdown:**
- **Score Range:**
  - 80-100: Excellent
  - 60-79: Good
  - 40-59: Fair
  - 0-39: Poor

- **Components:**
  - Savings Rate (40%): (Income - Expense) / Income
  - Budget Discipline (30%): Budget adherence percentage
  - Expense Stability (30%): Variance from last 3 months

#### Predict Next Month Expense
```http
GET /api/analytics/predict-expense
Authorization: Bearer <token>
```

**Response:**
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

**Confidence Levels:**
- **High**: Low variance in past 3 months (< 15%)
- **Medium**: Moderate variance (15-30%)
- **Low**: High variance (> 30%)

---

### **Receipt Scanner (OCR)**

#### Upload Receipt
```http
POST /api/receipt/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <image_file>
```

**Response:**
```json
{
  "success": true,
  "message": "Receipt processed successfully",
  "data": {
    "id": 1,
    "imagePath": "uploads/receipts/uuid_receipt.jpg",
    "extractedAmount": 1250.50,
    "extractedDate": "2024-01-10",
    "merchantName": "ABC Store",
    "createdAt": "2024-01-10T15:30:00"
  }
}
```

**Note:** After processing, an expense transaction is automatically created.

#### Get All Receipts
```http
GET /api/receipt
Authorization: Bearer <token>
```

---

## 🔔 Auto-Triggered Notifications

### 1. Budget Exceeded
Triggered when monthly expense exceeds budget limit.
```json
{
  "message": "Monthly budget exceeded! Spent: ₹55000.00, Budget: ₹50000.00",
  "type": "ALERT"
}
```

### 2. Daily Limit Exceeded
Triggered when daily expense exceeds calculated daily limit.
```json
{
  "message": "Daily spending limit exceeded! Today: ₹2500.00, Limit: ₹2000.00",
  "type": "ALERT"
}
```

### 3. Reminder Triggered
Triggered by scheduler every 5 minutes for pending reminders.
```json
{
  "message": "Reminder: Pay Credit Card Bill - Credit card payment due",
  "type": "INFO"
}
```

---

## ⏰ Scheduled Tasks

### Reminder Checker
- **Frequency:** Every 5 minutes
- **Function:** Checks pending reminders and creates notifications
- **Implementation:** `@Scheduled(fixedRate = 300000)`

---

## 🧪 Postman Collection Examples

### Environment Variables
```json
{
  "base_url": "http://localhost:8080",
  "token": "your_jwt_token_here"
}
```

### Test Sequence
1. Login/Register → Get JWT token
2. Create Budget for current month
3. Add some transactions
4. Create reminders
5. Upload receipt
6. Check health score
7. Get expense prediction
8. View notifications

---

## 📊 Database Schema

### New Tables

#### reminders
```sql
CREATE TABLE reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(255) NOT NULL,
    reminder_date DATETIME NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### notifications
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### receipts
```sql
CREATE TABLE receipts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    extracted_amount DOUBLE,
    extracted_date DATE,
    merchant_name VARCHAR(255),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key

# Receipt Upload Directory
RECEIPT_UPLOAD_DIR=uploads/receipts
```

### application.yml
```yaml
receipt:
  upload:
    dir: uploads/receipts
```

---

## 🎯 Business Logic

### Daily Limit Calculation
```
Monthly Budget = X
Days in Month = N
Daily Limit = X / N
```

### Health Score Formula
```
Score = (SavingsRate × 40) + (BudgetDiscipline × 30) + (ExpenseStability × 30)

Where:
- SavingsRate = ((Income - Expense) / Income) × 100
- BudgetDiscipline = (1 - (Expense / Budget)) × 100
- ExpenseStability = 100 - CoefficientOfVariation
```

### Expense Prediction
```
Predicted Expense = Average of last 3 months expenses
Confidence = Based on variance (lower variance = higher confidence)
```

---

## 🚨 Error Handling

All endpoints return standardized error responses:
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

Common HTTP Status Codes:
- 200: Success
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error

---

## 📱 Swagger UI

Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Security

All endpoints require JWT authentication except:
- POST /api/auth/register
- POST /api/auth/login

Include token in header:
```
Authorization: Bearer <your_jwt_token>
```
