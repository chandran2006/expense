@echo off
echo ========================================
echo Creating MySQL Database
echo ========================================
echo.
echo Run this command in MySQL:
echo.
echo mysql -u root -p
echo CREATE DATABASE IF NOT EXISTS finmate_ai;
echo.
echo Or run:
echo mysql -u root -pChandran@2006 -e "CREATE DATABASE IF NOT EXISTS finmate_ai;"
echo.
echo ========================================
pause

mysql -u root -pChandran@2006 -e "CREATE DATABASE IF NOT EXISTS finmate_ai;"
if %errorlevel% equ 0 (
    echo.
    echo SUCCESS! Database created.
    echo Now run: mvn spring-boot:run
) else (
    echo.
    echo ERROR! Please create database manually.
)
pause
