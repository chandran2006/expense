# FIX: Database 'finmate_ai' doesn't exist

## Problem
Backend fails to start with error: **Unknown database 'finmate_ai'**

## Solution - Create the Database

### Option 1: Using MySQL Command Line
```bash
mysql -u root -p
# Enter password: Chandran@2006
CREATE DATABASE finmate_ai;
exit;
```

### Option 2: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Run this query:
```sql
CREATE DATABASE finmate_ai;
```

### Option 3: Run the batch file
Double-click: `create-db.bat`

### Option 4: Run SQL script
```bash
mysql -u root -p < create-database.sql
```

## Then Start Backend
```bash
cd backend
start.bat
```

## Verify Database Created
```sql
SHOW DATABASES;
USE finmate_ai;
SHOW TABLES;
```

Tables will be auto-created by Hibernate when backend starts.
