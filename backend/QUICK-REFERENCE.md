# SaveUp - Quick Reference Card

## 🚀 New Endpoints

### Reminders
```
POST   /api/reminders              Create reminder
PUT    /api/reminders/{id}         Update reminder
DELETE /api/reminders/{id}         Delete reminder
GET    /api/reminders              Get all reminders
```

### Notifications
```
GET    /api/notifications                Get all
GET    /api/notifications/unread         Get unread
GET    /api/notifications/unread/count   Get count
PUT    /api/notifications/{id}/read      Mark as read
PUT    /api/notifications/read-all       Mark all read
```

### Analytics
```
GET    /api/analytics/health-score       Financial health
GET    /api/analytics/predict-expense    Expense prediction
```

### Receipt
```
POST   /api/receipt/upload               Upload receipt
GET    /api/receipt                      Get all receipts
```

---

## 📋 Request Examples

### Create Reminder
```json
POST /api/reminders
{
  "title": "Pay rent",
  "message": "Monthly rent due",
  "reminderDate": "2024-03-01T09:00:00"
}
```

### Upload Receipt
```
POST /api/receipt/upload
Content-Type: multipart/form-data
file: [image file]
```

---

## 📊 Response Examples

### Health Score
```json
{
  "score": 78,
  "status": "Good",
  "savingsRate": 0.85,
  "budgetDiscipline": 0.90,
  "expenseStability": 0.75
}
```

### Prediction
```json
{
  "predictedExpense": 28500.00,
  "confidenceLevel": "Medium"
}
```

### Notification
```json
{
  "id": 1,
  "message": "Budget exceeded! Spent: ₹35000, Limit: ₹30000",
  "type": "WARNING",
  "isRead": false,
  "createdAt": "2024-02-15T14:30:00"
}
```

---

## ⚙️ Configuration

### Environment Variables
```bash
DB_PASSWORD=your_password
JWT_SECRET=your_secret
UPLOAD_PATH=uploads/receipts
TESSERACT_DATA_PATH=tessdata
```

### application.yml
```yaml
upload:
  path: uploads/receipts

tesseract:
  data-path: tessdata
```

---

## 🔧 Setup Steps

1. **Install Tesseract**
   ```bash
   # Windows: Download installer
   # Linux: sudo apt-get install tesseract-ocr
   # macOS: brew install tesseract
   ```

2. **Create Directories**
   ```bash
   mkdir uploads/receipts
   mkdir tessdata
   ```

3. **Download Tessdata**
   - Get eng.traineddata
   - Place in tessdata/

4. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

---

## 🎯 Features

### Auto Notifications
- Budget exceeded → WARNING
- Daily limit exceeded → ALERT
- Reminder triggered → INFO

### Scheduler
- Runs every 5 minutes
- Checks pending reminders
- Auto-creates notifications

### OCR Extraction
- Amount (₹1234.56)
- Date (DD/MM/YYYY)
- Merchant name
- Auto-creates transaction

---

## 📈 Formulas

### Daily Limit
```
Daily Limit = Monthly Budget / Days in Month
```

### Health Score
```
Score = (SavingsRate × 40) + 
        (BudgetDiscipline × 30) + 
        (ExpenseStability × 30)
```

### Prediction
```
Predicted = Average(Last 3 Months)
```

---

## 🧪 Testing

### Test Reminder
1. Create reminder for 5 min from now
2. Wait for scheduler
3. Check notifications

### Test Daily Limit
1. Set monthly budget
2. Add expense > daily limit
3. Check notifications

### Test OCR
1. Upload receipt image
2. Verify extracted data
3. Check auto-created transaction

---

## 📚 Documentation

- **UPGRADE-GUIDE.md** - Complete guide
- **UPGRADE-SUMMARY.md** - Feature summary
- **Swagger UI** - http://localhost:8080/swagger-ui.html

---

## ✅ Checklist

- [ ] Tesseract installed
- [ ] Upload directory created
- [ ] Tessdata downloaded
- [ ] Application running
- [ ] Test reminder creation
- [ ] Test notification system
- [ ] Test health score
- [ ] Test prediction
- [ ] Upload receipt
- [ ] Verify OCR extraction

---

## 🆘 Troubleshooting

**Tesseract not found**
- Install Tesseract
- Add to system PATH

**Upload fails**
- Create uploads/receipts directory
- Check write permissions

**Scheduler not running**
- Verify @EnableScheduling
- Check logs

**OCR extraction fails**
- Verify tessdata path
- Check image quality
- Ensure eng.traineddata exists

---

## 🎉 Success!

All advanced features are now ready to use!

Test at: http://localhost:8080/swagger-ui.html
