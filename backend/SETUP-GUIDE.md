# SaveUp - Setup Guide for Advanced Features

## 📦 Prerequisites

- Java 17
- Maven 3.8+
- MySQL 8.0+
- Tesseract OCR (for receipt scanning)

---

## 🔧 Installation Steps

### 1. Install Tesseract OCR

#### Windows:
```bash
# Download installer from: https://github.com/UB-Mannheim/tesseract/wiki
# Install to: C:\Program Files\Tesseract-OCR
# Add to PATH environment variable
```

#### Linux:
```bash
sudo apt-get update
sudo apt-get install tesseract-ocr
sudo apt-get install libtesseract-dev
```

#### macOS:
```bash
brew install tesseract
```

### 2. Configure Tesseract (Optional)
If Tesseract is not in PATH, configure in ReceiptService:
```java
tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
```

### 3. Database Setup

```sql
CREATE DATABASE finmate_ai;
USE finmate_ai;

-- Tables will be auto-created by Hibernate
-- Or run the migration script
```

### 4. Environment Variables

Create `.env` file or set system environment variables:

```bash
# Database
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_jwt_secret_key_min_256_bits

# Receipt Upload
RECEIPT_UPLOAD_DIR=uploads/receipts
```

### 5. Application Configuration

Update `application.yml`:
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD:default_password}

jwt:
  secret: ${JWT_SECRET:default_secret}

receipt:
  upload:
    dir: ${RECEIPT_UPLOAD_DIR:uploads/receipts}
```

### 6. Build and Run

```bash
# Clean and build
mvn clean install

# Run application
mvn spring-boot:run

# Or run JAR
java -jar target/saveup-1.0.0.jar
```

---

## 🗂️ Project Structure

```
src/main/java/com/finmate/ai/
├── aspect/
│   └── LoggingAspect.java
├── config/
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   └── SchedulerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── TransactionController.java
│   ├── BudgetController.java
│   ├── ReminderController.java          ✨ NEW
│   ├── NotificationController.java      ✨ NEW
│   ├── ReceiptController.java           ✨ NEW
│   └── AnalyticsController.java         ✨ NEW
├── dto/
│   ├── ApiResponse.java
│   ├── ReminderRequest.java             ✨ NEW
│   ├── ReminderDTO.java                 ✨ NEW
│   ├── NotificationDTO.java             ✨ NEW
│   ├── ReceiptDTO.java                  ✨ NEW
│   ├── HealthScoreResponse.java         ✨ NEW
│   └── PredictionResponse.java          ✨ NEW
├── entity/
│   ├── User.java
│   ├── Transaction.java
│   ├── Budget.java
│   ├── Reminder.java                    ✨ NEW
│   ├── Notification.java                ✨ NEW
│   ├── Receipt.java                     ✨ NEW
│   └── NotificationType.java            ✨ NEW
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
├── repository/
│   ├── UserRepository.java
│   ├── TransactionRepository.java
│   ├── BudgetRepository.java
│   ├── ReminderRepository.java          ✨ NEW
│   ├── NotificationRepository.java      ✨ NEW
│   └── ReceiptRepository.java           ✨ NEW
├── scheduler/
│   └── ReminderScheduler.java           ✨ NEW
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── TransactionService.java
│   ├── BudgetService.java
│   ├── ReminderService.java             ✨ NEW
│   ├── NotificationService.java         ✨ NEW
│   ├── DailySpendingService.java        ✨ NEW
│   ├── HealthScoreService.java          ✨ NEW
│   ├── PredictionService.java           ✨ NEW
│   └── ReceiptService.java              ✨ NEW
├── util/
│   └── JwtUtil.java
└── SaveUpApplication.java
```

---

## 🧪 Testing

### 1. Test Reminder System
```bash
# Create reminder
curl -X POST http://localhost:8080/api/reminders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Reminder",
    "message": "This is a test",
    "reminderDate": "2024-01-15T10:00:00"
  }'

# Wait for scheduler (runs every 5 minutes)
# Check notifications
curl -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. Test Daily Spending Alert
```bash
# Set monthly budget
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "month": "2024-01",
    "limitAmount": 30000
  }'

# Add expense exceeding daily limit
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EXPENSE",
    "category": "Food",
    "amount": 2000,
    "description": "Test",
    "date": "2024-01-15"
  }'

# Check notifications
curl -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Test Health Score
```bash
curl -X GET http://localhost:8080/api/analytics/health-score \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Test Expense Prediction
```bash
curl -X GET http://localhost:8080/api/analytics/predict-expense \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. Test Receipt OCR
```bash
curl -X POST http://localhost:8080/api/receipt/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/receipt.jpg"
```

---

## 📊 Database Verification

```sql
-- Check new tables
SHOW TABLES;

-- Verify reminders
SELECT * FROM reminders;

-- Verify notifications
SELECT * FROM notifications;

-- Verify receipts
SELECT * FROM receipts;
```

---

## 🔍 Troubleshooting

### Issue: Tesseract OCR not found
**Solution:**
```java
// In ReceiptService.java
tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
```

### Issue: File upload fails
**Solution:**
- Check `application.yml` multipart configuration
- Verify upload directory exists and has write permissions
- Check file size limits

### Issue: Scheduler not running
**Solution:**
- Verify `@EnableScheduling` in SchedulerConfig
- Check application logs for scheduler execution
- Ensure no exceptions in ReminderScheduler

### Issue: Notifications not created
**Solution:**
- Check TransactionService has DailySpendingService injected
- Verify budget exists for current month
- Check application logs for errors

---

## 🚀 Production Deployment

### 1. Environment Variables
```bash
export DB_PASSWORD=production_password
export JWT_SECRET=production_secret_key
export RECEIPT_UPLOAD_DIR=/var/app/uploads/receipts
```

### 2. Build Production JAR
```bash
mvn clean package -DskipTests
```

### 3. Run with Production Profile
```bash
java -jar -Dspring.profiles.active=prod target/saveup-1.0.0.jar
```

### 4. Nginx Configuration (Optional)
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /uploads/ {
        alias /var/app/uploads/;
    }
}
```

---

## 📈 Performance Optimization

### 1. Database Indexing
```sql
CREATE INDEX idx_reminder_date ON reminders(reminder_date, is_completed);
CREATE INDEX idx_notification_user ON notifications(user_id, is_read);
CREATE INDEX idx_receipt_user ON receipts(user_id);
```

### 2. Caching (Optional)
Add Redis for caching health scores and predictions:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 3. Async Processing
For OCR processing, consider async execution:
```java
@Async
public CompletableFuture<ReceiptDTO> uploadAndProcessReceipt(...)
```

---

## 📝 API Documentation

Access Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Security Checklist

- ✅ JWT authentication on all endpoints
- ✅ Password encryption (BCrypt)
- ✅ SQL injection prevention (JPA)
- ✅ XSS protection (Spring Security)
- ✅ CSRF protection
- ✅ File upload validation
- ✅ Environment variable for sensitive data

---

## 📞 Support

For issues or questions:
- Check logs: `logs/application.log`
- Review API documentation
- Test with Postman collection
- Verify database schema

---

## ✅ Feature Checklist

- ✅ Reminder CRUD operations
- ✅ Notification system
- ✅ Auto-trigger reminders (scheduler)
- ✅ Daily spending limit alerts
- ✅ Monthly budget alerts
- ✅ Financial health score calculation
- ✅ Expense prediction (moving average)
- ✅ Receipt OCR integration
- ✅ Auto-create expense from receipt
- ✅ JWT authentication
- ✅ Swagger documentation
- ✅ AOP logging
- ✅ Global exception handling
- ✅ Environment variable support
- ✅ Production-ready configuration

---

## 🎯 Next Steps

1. Test all endpoints with Postman
2. Verify scheduler execution
3. Test OCR with sample receipts
4. Monitor application logs
5. Set up production environment
6. Configure backup strategy
7. Implement monitoring (optional)
8. Add unit tests (optional)

**Your SaveUp application is now startup-ready! 🚀**
