# Book Microservices IntelliJ Project

This is a complete IntelliJ IDEA project structure for a microservices application with frontend integration.

## Project Structure

```
book-microservices/
├── .idea/                              # IntelliJ IDEA configuration
│   ├── modules.xml
│   ├── workspace.xml
│   ├── compiler.xml
│   ├── encodings.xml
│   ├── misc.xml
│   ├── vcs.xml
│   └── runConfigurations/
│       ├── BookService.xml
│       ├── Frontend_Dev.xml
│       ├── All_Services.xml
│       └── Docker_Compose.xml
├── book-service/                       # Backend microservice
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── bookstore/
│   │       │           ├── BookServiceApplication.java
│   │       │           ├── controller/
│   │       │           │   └── BookController.java
│   │       │           ├── model/
│   │       │           │   └── Book.java
│   │       │           ├── repository/
│   │       │           │   └── BookRepository.java
│   │       │           └── service/
│   │       │               └── BookService.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── application-dev.yml
│   ├── pom.xml
│   └── Dockerfile
├── book-frontend/                      # Frontend module
│   ├── src/
│   │   ├── index.html
│   │   ├── styles/
│   │   │   └── main.css
│   │   └── scripts/
│   │       └── api.js
│   ├── dev-server.js
│   ├── package.json
│   ├── Dockerfile
│   └── Dockerfile.dev
├── docker-compose.yml                  # Multi-service orchestration
├── docker-compose.dev.yml             # Development environment
├── nginx.conf                         # API Gateway configuration
├── pom.xml                            # Root Maven configuration
├── README.md                          # Project documentation
└── .gitignore                         # Git ignore rules
```

---

## File Contents

### Root Configuration Files

#### pom.xml (Root)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.bookstore</groupId>
    <artifactId>microservices-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <name>Book Microservices Project</name>
    <description>Multi-module project with microservices and frontend</description>
    
    <modules>
        <module>book-service</module>
        <module>book-frontend</module>
    </modules>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring.boot.version>2.7.14</spring.boot.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

#### .gitignore
```gitignore
# IntelliJ IDEA
.idea/workspace.xml
.idea/tasks.xml
.idea/dictionaries/
.idea/shelf/
.idea/libraries/
.idea/modules/
.idea/artifacts/
.idea/inspectionProfiles/
*.iws
*.iml
*.ipr
out/

# Maven
target/
!.mvn/wrapper/maven-wrapper.jar

# Node.js
node_modules/
npm-debug.log*
package-lock.json

# Logs
*.log
logs/

# OS
.DS_Store
Thumbs.db

# Environment
.env
.env.local
.env.production
.env.development

# Docker
.dockerignore
```

---

### IntelliJ Configuration Files

#### .idea/modules.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/book-microservices.iml" filepath="$PROJECT_DIR$/book-microservices.iml" />
      <module fileurl="file://$PROJECT_DIR$/book-service/book-service.iml" filepath="$PROJECT_DIR$/book-service/book-service.iml" />
      <module fileurl="file://$PROJECT_DIR$/book-frontend/book-frontend.iml" filepath="$PROJECT_DIR$/book-frontend/book-frontend.iml" />
    </modules>
  </component>
</project>
```

#### .idea/misc.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ExternalStorageConfigurationManager" enabled="true" />
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" languageLevel="JDK_11" default="true" project-jdk-name="11" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/out" />
  </component>
</project>
```

#### .idea/compiler.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <annotationProcessing>
      <profile name="Maven default annotation processors profile" enabled="true">
        <sourceOutputDir name="target/generated-sources/annotations" />
        <sourceTestOutputDir name="target/generated-test-sources/test-annotations" />
        <outputRelativeToContentRoot value="true" />
        <module name="book-service" />
      </profile>
    </annotationProcessing>
  </component>
</project>
```

#### .idea/encodings.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="Encoding">
    <file url="file://$PROJECT_DIR$/book-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/book-service/src/main/resources" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/src/main/resources" charset="UTF-8" />
  </component>
</project>
```

#### .idea/vcs.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="" vcs="Git" />
  </component>
</project>
```

---

### Run Configurations

#### .idea/runConfigurations/BookService.xml
```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="BookService" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
    <module name="book-service" />
    <option name="SPRING_BOOT_MAIN_CLASS" value="com.bookstore.BookServiceApplication" />
    <option name="ALTERNATIVE_JRE_PATH" />
    <option name="SHORTEN_COMMAND_LINE" value="NONE" />
    <option name="FRAME_DEACTIVATION_UPDATE_POLICY" value="UpdateClassesAndResources" />
    <envs>
      <env name="SPRING_PROFILES_ACTIVE" value="dev" />
    </envs>
    <method v="2">
      <option name="Make" enabled="true" />
    </method>
  </configuration>
</component>
```

#### .idea/runConfigurations/Frontend_Dev.xml
```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="Frontend Dev" type="NodeJSConfigurationType" factoryName="Node.js">
    <option name="WORKING_DIR" value="$PROJECT_DIR$/book-frontend" />
    <option name="JAVASCRIPT_FILE" value="dev-server.js" />
    <option name="APPLICATION_PARAMETERS" value="" />
    <option name="NODE_PARAMETERS" value="" />
    <envs>
      <env name="NODE_ENV" value="development" />
      <env name="PORT" value="3000" />
    </envs>
    <EXTENSION ID="com.jetbrains.nodejs.run.NodeJSStartBrowserRunConfigurationExtension">
      <browser start="true" url="http://localhost:3000" />
    </EXTENSION>
    <method v="2" />
  </configuration>
</component>
```

#### .idea/runConfigurations/All_Services.xml
```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="All Services" type="CompoundRunConfigurationType" factoryName="Compound Run Configuration">
    <toRun name="BookService" type="SpringBootApplicationConfigurationType" />
    <toRun name="Frontend Dev" type="NodeJSConfigurationType" />
    <method v="2" />
  </configuration>
</component>
```

#### .idea/runConfigurations/Docker_Compose.xml
```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="Docker Compose" type="docker-deploy" factoryName="docker-compose.yml" server-name="Docker">
    <deployment type="docker-compose.yml">
      <settings>
        <option name="sourceFilePath" value="docker-compose.dev.yml" />
      </settings>
    </deployment>
    <method v="2" />
  </configuration>
</component>
```

---

### Backend Service (book-service)

#### book-service/pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.bookstore</groupId>
        <artifactId>microservices-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>book-service</artifactId>
    <packaging>jar</packaging>
    
    <name>Book Service</name>
    <description>REST API for book management</description>
    
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
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
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

#### book-service/src/main/java/com/bookstore/BookServiceApplication.java
```java
package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:63342"})
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}
```

#### book-service/src/main/java/com/bookstore/model/Book.java
```java
package com.bookstore.model;

import javax.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true)
    private String isbn;
    
    private String genre;
    private Integer pages;
    private String status = "available";
    
    // Constructors
    public Book() {}
    
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;    
        this.isbn = isbn;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

#### book-service/src/main/java/com/bookstore/repository/BookRepository.java
```java
package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "b.isbn LIKE CONCAT('%', :search, '%')")
    List<Book> findBySearchTerm(@Param("search") String search);
    
    List<Book> findByStatus(String status);
}
```

#### book-service/src/main/java/com/bookstore/service/BookService.java
```java
package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> searchBooks(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.findBySearchTerm(search);
    }
    
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
```

#### book-service/src/main/java/com/bookstore/controller/BookController.java
```java
package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:63342"})
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public List<Book> getAllBooks(@RequestParam(required = false) String search) {
        if (search != null) {
            return bookService.searchBooks(search);
        }
        return bookService.getAllBooks();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Optional<Book> book = bookService.getBookById(id);
        
        if (book.isPresent()) {
            Book existingBook = book.get();
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setIsbn(bookDetails.getIsbn());
            existingBook.setGenre(bookDetails.getGenre());
            existingBook.setPages(bookDetails.getPages());
            existingBook.setStatus(bookDetails.getStatus());
            
            return ResponseEntity.ok(bookService.saveBook(existingBook));
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        
        if (book.isPresent()) {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}
```

#### book-service/src/main/resources/application.yml
```yaml
spring:
  application:
    name: book-service
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
server:
  port: 8080

logging:
  level:
    com.bookstore: DEBUG
    org.springframework.web: DEBUG
```

#### book-service/src/main/resources/application-dev.yml
```yaml
spring:
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    com.bookstore: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### book-service/Dockerfile
```dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/book-service-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

---

### Frontend Module (book-frontend)

#### book-frontend/pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.bookstore</groupId>
        <artifactId>microservices-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>book-frontend</artifactId>
    <packaging>pom</packaging>
    
    <name>Book Frontend</name>
    <description>Frontend for Book Microservice</description>
    
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.eirslett</groupId>
                <artifactId>frontend-maven-plugin</artifactId>
                <version>1.12.1</version>
                <executions>
                    <execution>
                        <id>install node and npm</id>
                        <goals>
                            <goal>install-node-and-npm</goal>
                        </goals>
                        <configuration>
                            <nodeVersion>v18.17.0</nodeVersion>
                            <npmVersion>9.6.7</npmVersion>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm install</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>install</arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### book-frontend/package.json
```json
{
  "name": "book-microservice-frontend",
  "version": "1.0.0",
  "description": "Frontend for Book Microservice",
  "main": "dev-server.js",
  "scripts": {
    "start": "node dev-server.js",
    "dev": "nodemon dev-server.js",
    "build": "echo 'Building frontend assets...'",
    "test": "echo 'Running frontend tests...'"
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5"
  },
  "devDependencies": {
    "nodemon": "^3.0.1"
  },
  "engines": {
    "node": ">=16.0.0",
    "npm": ">=8.0.0"
  }
}
```

#### book-frontend/dev-server.js
```javascript
const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// CORS configuration
app.use(cors({
  origin: ['http://localhost:3000', 'http://localhost:8080'],
  credentials: true
}));

// Serve static files
app.use(express.static('.'));
app.use('/src', express.static('src'));

// API proxy for development (optional)
app.use('/api/*', (req, res) => {
  res.status(502).json({
    error: "API Proxy not configured",
    message: "Please start the book-service backend",
    suggestion: "Run BookService configuration in IntelliJ"
  });
});

// Serve frontend for all routes (SPA support)
app.get('*', (req, res) => {
  if (!req.path.startsWith('/api')) {
    res.sendFile(path.join(__dirname, 'src', 'index.html'));
  }
});

app.listen(PORT, () => {
  console.log(`🚀 Frontend development server running on http://localhost:${PORT}`);
  console.log(`📁 Serving files from: ${__dirname}`);
  console.log(`🔧 Make sure book-service is running on http://localhost:8080`);
});
```

#### book-frontend/src/index.html
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Microservice Dashboard</title>
    <link rel="stylesheet" href="styles/main.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📚 Book Microservice Dashboard</h1>
            <p>Manage your book collection with REST API operations</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number" id="total-books">0</div>
                <div>Total Books</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="available-books">0</div>
                <div>Available</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="borrowed-books">0</div>
                <div>Borrowed</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="maintenance-books">0</div>
                <div>Maintenance</div>
            </div>
        </div>

        <div class="main-content">
            <div class="books-section">
                <div class="section-header">
                    <h2 class="section-title">Book Collection</h2>
                    <button class="btn btn-primary" onclick="refreshBooks()">🔄 Refresh</button>
                </div>

                <div class="search-bar">
                    <input type="text" class="search-input" placeholder="Search books..." id="searchInput">
                    <button class="btn btn-secondary" onclick="searchBooks()">🔍 Search</button>
                    <button class="btn btn-secondary" onclick="clearSearch()">Clear</button>
                </div>

                <div class="book-list" id="bookList">
                    <!-- Books will be loaded here -->
                </div>
            </div>

            <div class="sidebar">
                <div class="add-book-form">
                    <div class="section-header">
                        <h3 class="section-title">Add New Book</h3>
                    </div>
                    
                    <form id="addBookForm" onsubmit="addBook(event)">
                        <div class="form-group">
                            <label class="form-label">Title</label>
                            <input type="text" class="form-input" name="title" required>
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">Author</label>
                            <input type="text" class="form-input" name="author" required>
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">ISBN</label>
                            <input type="text" class="form-input" name="isbn">
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">Genre</label>
                            <input type="text" class="form-input" name="genre">
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">Pages</label>
                            <input type="number" class="form-input" name="pages">
                        </div>
                        
                        <button type="submit" class="btn btn-primary" style="width: 100%;">➕ Add Book</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="scripts/api.js"></script>
</body>
</html>
```

#### book-frontend/src/styles/main.css
```css
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    color: #333;
}

.container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
}

.header {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 15px;
    padding: 25px;
    margin-bottom: 30px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header h1 {
    font-size: 2.5rem;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: 10px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.stat-card {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 15px;
    padding: 25px;
    text-align: center;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
}