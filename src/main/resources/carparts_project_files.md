# Car Parts Microservice - IntelliJ Project Setup

## 1. Project Structure
Create this folder structure in IntelliJ:

```
car-parts-microservice/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── carparts/
│   │   │           └── microservice/
│   │   │               ├── CarPartsApplication.java
│   │   │               ├── config/
│   │   │               │   └── DatabaseConfig.java
│   │   │               ├── controller/
│   │   │               │   └── CarPartController.java
│   │   │               ├── entity/
│   │   │               │   ├── CarPart.java
│   │   │               │   └── PartStatus.java
│   │   │               ├── repository/
│   │   │               │   └── CarPartRepository.java
│   │   │               ├── service/
│   │   │               │   └── CarPartService.java
│   │   │               └── exception/
│   │   │                   ├── CarPartNotFoundException.java
│   │   │                   ├── DuplicatePartNumberException.java
│   │   │                   ├── ErrorResponse.java
│   │   │                   └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── data.sql
│   └── test/
│       └── java/
│           └── com/
│               └── carparts/
│                   └── microservice/
│                       ├── CarPartsApplicationTests.java
│                       ├── controller/
│                       │   └── CarPartControllerTest.java
│                       ├── service/
│                       │   └── CarPartServiceTest.java
│                       └── repository/
│                           └── CarPartRepositoryTest.java
├── pom.xml
├── README.md
└── docker-compose.yml
```

## 2. Step-by-Step IntelliJ Setup

### Step 1: Create New Project
1. Open IntelliJ IDEA
2. Click "New Project"
3. Select "Spring Initializr"
4. Configure:
   - **Project SDK**: Java 17 or 21
   - **Choose Initializr Service URL**: Default (start.spring.io)
   - Click "Next"

### Step 2: Project Metadata
```
Group: com.carparts
Artifact: microservice
Type: Maven Project
Language: Java
Packaging: Jar
Java Version: 17 (or 21)
Version: 0.0.1-SNAPSHOT
Name: car-parts-microservice
Description: Car Parts Management Microservice
Package name: com.carparts.microservice
```

### Step 3: Dependencies Selection
Select these dependencies:
- **Spring Boot DevTools** (Developer Tools)
- **Spring Web** (Web)
- **Spring Data JPA** (SQL)
- **H2 Database** (SQL) - for development
- **PostgreSQL Driver** (SQL) - for production
- **Validation** (I/O)
- **Spring Boot Actuator** (Ops)

### Step 4: Finish Project Creation
- Choose project location
- Click "Finish"

## 3. Configuration Files

### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.carparts</groupId>
    <artifactId>microservice</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>car-parts-microservice</name>
    <description>Car Parts Management Microservice</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- H2 Database for development -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- PostgreSQL for production -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Development tools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- Test dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### application.yml (Main Configuration)
```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: car-parts-microservice
  
  profiles:
    active: dev
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
    open-in-view: false

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    com.carparts.microservice: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### application-dev.yml (Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:carparts
    driverClassName: org.h2.Driver
    username: sa
    password: password
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect

logging:
  level:
    com.carparts.microservice: DEBUG
```

### application-prod.yml (Production)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/carparts
    username: ${DB_USERNAME:carparts_user}
    password: ${DB_PASSWORD:carparts_password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false

logging:
  level:
    com.carparts.microservice: INFO
    org.hibernate.SQL: WARN
```

### data.sql (Sample Data)
```sql
-- Sample car parts data
INSERT INTO car_parts (part_number, brand_name, model, year, part_name, description, category, price, stock_quantity, supplier, status, created_at, updated_at) VALUES
('BRK001', 'Toyota', 'Camry', 2020, 'Front Brake Pad Set', 'High-performance ceramic brake pads for front wheels', 'Brakes', 89.99, 25, 'Auto Parts Plus', 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ENG002', 'Honda', 'Civic', 2019, 'Oil Filter', 'Premium oil filter compatible with Honda Civic 2019', 'Engine', 15.99, 50, 'Honda Parts Direct', 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TIR003', 'Ford', 'F-150', 2021, 'All-Season Tire', 'All-season tire 265/70R17', 'Tires', 145.00, 12, 'Tire World', 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BAT004', 'Chevrolet', 'Malibu', 2018, 'Car Battery', '12V automotive battery with 3-year warranty', 'Electrical', 129.99, 8, 'Battery Express', 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AIR005', 'Nissan', 'Altima', 2020, 'Air Filter', 'Engine air filter for optimal performance', 'Engine', 24.99, 30, 'Filter Pro', 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### docker-compose.yml (For PostgreSQL)
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    container_name: carparts-postgres
    environment:
      POSTGRES_DB: carparts
      POSTGRES_USER: carparts_user
      POSTGRES_PASSWORD: carparts_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - carparts-network

  pgadmin:
    image: dpage/pgadmin4
    container_name: carparts-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@carparts.com
      PGADMIN_DEFAULT_PASSWORD: admin123
    ports:
      - "5050:80"
    depends_on:
      - postgres
    networks:
      - carparts-network

volumes:
  postgres_data:

networks:
  carparts-network:
    driver: bridge
```

### README.md
```markdown
# Car Parts Microservice

A Spring Boot microservice for managing car parts inventory with search capabilities.

## Features

- CRUD operations for car parts
- Advanced search functionality
- Database integration (H2 for dev, PostgreSQL for prod)
- RESTful API endpoints
- Data validation
- Exception handling
- Pagination support

## Quick Start

### Development Mode (H2 Database)
```bash
mvn spring-boot:run
```

### Production Mode (PostgreSQL)
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Run application
mvn spring-boot:run -Dspring.profiles.active=prod
```

## API Endpoints

### Car Parts Management
- `GET /api/car-parts` - Get all parts (paginated)
- `POST /api/car-parts` - Create new part
- `GET /api/car-parts/{id}` - Get part by ID
- `PUT /api/car-parts/{id}` - Update part
- `DELETE /api/car-parts/{id}` - Delete part

### Search & Filter
- `GET /api/car-parts/search` - Advanced search
- `GET /api/car-parts/search/text?q={term}` - Full text search
- `GET /api/car-parts/brand/{brand}` - Get parts by brand
- `GET /api/car-parts/brand/{brand}/model/{model}` - Get parts by brand and model

### Metadata
- `GET /api/car-parts/brands` - Get all brands
- `GET /api/car-parts/stats` - Get statistics

## Database Access

### H2 Console (Development)
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:carparts
- Username: sa
- Password: password

### PostgreSQL (Production)
- Host: localhost:5432
- Database: carparts
- Username: carparts_user
- Password: carparts_password

## Health Check
- URL: http://localhost:8080/actuator/health
```

## 4. How to Run in IntelliJ

### Method 1: Using Run Configuration
1. Right-click on `CarPartsApplication.java`
2. Select "Run 'CarPartsApplication'"
3. Application will start on http://localhost:8080

### Method 2: Using Maven
1. Open Terminal in IntelliJ (Alt+F12)
2. Run: `mvn spring-boot:run`

### Method 3: Run Configuration Setup
1. Go to Run → Edit Configurations
2. Click "+" → Spring Boot
3. Configure:
   - Name: Car Parts Microservice
   - Main class: com.carparts.microservice.CarPartsApplication
   - Active profiles: dev (for development)

## 5. Testing the API

### Using IntelliJ HTTP Client
Create a file `api-test.http`:

```http
### Get all car parts
GET http://localhost:8080/api/car-parts

### Create a new car part
POST http://localhost:8080/api/car-parts
Content-Type: application/json

{
  "partNumber": "TEST001",
  "brandName": "Toyota",
  "model": "Prius",
  "year": 2022,
  "partName": "Hybrid Battery",
  "description": "High-capacity hybrid battery",
  "category": "Electrical",
  "price": 2999.99,
  "stockQuantity": 5,
  "supplier": "Toyota Direct"
}

### Search car parts
GET http://localhost:8080/api/car-parts/search?brandName=Toyota&year=2020

### Get statistics
GET http://localhost:8080/api/car-parts/stats
```

## 6. Database Setup

### For Development (H2):
- No setup required
- Data is in-memory
- Access H2 console at http://localhost:8080/h2-console

### For Production (PostgreSQL):
```bash
# Start PostgreSQL using Docker
docker-compose up -d postgres

# Run with production profile
mvn spring-boot:run -Dspring.profiles.active=prod
```

This setup gives you a complete, production-ready car parts microservice with all the features you requested!