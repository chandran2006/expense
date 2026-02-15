# FinMate AI - Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Start Frontend
```bash
cd frontend
npm run dev
```
Or double-click `start.bat`

**Frontend will run at:** http://localhost:5173

### Step 2: Start Backend (Optional - for AI Chat)
```bash
cd backend
mvn spring-boot:run
```

**Backend will run at:** http://localhost:8080

### Step 3: Open Browser
Navigate to: **http://localhost:5173**

---

## 📋 What's Changed

### ✅ Backend
- **Application Name**: SaveUp → **FinMate AI**
- **Main Class**: SaveUpApplication → **FinMateAIApplication**
- **Files Updated**:
  - `FinMateAIApplication.java` (renamed)
  - `pom.xml`
  - `application.yml`
  - `README.md`

### ✅ Frontend
- **App.tsx**: Complete routing and authentication setup
- **start.bat**: Quick start script
- **README.md**: Comprehensive documentation

---

## 🎯 Features Ready to Use

### Without Backend (Supabase Only)
✅ User Registration & Login
✅ Dashboard with Charts
✅ Transaction Management
✅ Budget Tracking
✅ Profile Management
✅ Multi-language (EN, TA, HI)
✅ Dark/Light Theme

### With Backend Running
✅ All above features
✅ AI Chat Assistant

---

## 🔑 Test Credentials

Create a new account or use:
- **Email**: test@finmate.ai
- **Password**: test123

---

## 📱 Application Pages

| Page | URL | Description |
|------|-----|-------------|
| Login | `/login` | User authentication |
| Signup | `/signup` | New user registration |
| Dashboard | `/dashboard` | Financial overview |
| Transactions | `/transactions` | Manage income/expenses |
| Budget | `/budget` | Set and track budgets |
| AI Chat | `/ai-chat` | Financial assistant |
| Profile | `/profile` | User settings |

---

## 🛠️ Tech Stack

### Frontend
- React 18 + TypeScript
- Vite (Build tool)
- Tailwind CSS
- React Router v7
- Supabase (Auth + DB)
- Recharts (Charts)
- i18next (i18n)

### Backend
- Java 17
- Spring Boot 3.2.0
- MySQL 8.0
- JWT Authentication
- Swagger/OpenAPI

---

## 📊 Database

### Supabase (Frontend)
- **URL**: https://qfiqdjsxudeygcfgwkju.supabase.co
- **Tables**: users, transactions, budgets

### MySQL (Backend - Optional)
- **Database**: finmate_ai
- **Port**: 3306
- **User**: root

---

## 🎨 UI Features

### Responsive Design
- Mobile-friendly
- Tablet optimized
- Desktop layout

### Themes
- Light mode (default)
- Dark mode
- Auto-save preference

### Languages
- English (EN)
- Tamil (தமிழ்)
- Hindi (हिन्दी)

---

## 🐛 Troubleshooting

### Frontend won't start
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Port already in use
- Frontend: Vite will auto-select next port
- Backend: Change port in `application.yml`

### Supabase connection error
- Check `.env` file exists
- Verify credentials are correct
- Check internet connection

### Backend connection error (AI Chat)
- Ensure backend is running on port 8080
- Check MySQL database is running
- Verify `application.yml` configuration

---

## 📚 Documentation

- **Frontend**: `frontend/README.md`
- **Backend**: `backend/README.md`
- **Setup Summary**: `SETUP-SUMMARY.md`

---

## 🎉 You're All Set!

The FinMate AI application is ready to run. Start the frontend and begin managing your finances with AI-powered insights!

**Happy Coding! 💰📊**
