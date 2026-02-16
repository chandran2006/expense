# Quick Start Guide - Backend Server

## ⚠️ IMPORTANT: Start Backend First!

The AI Chat and other features require the backend server to be running at `http://localhost:8080`

---

## 🚀 Start Backend Server

### Option 1: Using start.bat (Recommended)
```bash
cd C:\Users\ganes\OneDrive\Desktop\budget\backend
start.bat
```

### Option 2: Using Maven directly
```bash
cd C:\Users\ganes\OneDrive\Desktop\budget\backend
mvn spring-boot:run
```

### Option 3: Using IDE
1. Open backend folder in IntelliJ IDEA or Eclipse
2. Run `FinMateAIApplication.java`

---

## ✅ Verify Backend is Running

Once started, you should see:
```
Started FinMateAIApplication in X.XXX seconds
```

Test the backend:
- Open browser: http://localhost:8080/swagger-ui.html
- You should see the Swagger API documentation

---

## 📋 Prerequisites

### 1. Java 17 or Higher
Check if installed:
```bash
java -version
```

If not installed, download from: https://adoptium.net/

### 2. Maven
Check if installed:
```bash
mvn -version
```

If not installed, download from: https://maven.apache.org/download.cgi

### 3. MySQL Database
Make sure MySQL is running and database exists:
```sql
CREATE DATABASE IF NOT EXISTS finmate_ai;
```

Database credentials (from application.properties):
- URL: jdbc:mysql://localhost:3306/finmate_ai
- Username: root
- Password: Chandran@2006

---

## 🔧 Troubleshooting

### Error: "Port 8080 already in use"
Kill the process using port 8080:
```bash
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

### Error: "Cannot connect to database"
1. Start MySQL service
2. Verify database exists
3. Check credentials in `application.properties`

### Error: "Java not found"
1. Install Java 17
2. Set JAVA_HOME environment variable
3. Add Java to PATH

### Error: "Maven not found"
1. Install Maven
2. Set MAVEN_HOME environment variable
3. Add Maven to PATH

---

## 📝 Complete Startup Steps

### Step 1: Start MySQL
Make sure MySQL service is running

### Step 2: Start Backend
```bash
cd C:\Users\ganes\OneDrive\Desktop\budget\backend
start.bat
```

Wait for: "Started FinMateAIApplication"

### Step 3: Start Frontend (Already Running)
```bash
cd C:\Users\ganes\OneDrive\Desktop\budget\frontend
npm run dev
```

### Step 4: Access Application
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## 🎯 Quick Test

Once both servers are running:

1. Open http://localhost:5173
2. Login or signup
3. Go to "AI Chat" page
4. Click any quick action button
5. You should get a response!

---

## 📞 Still Having Issues?

### Check Backend Logs
Look for errors in the console where you ran `start.bat`

### Check Frontend Console
Open browser DevTools (F12) → Console tab

### Common Issues:
1. **Backend not started** → Start using start.bat
2. **Database not running** → Start MySQL service
3. **Wrong port** → Backend must be on 8080
4. **CORS errors** → Backend should handle CORS automatically

---

## 🔥 Pro Tip

Keep both terminals open:
- Terminal 1: Backend (start.bat)
- Terminal 2: Frontend (npm run dev)

This way you can see logs from both servers!

---

## ✅ Success Indicators

Backend is ready when you see:
```
Started FinMateAIApplication in X.XXX seconds (JVM running for X.XXX)
```

Frontend is ready when you see:
```
➜  Local:   http://localhost:5173/
```

AI Chat works when:
- You can send messages
- You get responses
- No error messages appear

---

**Now start the backend and enjoy your enhanced AI Assistant! 🚀**
