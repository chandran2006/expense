# Frontend-Backend Connection Analysis

## 🔴 CRITICAL ISSUES FOUND

### 1. **FRONTEND IS NOT CONNECTED TO BACKEND**
The frontend is using **Supabase** for authentication, while the backend has a **Spring Boot REST API** with JWT authentication. They are completely disconnected!

---

## Current Architecture

### Frontend (React + TypeScript)
- **Port**: 5173 (Vite default)
- **Authentication**: Supabase (Cloud service)
- **Database**: Supabase PostgreSQL
- **Files**:
  - `AuthContext.tsx` - Uses Supabase client
  - `supabase.ts` - Supabase configuration
  - `Login.tsx` & `Signup.tsx` - Call Supabase methods

### Backend (Spring Boot + Java)
- **Port**: 8080
- **Authentication**: JWT tokens with Spring Security
- **Database**: MySQL (localhost:3306/finmate_ai)
- **Endpoints**:
  - `POST /api/auth/register` - Register new user
  - `POST /api/auth/login` - Login user
- **Files**:
  - `AuthController.java` - REST endpoints
  - `AuthService.java` - Business logic
  - `SecurityConfig.java` - CORS & Security config

---

## Problems Identified

### ❌ Problem 1: Frontend Uses Supabase, Not Backend API
**Location**: `frontend/src/context/AuthContext.tsx`

```typescript
// Current code uses Supabase
const { data, error } = await supabase.auth.signUp({
  email,
  password,
});
```

**Should be**: HTTP calls to `http://localhost:8080/api/auth/register`

### ❌ Problem 2: No API Service Layer
The frontend lacks an API service to communicate with the Spring Boot backend.

### ❌ Problem 3: Different Data Models
- **Supabase**: Uses `id`, `email`, `name`
- **Backend**: Uses `User` entity with additional fields like `role`

### ❌ Problem 4: Token Storage
- Frontend expects Supabase session tokens
- Backend generates JWT tokens that need to be stored and sent with requests

---

## What Needs to Be Fixed

### 1. Create API Service (`frontend/src/services/api.ts`)
```typescript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  register: (name: string, email: string, password: string) =>
    api.post('/auth/register', { name, email, password }),
  
  login: (email: string, password: string) =>
    api.post('/auth/login', { email, password }),
};

export default api;
```

### 2. Update AuthContext to Use Backend API
Replace Supabase calls with axios calls to Spring Boot backend.

### 3. Update Environment Variables
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### 4. Remove Supabase Dependency (Optional)
If you want to use only the Spring Boot backend, remove:
- `@supabase/supabase-js` package
- `supabase.ts` file
- Supabase environment variables

---

## Backend Status

### ✅ Backend is Ready
- CORS configured for `http://localhost:5173`
- JWT authentication implemented
- Register & Login endpoints working
- MySQL database configured

### ⚠️ Backend Needs
- Ensure MySQL is running
- Database `finmate_ai` must exist
- Run: `mvn spring-boot:run` or use `start.bat`

---

## Frontend Status

### ❌ Frontend Needs Complete Rewrite of Auth
- Replace Supabase with axios/fetch calls
- Update AuthContext
- Add token management
- Update all API calls to use backend

---

## Recommended Solution

### Option 1: Use Backend Only (Recommended)
1. Remove Supabase integration
2. Create API service layer
3. Update AuthContext to use backend
4. Store JWT tokens in localStorage
5. Add Authorization headers to all requests

### Option 2: Use Supabase Only
1. Remove Spring Boot backend
2. Keep current frontend as-is
3. Use Supabase for all features

### Option 3: Hybrid (Not Recommended)
- Use Supabase for auth
- Use Spring Boot for business logic
- Requires syncing users between systems

---

## Next Steps

1. **Decide**: Backend or Supabase?
2. **If Backend**: Implement API service layer
3. **Update**: AuthContext to use chosen solution
4. **Test**: Login and Signup flows
5. **Update**: All other API calls (transactions, budget, etc.)

---

## Files That Need Changes

### Frontend Files to Modify:
1. `src/context/AuthContext.tsx` - Replace Supabase with API calls
2. `src/services/supabase.ts` - Delete or replace with api.ts
3. `src/pages/Login.tsx` - Update error handling
4. `src/pages/Signup.tsx` - Update error handling
5. `.env` - Update environment variables

### Backend Files (Already Good):
- ✅ AuthController.java
- ✅ AuthService.java
- ✅ SecurityConfig.java
- ✅ JWT implementation

---

## Testing Checklist

After fixing:
- [ ] Backend starts on port 8080
- [ ] Frontend starts on port 5173
- [ ] Signup creates user in MySQL database
- [ ] Login returns JWT token
- [ ] Token is stored in localStorage
- [ ] Protected routes check for token
- [ ] Logout clears token
- [ ] API calls include Authorization header

---

## Summary

**Current State**: Frontend and backend are NOT connected. Frontend uses Supabase cloud service while backend is a standalone Spring Boot API.

**Required Action**: Choose one authentication system and update the frontend to match.
