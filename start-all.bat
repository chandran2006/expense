@echo off
echo ========================================
echo FinMate Application Startup
echo ========================================
echo.

echo This will start both Backend and Frontend servers
echo.
echo Backend: http://localhost:8080
echo Frontend: http://localhost:5173
echo.
pause

echo.
echo [1/2] Starting Backend Server...
echo ========================================
start "FinMate Backend" cmd /k "cd backend && start.bat"

echo Waiting for backend to start (30 seconds)...
timeout /t 30 /nobreak

echo.
echo [2/2] Starting Frontend Server...
echo ========================================
start "FinMate Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo ========================================
echo Both servers are starting!
echo ========================================
echo.
echo Backend: http://localhost:8080
echo Frontend: http://localhost:5173
echo Swagger UI: http://localhost:8080/swagger-ui.html
echo.
echo Two new windows have opened:
echo - Backend Server (Spring Boot)
echo - Frontend Server (Vite)
echo.
echo Keep both windows open while using the app!
echo Close this window when done.
echo.
pause
