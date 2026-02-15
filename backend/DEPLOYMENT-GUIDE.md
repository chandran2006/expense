# SaveUp - Deployment Guide

## Production Deployment Checklist

This guide helps you deploy SaveUp to production environments.

---

## Pre-Deployment Checklist

### 1. Security Configuration

#### Change JWT Secret
```properties
# Generate a strong random secret (256-bit)
jwt.secret=YOUR_STRONG_RANDOM_SECRET_HERE
```

Generate using:
```bash
# Using OpenSSL
openssl rand -base64 64

# Using Java
java -cp . -c "System.out.println(java.util.Base64.getEncoder().encodeToString(new java.security.SecureRandom().generateSeed(64)));"
```

#### Update CORS Origins
```java
// SecurityConfig.java
configuration.setAllowedOrigins(Arrays.asList(
    "https://yourdomain.com",
    "https://www.yourdomain.com"
));
```

#### Change Database Credentials
```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### 2. Database Configuration

#### Change DDL Auto
```properties
# For production, use validate or none
spring.jpa.hibernate.ddl-auto=validate
```

#### Disable SQL Logging
```properties
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

#### Configure Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 3. Logging Configuration

```properties
# File-based logging
logging.file.name=logs/SaveUp.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.level.root=INFO
logging.level.com.finmate.ai=INFO
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 4. Application Properties

```properties
# Production profile
spring.profiles.active=prod

# Server configuration
server.port=8080
server.compression.enabled=true
server.http2.enabled=true

# Error handling
server.error.include-message=never
server.error.include-binding-errors=never
server.error.include-stacktrace=never
server.error.include-exception=false
```

---

## Deployment Options

### Option 1: Traditional Server (Linux)

#### Step 1: Prepare Server
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Verify installation
java -version

# Install MySQL
sudo apt install mysql-server -y
sudo mysql_secure_installation
```

#### Step 2: Setup Database
```bash
# Login to MySQL
sudo mysql -u root -p

# Create database and user
CREATE DATABASE finmate_ai;
CREATE USER 'finmateuser'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON finmate_ai.* TO 'finmateuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### Step 3: Build Application
```bash
# On your local machine
cd backend
mvn clean package -DskipTests

# This creates: target/saveup-1.0.0.jar
```

#### Step 4: Deploy to Server
```bash
# Create application directory
sudo mkdir -p /opt/SaveUp
sudo chown $USER:$USER /opt/SaveUp

# Upload JAR file
scp target/saveup-1.0.0.jar user@server:/opt/SaveUp/

# Upload application.properties
scp src/main/resources/application.properties user@server:/opt/SaveUp/
```

#### Step 5: Create Systemd Service
```bash
# Create service file
sudo nano /etc/systemd/system/SaveUp.service
```

```ini
[Unit]
Description=SaveUp Spring Boot Application
After=syslog.target network.target

[Service]
User=SaveUp
Group=SaveUp
Type=simple
WorkingDirectory=/opt/SaveUp
ExecStart=/usr/bin/java -jar /opt/SaveUp/saveup-1.0.0.jar --spring.config.location=/opt/SaveUp/application.properties
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=SaveUp

[Install]
WantedBy=multi-user.target
```

#### Step 6: Start Service
```bash
# Create user
sudo useradd -r -s /bin/false SaveUp
sudo chown -R SaveUp:SaveUp /opt/SaveUp

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable SaveUp
sudo systemctl start SaveUp

# Check status
sudo systemctl status SaveUp

# View logs
sudo journalctl -u SaveUp -f
```

#### Step 7: Setup Nginx Reverse Proxy
```bash
# Install Nginx
sudo apt install nginx -y

# Create configuration
sudo nano /etc/nginx/sites-available/SaveUp
```

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/SaveUp /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### Step 8: Setup SSL with Let's Encrypt
```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Get certificate
sudo certbot --nginx -d api.yourdomain.com

# Auto-renewal is configured automatically
```

---

### Option 2: Docker Deployment

#### Step 1: Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/saveup-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Step 2: Create docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: SaveUp-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: finmate_ai
      MYSQL_USER: finmateuser
      MYSQL_PASSWORD: ${DB_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - SaveUp-network

  app:
    build: .
    container_name: SaveUp-app
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/finmate_ai
      SPRING_DATASOURCE_USERNAME: finmateuser
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    networks:
      - SaveUp-network

volumes:
  mysql-data:

networks:
  SaveUp-network:
    driver: bridge
```

#### Step 3: Create .env file
```bash
DB_ROOT_PASSWORD=your_root_password
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
```

#### Step 4: Deploy
```bash
# Build and start
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

---

### Option 3: AWS Deployment

#### Using AWS Elastic Beanstalk

1. **Install EB CLI**
```bash
pip install awsebcli
```

2. **Initialize EB**
```bash
cd backend
eb init -p java-17 SaveUp
```

3. **Create Environment**
```bash
eb create SaveUp-prod
```

4. **Deploy**
```bash
mvn clean package
eb deploy
```

5. **Configure RDS**
- Create MySQL RDS instance
- Update environment variables
- Configure security groups

#### Using AWS ECS (Fargate)

1. **Build and push Docker image**
```bash
# Build image
docker build -t SaveUp .

# Tag for ECR
docker tag SaveUp:latest 123456789.dkr.ecr.region.amazonaws.com/SaveUp:latest

# Push to ECR
docker push 123456789.dkr.ecr.region.amazonaws.com/SaveUp:latest
```

2. **Create ECS Task Definition**
3. **Create ECS Service**
4. **Configure Load Balancer**
5. **Setup RDS for MySQL**

---

### Option 4: Heroku Deployment

#### Step 1: Prepare Application
```bash
# Create Procfile
echo "web: java -jar target/saveup-1.0.0.jar" > Procfile
```

#### Step 2: Deploy
```bash
# Login to Heroku
heroku login

# Create app
heroku create SaveUp

# Add MySQL addon
heroku addons:create jawsdb:kitefin

# Get database URL
heroku config:get JAWSDB_URL

# Update application.properties to use DATABASE_URL
# Deploy
git push heroku main
```

---

## Environment Variables

### Required Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=finmate_ai
DB_USERNAME=finmateuser
DB_PASSWORD=strong_password

# JWT
JWT_SECRET=your_strong_secret
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# CORS
ALLOWED_ORIGINS=https://yourdomain.com
```

### Setting Environment Variables

**Linux:**
```bash
export DB_PASSWORD="your_password"
export JWT_SECRET="your_secret"
```

**Windows:**
```cmd
set DB_PASSWORD=your_password
set JWT_SECRET=your_secret
```

**Docker:**
```yaml
environment:
  - DB_PASSWORD=${DB_PASSWORD}
  - JWT_SECRET=${JWT_SECRET}
```

---

## Monitoring & Maintenance

### 1. Health Checks

Add Spring Boot Actuator:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configure:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### 2. Application Monitoring

**Using Prometheus + Grafana:**
```properties
management.metrics.export.prometheus.enabled=true
```

**Using New Relic:**
- Add New Relic Java agent
- Configure license key

### 3. Log Aggregation

**Using ELK Stack:**
- Configure Logstash
- Send logs to Elasticsearch
- Visualize in Kibana

**Using CloudWatch (AWS):**
- Install CloudWatch agent
- Configure log groups

### 4. Database Backups

**Automated MySQL Backups:**
```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u finmateuser -p finmate_ai > /backups/finmate_ai_$DATE.sql
find /backups -name "*.sql" -mtime +7 -delete
```

**Cron Job:**
```bash
# Daily backup at 2 AM
0 2 * * * /opt/scripts/backup.sh
```

---

## Performance Optimization

### 1. Database Indexing
```sql
-- Already indexed in schema
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_user_date ON transactions(user_id, date);
CREATE INDEX idx_user_type ON transactions(user_id, type);
CREATE INDEX idx_user_month ON budgets(user_id, month);
```

### 2. Caching
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 3. Connection Pooling
Already configured with HikariCP (default in Spring Boot)

---

## Security Hardening

### 1. HTTPS Only
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

### 2. Rate Limiting
Add Bucket4j dependency for rate limiting

### 3. Security Headers
```java
http.headers()
    .contentSecurityPolicy("default-src 'self'")
    .and()
    .xssProtection()
    .and()
    .frameOptions().deny();
```

---

## Troubleshooting

### Common Issues

**1. Application won't start**
- Check Java version: `java -version`
- Check logs: `journalctl -u SaveUp -n 100`
- Verify database connection

**2. Database connection failed**
- Check MySQL status: `systemctl status mysql`
- Verify credentials
- Check firewall rules

**3. High memory usage**
- Adjust JVM options: `-Xmx512m -Xms256m`
- Monitor with: `jstat -gc <pid>`

**4. Slow performance**
- Check database queries
- Enable query logging
- Review indexes

---

## Rollback Procedure

### Quick Rollback
```bash
# Stop current version
sudo systemctl stop SaveUp

# Restore previous JAR
sudo cp /opt/SaveUp/backup/saveup-1.0.0.jar /opt/SaveUp/

# Start service
sudo systemctl start SaveUp
```

### Database Rollback
```bash
# Restore from backup
mysql -u finmateuser -p finmate_ai < /backups/finmate_ai_20240115.sql
```

---

## Post-Deployment Verification

### 1. Health Check
```bash
curl http://localhost:8080/actuator/health
```

### 2. API Test
```bash
# Test registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"test123"}'
```

### 3. Load Test
```bash
# Using Apache Bench
ab -n 1000 -c 10 http://localhost:8080/api/auth/login
```

---

## Support & Maintenance

### Regular Tasks
- [ ] Daily: Check application logs
- [ ] Daily: Verify database backups
- [ ] Weekly: Review error logs
- [ ] Weekly: Check disk space
- [ ] Monthly: Update dependencies
- [ ] Monthly: Security patches
- [ ] Quarterly: Performance review

---

## Conclusion

Follow this guide to deploy SaveUp securely and efficiently to production. Always test in a staging environment before production deployment.

For issues, check logs and refer to troubleshooting section.
