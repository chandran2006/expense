@echo off
echo Creating FinMate AI Database...
echo.
mysql -u root -pChandran@2006 -e "CREATE DATABASE IF NOT EXISTS finmate_ai;"
if %errorlevel% equ 0 (
    echo SUCCESS: Database 'finmate_ai' created!
) else (
    echo ERROR: Failed to create database. Make sure MySQL is running.
)
echo.
pause
