@echo off
echo ========================================
echo FinMate AI Backend - Startup Script
echo ========================================
echo.

echo Checking Java installation...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java not found! Please install Java 17 or higher.
    pause
    exit /b 1
)
echo.

echo Checking Maven installation...
call mvn -version
if %errorlevel% neq 0 (
    echo ERROR: Maven not found! Please install Maven.
    pause
    exit /b 1
)
echo.

echo Creating uploads directory...
if not exist "uploads\receipts" mkdir uploads\receipts
echo.

echo Building project...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

echo ========================================
echo Starting FinMate AI Backend...
echo ========================================
echo Server will start on: http://localhost:8080
echo Swagger UI: http://localhost:8080/swagger-ui.html
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

call mvn spring-boot:run
