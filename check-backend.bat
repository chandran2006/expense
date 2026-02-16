@echo off
echo ========================================
echo FinMate Backend Status Check
echo ========================================
echo.

echo Checking if backend is running on port 8080...
netstat -ano | findstr :8080 > nul
if %errorlevel% equ 0 (
    echo [OK] Backend is running on port 8080
    echo.
    echo Testing backend API...
    curl -s http://localhost:8080/swagger-ui.html > nul 2>&1
    if %errorlevel% equ 0 (
        echo [OK] Backend API is responding
        echo.
        echo Backend URL: http://localhost:8080
        echo Swagger UI: http://localhost:8080/swagger-ui.html
    ) else (
        echo [WARNING] Port 8080 is in use but API not responding
    )
) else (
    echo [ERROR] Backend is NOT running!
    echo.
    echo To start the backend:
    echo 1. Open a new terminal
    echo 2. cd backend
    echo 3. Run: start.bat
    echo.
    echo Or run this command:
    echo cd backend ^&^& start.bat
)

echo.
echo ========================================
pause
