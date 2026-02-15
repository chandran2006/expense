# Maven Installation Required

## Current Status
- ✅ Java 17 is installed
- ❌ Maven is NOT installed

## Install Maven

### Option 1: Using Chocolatey (Recommended)
```bash
# Install Chocolatey first (if not installed)
# Run PowerShell as Administrator:
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven
```

### Option 2: Manual Installation
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to: `C:\Program Files\Maven`
3. Add to PATH:
   - Open System Properties → Environment Variables
   - Add to Path: `C:\Program Files\Maven\bin`
4. Verify: Open new CMD and run `mvn -version`

### Option 3: Use IDE
- **IntelliJ IDEA**: Open project, it will auto-download Maven
- **Eclipse**: Install m2e plugin
- **VS Code**: Install Java Extension Pack

## After Maven Installation

Run the backend:
```bash
cd backend
mvn spring-boot:run
```

Or use the startup script:
```bash
start.bat
```

## Alternative: Use IDE to Run

1. Open `backend` folder in IntelliJ IDEA / Eclipse / VS Code
2. Wait for dependencies to download
3. Run `SaveUpApplication.java`
4. Access: http://localhost:8080/swagger-ui.html
