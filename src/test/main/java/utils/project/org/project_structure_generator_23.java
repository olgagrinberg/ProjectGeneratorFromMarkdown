package java.utils.project.org;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class ProjectStructureGenerator {
    private static final String PROJECT_NAME = "book-microservices";
    private static final Pattern FILE_HEADER_PATTERN = Pattern.compile("####\\s+(.+)");
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```(\\w+)?\\s*\\n([\\s\\S]*?)```");
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ProjectStructureGenerator <markdown-file-path> [output-directory]");
            System.out.println("Example: java ProjectStructureGenerator paste.txt ./output");
            return;
        }
        
        String markdownFile = args[0];
        String outputDir = args.length > 1 ? args[1] : "./";
        
        try {
            ProjectStructureGenerator generator = new ProjectStructureGenerator();
            generator.generateProject(markdownFile, outputDir);
            System.out.println("✅ Project structure generated successfully!");
            System.out.println("📁 Location: " + Paths.get(outputDir, PROJECT_NAME).toAbsolutePath());
            System.out.println("\n🚀 Next steps:");
            System.out.println("1. Open IntelliJ IDEA");
            System.out.println("2. File -> Open -> Select the '" + PROJECT_NAME + "' folder");
            System.out.println("3. Wait for Maven to import dependencies");
            System.out.println("4. Run the 'All Services' configuration to start both backend and frontend");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void generateProject(String markdownFile, String outputDir) throws IOException {
        String content = Files.readString(Paths.get(markdownFile));
        Path projectRoot = Paths.get(outputDir, PROJECT_NAME);
        
        // Create project root directory
        Files.createDirectories(projectRoot);
        System.out.println("📁 Created project root: " + projectRoot);
        
        // Create directory structure first
        createDirectoryStructure(projectRoot);
        
        // Parse and create files
        parseAndCreateFiles(content, projectRoot);
        
        // Create additional necessary files
        createAdditionalFiles(projectRoot);
    }
    
    private void createDirectoryStructure(Path projectRoot) throws IOException {
        String[] directories = {
            ".idea",
            ".idea/runConfigurations",
            "book-service",
            "book-service/src",
            "book-service/src/main",
            "book-service/src/main/java",
            "book-service/src/main/java/com",
            "book-service/src/main/java/com/bookstore",
            "book-service/src/main/java/com/bookstore/controller",
            "book-service/src/main/java/com/bookstore/model",
            "book-service/src/main/java/com/bookstore/repository",
            "book-service/src/main/java/com/bookstore/service",
            "book-service/src/main/resources",
            "book-service/src/test",
            "book-service/src/test/java",
            "book-frontend",
            "book-frontend/src",
            "book-frontend/src/styles",
            "book-frontend/src/scripts"
        };
        
        for (String dir : directories) {
            Path dirPath = projectRoot.resolve(dir);
            Files.createDirectories(dirPath);
            System.out.println("📂 Created directory: " + dir);
        }
    }
    
    private void parseAndCreateFiles(String content, Path projectRoot) throws IOException {
        Map<String, String> files = new HashMap<>();
        
        // Split content by file headers
        String[] sections = content.split("(?=####\\s+)");
        
        for (String section : sections) {
            String[] lines = section.split("\n");
            if (lines.length == 0) continue;
            
            Matcher headerMatcher = FILE_HEADER_PATTERN.matcher(lines[0]);
            if (!headerMatcher.matches()) continue;
            
            String filePath = headerMatcher.group(1).trim();
            String fileContent = extractCodeContent(section);
            
            if (!fileContent.isEmpty()) {
                files.put(filePath, fileContent);
            }
        }
        
        // Create files
        for (Map.Entry<String, String> entry : files.entrySet()) {
            createFile(projectRoot, entry.getKey(), entry.getValue());
        }
    }
    
    private String extractCodeContent(String section) {
        Matcher codeMatcher = CODE_BLOCK_PATTERN.matcher(section);
        if (codeMatcher.find()) {
            return codeMatcher.group(2).trim();
        }
        return "";
    }
    
    private void createFile(Path projectRoot, String filePath, String content) throws IOException {
        // Clean up file path
        filePath = filePath.replace("(Root)", "").trim();
        
        Path fullPath = projectRoot.resolve(filePath);
        
        // Ensure parent directories exist
        Files.createDirectories(fullPath.getParent());
        
        // Write file content
        Files.writeString(fullPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("📄 Created file: " + filePath);
    }
    
    private void createAdditionalFiles(Path projectRoot) throws IOException {
        // Create .iml files for IntelliJ modules
        createImlFiles(projectRoot);
        
        // Create workspace.xml
        createWorkspaceXml(projectRoot);
        
        // Create README.md
        createReadme(projectRoot);
        
        // Create docker-compose files
        createDockerComposeFiles(projectRoot);
        
        // Create nginx.conf
        createNginxConf(projectRoot);
        
        System.out.println("📝 Created additional configuration files");
    }
    
    private void createImlFiles(Path projectRoot) throws IOException {
        // Root .iml file
        String rootIml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <module type="JAVA_MODULE" version="4">
              <component name="NewModuleRootManager" inherit-compiler-output="true">
                <exclude-output />
                <content url="file://$MODULE_DIR$">
                  <excludeFolder url="file://$MODULE_DIR$/target" />
                </content>
                <orderEntry type="inheritedJdk" />
                <orderEntry type="sourceFolder" forTests="false" />
              </component>
            </module>
            """;
        Files.writeString(projectRoot.resolve(PROJECT_NAME + ".iml"), rootIml);
        
        // book-service .iml file
        String bookServiceIml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <module type="JAVA_MODULE" version="4">
              <component name="NewModuleRootManager" LANGUAGE_LEVEL="JDK_11" inherit-compiler-output="false">
                <output url="file://$MODULE_DIR$/target/classes" />
                <output-test url="file://$MODULE_DIR$/target/test-classes" />
                <content url="file://$MODULE_DIR$">
                  <sourceFolder url="file://$MODULE_DIR$/src/main/java" isTestSource="false" />
                  <sourceFolder url="file://$MODULE_DIR$/src/main/resources" type="java-resource" />
                  <sourceFolder url="file://$MODULE_DIR$/src/test/java" isTestSource="true" />
                  <excludeFolder url="file://$MODULE_DIR$/target" />
                </content>
                <orderEntry type="inheritedJdk" />
                <orderEntry type="sourceFolder" forTests="false" />
              </component>
            </module>
            """;
        Files.writeString(projectRoot.resolve("book-service/book-service.iml"), bookServiceIml);
        
        // book-frontend .iml file
        String bookFrontendIml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <module type="WEB_MODULE" version="4">
              <component name="NewModuleRootManager" inherit-compiler-output="true">
                <exclude-output />
                <content url="file://$MODULE_DIR$">
                  <excludeFolder url="file://$MODULE_DIR$/node_modules" />
                  <excludeFolder url="file://$MODULE_DIR$/target" />
                </content>
                <orderEntry type="sourceFolder" forTests="false" />
              </component>
            </module>
            """;
        Files.writeString(projectRoot.resolve("book-frontend/book-frontend.iml"), bookFrontendIml);
    }
    
    private void createWorkspaceXml(Path projectRoot) throws IOException {
        String workspaceXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project version="4">
              <component name="ChangeListManager">
                <list default="true" id="default" name="Changes" comment="" />
                <option name="SHOW_DIALOG" value="false" />
                <option name="HIGHLIGHT_CONFLICTS" value="true" />
                <option name="HIGHLIGHT_NON_ACTIVE_CHANGELIST" value="false" />
                <option name="LAST_RESOLUTION" value="IGNORE" />
              </component>
              <component name="ProjectViewState">
                <option name="hideEmptyMiddlePackages" value="true" />
                <option name="showLibraryContents" value="true" />
              </component>
              <component name="RunManager" selected="Compound.All Services">
                <configuration name="All Services" type="CompoundRunConfigurationType" factoryName="Compound Run Configuration" />
                <configuration name="BookService" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot" />
                <configuration name="Frontend Dev" type="NodeJSConfigurationType" factoryName="Node.js" />
                <configuration name="Docker Compose" type="docker-deploy" factoryName="docker-compose.yml" />
              </component>
            </project>
            """;
        Files.writeString(projectRoot.resolve(".idea/workspace.xml"), workspaceXml);
    }
    
    private void createReadme(Path projectRoot) throws IOException {
        String readme = """
            # Book Microservices Project
            
            A complete microservices application with Spring Boot backend and frontend integration.
            
            ## Quick Start
            
            ### Prerequisites
            - Java 11+
            - Maven 3.6+
            - Node.js 16+ (for frontend development)
            - Docker (optional)
            
            ### Running the Application
            
            #### Option 1: IntelliJ IDEA (Recommended)
            1. Open this project in IntelliJ IDEA
            2. Wait for Maven to import dependencies
            3. Run the "All Services" configuration to start both backend and frontend
            
            #### Option 2: Command Line
            ```bash
            # Terminal 1 - Start Backend
            cd book-service
            mvn spring-boot:run
            
            # Terminal 2 - Start Frontend
            cd book-frontend
            npm install
            npm start
            ```
            
            #### Option 3: Docker
            ```bash
            docker-compose up -d
            ```
            
            ## Application URLs
            - Frontend: http://localhost:3000
            - Backend API: http://localhost:8080/api/books
            - H2 Database Console: http://localhost:8080/h2-console
            
            ## API Endpoints
            - GET /api/books - List all books
            - GET /api/books?search=term - Search books
            - GET /api/books/{id} - Get book by ID
            - POST /api/books - Create new book
            - PUT /api/books/{id} - Update book
            - DELETE /api/books/{id} - Delete book
            
            ## Project Structure
            - `book-service/` - Spring Boot REST API
            - `book-frontend/` - Frontend application
            - `.idea/` - IntelliJ IDEA configuration
            
            ## Development
            - Backend runs on port 8080
            - Frontend dev server runs on port 3000
            - H2 in-memory database for development
            - CORS enabled for frontend-backend communication
            
            Generated by ProjectStructureGenerator
            """;
        Files.writeString(projectRoot.resolve("README.md"), readme);
    }
    
    private void createDockerComposeFiles(Path projectRoot) throws IOException {
        String dockerCompose = """
            version: '3.8'
            services:
              book-service:
                build: ./book-service
                ports:
                  - "8080:8080"
                environment:
                  - SPRING_PROFILES_ACTIVE=prod
                networks:
                  - book-network
            
              book-frontend:
                build: ./book-frontend
                ports:
                  - "3000:3000"
                depends_on:
                  - book-service
                networks:
                  - book-network
            
              nginx:
                image: nginx:alpine
                ports:
                  - "80:80"
                volumes:
                  - ./nginx.conf:/etc/nginx/nginx.conf
                depends_on:
                  - book-service
                  - book-frontend
                networks:
                  - book-network
            
            networks:
              book-network:
                driver: bridge
            """;
        Files.writeString(projectRoot.resolve("docker-compose.yml"), dockerCompose);
        
        String dockerComposeDev = """
            version: '3.8'
            services:
              book-service:
                build: ./book-service
                ports:
                  - "8080:8080"
                environment:
                  - SPRING_PROFILES_ACTIVE=dev
                volumes:
                  - ./book-service/src:/app/src
                networks:
                  - book-network
            
              book-frontend:
                build:
                  context: ./book-frontend
                  dockerfile: Dockerfile.dev
                ports:
                  - "3000:3000"
                volumes:
                  - ./book-frontend/src:/app/src
                depends_on:
                  - book-service
                networks:
                  - book-network
            
            networks:
              book-network:
                driver: bridge
            """;
        Files.writeString(projectRoot.resolve("docker-compose.dev.yml"), dockerComposeDev);
    }
    
    private void createNginxConf(Path projectRoot) throws IOException {
        String nginxConf = """
            events {
                worker_connections 1024;
            }
            
            http {
                upstream backend {
                    server book-service:8080;
                }
                
                upstream frontend {
                    server book-frontend:3000;
                }
                
                server {
                    listen 80;
                    
                    location /api/ {
                        proxy_pass http://backend;
                        proxy_set_header Host $host;
                        proxy_set_header X-Real-IP $remote_addr;
                        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                        proxy_set_header X-Forwarded-Proto $scheme;
                    }
                    
                    location / {
                        proxy_pass http://frontend;
                        proxy_set_header Host $host;
                        proxy_set_header X-Real-IP $remote_addr;
                        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                        proxy_set_header X-Forwarded-Proto $scheme;
                    }
                }
            }
            """;
        Files.writeString(projectRoot.resolve("nginx.conf"), nginxConf);
    }
}