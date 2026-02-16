# ⚠️ BACKEND NOT RUNNING - HOW TO FIX

## The Problem
You're seeing: **"Sorry, I encountered an error. Please make sure the AI service is running at http://localhost:8080"**

This means the **Spring Boot backend server is NOT running**.

---

## ✅ SOLUTION - Start the Backend

### Method 1: Double-Click Startup Script (EASIEST)
1. Go to: `C:\Users\ganes\OneDrive\Desktop\budget`
2. Double-click: **`start-all.bat`**
3. Wait 30 seconds for backend to start
4. Refresh your browser

### Method 2: Manual Start
1. Open **NEW** Command Prompt/Terminal
2. Run these commands:
```bash
cd C:\Users\ganes\OneDrive\Desktop\budget\backend
start.bat
```
3. Wait for: "Started FinMateAIApplication"
4. Keep this window OPEN
5. Go back to your browser and try again

### Method 3: Using Maven Directly
```bash
cd C:\Users\ganes\OneDrive\Desktop\budget\backend
mvn spring-boot:run
```

---

## 🔍 How to Check if Backend is Running

### Option 1: Check in Browser
Open: http://localhost:8080/swagger-ui.html

✅ If you see Swagger UI = Backend is running
❌ If you see error = Backend is NOT running

### Option 2: Check Port
```bash
netstat -ano | findstr :8080
```
If you see output = Something is on port 8080
If no output = Port 8080 is free (backend not running)

---

## 📋 Prerequisites (Install if Missing)

### 1. Java 17+
Check:
```bash
java -version
```
Download: https://adoptium.net/

### 2. Maven
Check:
```bash
mvn -version
```
Download: https://maven.apache.org/download.cgi

### 3. MySQL
- MySQL must be running
- Database: `finmate_ai` must exist
- Username: `root`
- Password: `Chandran@2006`

Create database:
```sql
CREATE DATABASE IF NOT EXISTS finmate_ai;
```

---

## 🎯 Complete Startup Process

### Step 1: Start MySQL
Make sure MySQL service is running

### Step 2: Start Backend (Choose one method above)
Wait for: "Started FinMateAIApplication in X.XXX seconds"

### Step 3: Verify Backend
Open: http://localhost:8080/swagger-ui.html

### Step 4: Use Frontend
Your frontend is already running at: http://localhost:5173
Now try AI Chat again!

---

## 🐛 Troubleshooting

### "Port 8080 already in use"
```bash
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

### "Cannot connect to database"
1. Start MySQL service
2. Check database exists: `SHOW DATABASES;`
3. Verify credentials in `backend/src/main/resources/application.properties`

### "Java not found"
1. Install Java 17 from https://adoptium.net/
2. Set JAVA_HOME environment variable
3. Add to PATH

### "Maven not found"
1. Install Maven from https://maven.apache.org/
2. Set MAVEN_HOME environment variable
3. Add to PATH

---

## 📝 Quick Commands Reference

### Start Backend
```bash
cd backend
start.bat
```

### Check Backend Status
```bash
curl http://localhost:8080/swagger-ui.html
```

### View Backend Logs
Look at the terminal where you ran `start.bat`

### Stop Backend
Press `Ctrl+C` in the backend terminal

---

## ✅ Success Checklist

- [ ] MySQL is running
- [ ] Backend started successfully
- [ ] Can access http://localhost:8080/swagger-ui.html
- [ ] Frontend is running at http://localhost:5173
- [ ] Can login to application
- [ ] AI Chat responds without errors

---

## 🎉 Once Backend is Running

The AI Chat will work and you can:
- ✅ Ask about financial health score
- ✅ Check spending and balance
- ✅ Get saving tips
- ✅ Compare months
- ✅ View top categories
- ✅ Get personalized recommendations

---

## 📞 Still Not Working?

1. Check backend terminal for error messages
2. Check browser console (F12) for errors
3. Verify MySQL is running
4. Verify database credentials
5. Try restarting both backend and frontend

---

**Remember: Keep the backend terminal window OPEN while using the application!**
