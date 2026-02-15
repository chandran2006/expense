# SaveUp - Advanced Features Upgrade

## New Features Added

### 1. Notification & Reminder System

#### Reminder Entity
- Create, update, delete reminders
- Auto-trigger reminders every 5 minutes using @Scheduled
- Automatic notification creation when reminder triggers

#### Notification Entity
- Three types: INFO, WARNING, ALERT
- Auto-created for:
  - Budget exceeded
  - Daily limit exceeded
  - Reminder triggered

### 2. Daily Spending Limit Alert
- Calculates daily limit: Monthly Budget / Days in Month
- Monitors today's expenses
- Creates ALERT notification when exceeded

### 3. Monthly Financial Health Score
- **Savings Rate** (40%): (Income - Expense) / Income
- **Budget Discipline** (30%): Budget adherence percentage
- **Expense Stability** (30%): Variance from last 3 months
- Returns score (0-100) and status (Excellent/Good/Fair/Poor)

### 4. Smart Expense Prediction
- Analyzes last 3 months expense data
- Calculates moving average
- Predicts next month expense
- Confidence level: High/Medium/Low based on variance

### 5. Receipt Scanner (OCR Integration)
- Upload receipt images
- Tesseract OCR extraction:
  - Total amount (currency patterns)
  - Date (multiple formats)
  - Merchant name
- Auto-creates EXPENSE transaction

---

## API Endpoints

### Reminders
```
POST   /api/reminders              - Create reminder
PUT    /api/reminders/{id}         - Update reminder
DELETE /api/reminders/{id}         - Delete reminder
GET    /api/reminders              - Get all reminders
```

### Notifications
```
GET    /api/notifications          - Get all notifications
GET    /api/notifications/unread   - Get unread notifications
GET    /api/notifications/unread/count - Get unread count
PUT    /api/notifications/{id}/read - Mark as read
PUT    /api/notifications/read-all - Mark all as read
```

### Analytics
```
GET    /api/analytics/health-score    - Get financial health score
GET    /api/analytics/predict-expense - Predict next month expense
```

### Receipt
```
POST   /api/receipt/upload         - Upload receipt (multipart/form-data)
GET    /api/receipt                - Get all receipts
```

---

## Example API Requests

### 1. Create Reminder
```json
POST /api/reminders
Authorization: Bearer {token}

{
  "title": "Pay electricity bill",
  "message": "Don't forget to pay the electricity bill",
  "reminderDate": "2024-02-20T10:00:00"
}

Response:
{
  "success": true,
  "message": "Reminder created successfully",
  "data": {
    "id": 1,
    "title": "Pay electricity bill",
    "message": "Don't forget to pay the electricity bill",
    "reminderDate": "2024-02-20T10:00:00",
    "isCompleted": false,
    "createdAt": "2024-02-15T14:30:00"
  }
}
```

### 2. Get Unread Notifications
```json
GET /api/notifications/unread
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Unread notifications retrieved successfully",
  "data": [
    {
      "id": 1,
      "message": "Budget exceeded! Spent: ₹35000.00, Limit: ₹30000.00",
      "type": "WARNING",
      "isRead": false,
      "createdAt": "2024-02-15T14:30:00"
    },
    {
      "id": 2,
      "message": "Daily spending limit exceeded! Today: ₹1500.00, Limit: ₹1000.00",
      "type": "ALERT",
      "isRead": false,
      "createdAt": "2024-02-15T18:45:00"
    }
  ]
}
```

### 3. Get Health Score
```json
GET /api/analytics/health-score
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Health score calculated successfully",
  "data": {
    "score": 78,
    "status": "Good",
    "savingsRate": 0.85,
    "budgetDiscipline": 0.90,
    "expenseStability": 0.75
  }
}
```

### 4. Predict Expense
```json
GET /api/analytics/predict-expense
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Expense prediction generated successfully",
  "data": {
    "predictedExpense": 28500.00,
    "confidenceLevel": "Medium"
  }
}
```

### 5. Upload Receipt
```
POST /api/receipt/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [receipt_image.jpg]

Response:
{
  "success": true,
  "message": "Receipt processed successfully",
  "data": {
    "id": 1,
    "imagePath": "uploads/receipts/uuid_receipt.jpg",
    "extractedAmount": 1250.50,
    "extractedDate": "2024-02-15",
    "merchantName": "ABC Store",
    "createdAt": "2024-02-15T14:30:00"
  }
}
```

---

## Configuration

### application.yml
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD:Chandran@2006}
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

jwt:
  secret: ${JWT_SECRET:your_secret}

upload:
  path: ${UPLOAD_PATH:uploads/receipts}

tesseract:
  data-path: ${TESSERACT_DATA_PATH:tessdata}
```

### Environment Variables
```bash
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
UPLOAD_PATH=uploads/receipts
TESSERACT_DATA_PATH=tessdata
```

---

## Setup Instructions

### 1. Install Tesseract OCR

**Windows:**
```bash
# Download from: https://github.com/UB-Mannheim/tesseract/wiki
# Install and add to PATH
# Download tessdata from: https://github.com/tesseract-ocr/tessdata
```

**Linux:**
```bash
sudo apt-get install tesseract-ocr
sudo apt-get install libtesseract-dev
```

**macOS:**
```bash
brew install tesseract
```

### 2. Create Upload Directory
```bash
mkdir -p uploads/receipts
```

### 3. Download Tessdata
```bash
# Download eng.traineddata from:
# https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata
# Place in tessdata/ directory
```

### 4. Update Database
```sql
-- Tables will be auto-created by Hibernate
-- New tables: reminders, notifications, receipts
```

---

## Scheduler Configuration

Reminder check runs every 5 minutes:
```java
@Scheduled(fixedRate = 300000) // 5 minutes = 300000 ms
public void checkReminders() {
    reminderService.checkAndTriggerReminders();
}
```

---

## Business Logic

### Daily Limit Calculation
```
Monthly Budget = ₹30,000
Days in Month = 30
Daily Limit = ₹30,000 / 30 = ₹1,000
```

### Health Score Formula
```
Score = (SavingsRate × 40) + (BudgetDiscipline × 30) + (ExpenseStability × 30)

Example:
- Savings Rate: 0.85 (85%)
- Budget Discipline: 0.90 (90%)
- Expense Stability: 0.75 (75%)

Score = (0.85 × 40) + (0.90 × 30) + (0.75 × 30)
      = 34 + 27 + 22.5
      = 83.5 ≈ 84 (Excellent)
```

### Expense Prediction
```
Last 3 months: ₹25,000, ₹28,000, ₹30,000
Moving Average = (25000 + 28000 + 30000) / 3 = ₹27,667
Predicted Expense = ₹27,667

Variance calculation determines confidence level
```

---

## Postman Collection

### Create Reminder
```
POST http://localhost:8080/api/reminders
Headers:
  Authorization: Bearer {token}
  Content-Type: application/json
Body:
{
  "title": "Pay rent",
  "message": "Monthly rent payment due",
  "reminderDate": "2024-03-01T09:00:00"
}
```

### Upload Receipt
```
POST http://localhost:8080/api/receipt/upload
Headers:
  Authorization: Bearer {token}
Body:
  form-data:
    file: [Select Image File]
```

### Get Health Score
```
GET http://localhost:8080/api/analytics/health-score
Headers:
  Authorization: Bearer {token}
```

---

## Testing

### 1. Test Reminders
- Create reminder for 5 minutes from now
- Wait for scheduler to run
- Check notifications endpoint

### 2. Test Daily Limit
- Set monthly budget
- Add expense exceeding daily limit
- Check notifications

### 3. Test Health Score
- Add income and expenses for current month
- Call health score endpoint
- Verify calculation

### 4. Test Prediction
- Add expenses for last 3 months
- Call prediction endpoint
- Verify moving average

### 5. Test OCR
- Upload receipt image
- Verify extracted data
- Check auto-created transaction

---

## Architecture

```
controller/
├── ReminderController
├── NotificationController
├── AnalyticsController
└── ReceiptController

service/
├── ReminderService
├── NotificationService
├── DailySpendingService
├── HealthScoreService
├── PredictionService
└── ReceiptService

repository/
├── ReminderRepository
├── NotificationRepository
└── ReceiptRepository

scheduler/
└── ReminderScheduler

config/
└── SchedulerConfig
```

---

## Production Considerations

1. **OCR Performance**: Consider async processing for large images
2. **File Storage**: Use cloud storage (S3) instead of local filesystem
3. **Scheduler**: Use distributed scheduler for multiple instances
4. **Notifications**: Add email/SMS integration
5. **Caching**: Cache health score calculations
6. **Rate Limiting**: Limit OCR requests per user

---

## Troubleshooting

### Tesseract Not Found
```
Error: Tesseract not found in PATH
Solution: Install Tesseract and add to system PATH
```

### Upload Directory Error
```
Error: Cannot create directory
Solution: Create uploads/receipts manually with write permissions
```

### Scheduler Not Running
```
Error: @Scheduled not working
Solution: Ensure @EnableScheduling is present in config
```

---

## Next Steps

1. Test all new endpoints
2. Configure Tesseract OCR
3. Set up file upload directory
4. Test reminder scheduler
5. Integrate with frontend

---

## Support

For issues:
- Check logs for detailed error messages
- Verify Tesseract installation
- Ensure database tables are created
- Check file permissions for uploads

Swagger UI: http://localhost:8080/swagger-ui.html
