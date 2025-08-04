package java.utils.project.org;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class ProjectStructureGenerator {
    
    private static final Pattern TREE_SYMBOL_PATTERN = Pattern.compile("^(\\s*)[│├└─\\s]*([├└]──)?\\s*(.+)$");
    
    private static Pattern FOLDER_PATTERN = Pattern.compile("^(\\s*)([├└]──|[│\\s]*[├└]──)\\s*(.+?)/?\\s*$");
    private static Pattern FILE_PATTERN = Pattern.compile("^(\\s*)([├└]──|[│\\s]*[├└]──)\\s*(.+?\\.[a-zA-Z0-9]+)\\s*$");
    private static Pattern SIMPLE_ITEM_PATTERN = Pattern.compile("^(\\s*)(.+?)/?\\s*$");
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ProjectStructureGenerator <markdown-file> <output-directory>");
            System.out.println("Example: java ProjectStructureGenerator project-structure.md ./my-project");
            System.exit(1);
        }
        
        String markdownFile = args[0];
        String outputDirectory = args[1];
        
        try {
            generateProjectStructure(markdownFile, outputDirectory);
            System.out.println("Project structure generated successfully in: " + outputDirectory);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void generateProjectStructure(String markdownFile, String outputDirectory) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(markdownFile));
        Path basePath = Paths.get(outputDirectory);
        
        // Create base directory if it doesn't exist
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }
        
        // Parse the structure into a tree representation first
        List<StructureItem> structure = parseStructureFromMarkdown(lines);
        
        // Generate the actual file system structure recursively
        generateStructureRecursively(structure, basePath);
        
        System.out.println("Project structure generation completed!");
        System.out.println("Total items processed: " + countTotalItems(structure));
    }
    
    private static List<StructureItem> parseStructureFromMarkdown(List<String> lines) {
        List<StructureItem> rootItems = new ArrayList<>();
        Stack<StructureItem> parentStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();
        
        boolean inCodeBlock = false;
        
        for (String line : lines) {
            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }
            
            // Check for code block markers
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                continue;
            }
            
            // Only process lines within code blocks
            if (!inCodeBlock) {
                continue;
            }
            
            // Skip lines that don't match our patterns
            if (!isStructureLine(line)) {
                continue;
            }
            
            int currentIndent = getIndentLevel(line);
            String itemName = extractItemName(line);
            
            if (itemName == null || itemName.isEmpty()) {
                continue;
            }
            
            // Create structure item
            StructureItem item = new StructureItem(itemName, isFile(itemName));
            
            // Adjust parent stack based on indentation
            while (!indentStack.isEmpty() && currentIndent <= indentStack.peek()) {
                parentStack.pop();
                indentStack.pop();
            }
            
            // Add to appropriate parent or root
            if (parentStack.isEmpty()) {
                rootItems.add(item);
            } else {
                parentStack.peek().addChild(item);
            }
            
            // If it's a directory, push it onto the stack for potential children
            if (!item.isFile()) {
                parentStack.push(item);
                indentStack.push(currentIndent);
            }
        }
        
        return rootItems;
    }
    
    private static void generateStructureRecursively(List<StructureItem> items, Path parentPath) throws IOException {
        for (StructureItem item : items) {
            Path itemPath = parentPath.resolve(item.getName());
            
            if (item.isFile()) {
                // Create file with all necessary parent directories
                createFile(itemPath);
                System.out.println("Created file: " + getRelativePath(itemPath));
            } else {
                // Create directory with all necessary parent directories
                if (!Files.exists(itemPath)) {
                    Files.createDirectories(itemPath);
                    System.out.println("Created directory: " + getRelativePath(itemPath));
                }
                
                // Recursively create children
                if (!item.getChildren().isEmpty()) {
                    generateStructureRecursively(item.getChildren(), itemPath);
                }
            }
        }
    }
    
    private static String getRelativePath(Path path) {
        try {
            return Paths.get("").toAbsolutePath().relativize(path.toAbsolutePath()).toString();
        } catch (Exception e) {
            return path.toString();
        }
    }
    
    private static int countTotalItems(List<StructureItem> items) {
        int count = items.size();
        for (StructureItem item : items) {
            count += countTotalItems(item.getChildren());
        }
        return count;
    }
    
    // Inner class to represent structure items
    private static class StructureItem {
        private final String name;
        private final boolean isFile;
        private final List<StructureItem> children;
        
        public StructureItem(String name, boolean isFile) {
            this.name = name;
            this.isFile = isFile;
            this.children = new ArrayList<>();
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isFile() {
            return isFile;
        }
        
        public List<StructureItem> getChildren() {
            return children;
        }
        
        public void addChild(StructureItem child) {
            children.add(child);
        }
        
        @Override
        public String toString() {
            return name + (isFile ? " (file)" : " (dir)") + 
                   (children.isEmpty() ? "" : " [" + children.size() + " children]");
        }
    }
    
    private static boolean isStructureLine(String line) {
        String trimmed = line.trim();
        
        // Skip obviously invalid lines
        if (trimmed.isEmpty() || 
            trimmed.startsWith("#") || 
            trimmed.startsWith("//") ||
            trimmed.startsWith("```") ||
            trimmed.startsWith("*") ||
            (trimmed.startsWith("-") && !trimmed.contains("─"))) {
            return false;
        }
        
        // Check if line contains structure indicators or looks like a file/folder
        return trimmed.matches(".*[│├└─].*") || // Contains tree symbols
               trimmed.matches(".*\\.[a-zA-Z0-9]+$") || // Looks like a file
               trimmed.matches("^[a-zA-Z0-9._-]+/?$") || // Simple name with optional trailing slash
               line.matches("^\\s+.*"); // Indented item
    }
    
    private static int getIndentLevel(String line) {
        int level = 0;
        int i = 0;
        
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == ' ') {
                level++;
            } else if (c == '\t') {
                level += 4; // Tab equals 4 spaces
            } else if (c == '│' || c == '├' || c == '└' || c == '─') {
                // Tree symbols don't count toward base indentation
                // but we need to account for the structure they represent
                level += 2; // Each tree level adds some logical indentation
            } else {
                break;
            }
            i++;
        }
        
        // Normalize indentation to logical levels (every 2-4 characters = 1 level)
        return level / 2;
    }
    
    private static String extractItemName(String line) {
        // Remove all tree symbols and whitespace to get clean name
        String cleaned = line.replaceAll("^\\s*", ""); // Remove leading whitespace
        cleaned = cleaned.replaceAll("[│├└─\\s]*", ""); // Remove tree symbols and spaces
        cleaned = cleaned.replaceAll("^\\s+", ""); // Remove any remaining leading spaces
        cleaned = cleaned.trim();
        
        // If we removed too much, try a different approach
        if (cleaned.isEmpty()) {
            // Look for the actual content after tree symbols
            var matcher = TREE_SYMBOL_PATTERN.matcher(line);
            if (matcher.matches()) {
                cleaned = matcher.group(3).trim();
            }
        }
        
        // Remove trailing slash for directories
        if (cleaned.endsWith("/") && cleaned.length() > 1) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        
        return cleaned.isEmpty() ? null : cleaned;
    }
    
    private static boolean isFile(String itemName) {
        // Check if the item has a file extension
        return itemName.contains(".") && 
               itemName.lastIndexOf('.') > 0 && 
               itemName.lastIndexOf('.') < itemName.length() - 1;
    }
    
    private static void createFile(Path filePath) throws IOException {
        // Create parent directories if they don't exist
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        // Create the file if it doesn't exist
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                
                // Add basic content based on file type
                String fileName = filePath.getFileName().toString();
                String content = generateBasicContent(fileName, filePath);
                if (!content.isEmpty()) {
                    Files.write(filePath, content.getBytes());
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not create file " + filePath + ": " + e.getMessage());
                // Continue processing other files
            }
        }
    }
    
    private static String generateBasicContent(String fileName, Path filePath) {
        if (fileName.endsWith(".java")) {
            return generateJavaContent(fileName, filePath);
        } else if (fileName.endsWith(".xml")) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- TODO: Add XML content -->\n";
        } else if (fileName.endsWith(".properties")) {
            return "# Configuration properties\n# TODO: Add properties\n";
        } else if (fileName.endsWith(".md")) {
            return "# " + fileName.substring(0, fileName.lastIndexOf('.')) + "\n\nTODO: Add documentation\n";
        } else if (fileName.endsWith(".gitignore")) {
            return "# IDE files\n.idea/\n*.iml\n\n# Build files\ntarget/\nbuild/\nout/\n\n# OS files\n.DS_Store\nThumbs.db\n\n# Logs\n*.log\n";
        } else if (fileName.equals("pom.xml")) {
            return generatePomXml();
        } else if (fileName.equals("build.gradle")) {
            return generateBuildGradle();
        } else if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            return "# YAML configuration\n# TODO: Add configuration\n";
        } else if (fileName.endsWith(".json")) {
            return "{\n  \"TODO\": \"Add JSON content\"\n}\n";
        } else if (fileName.endsWith(".html")) {
            return generateHtmlContent(fileName);
        } else if (fileName.endsWith(".css")) {
            return "/* CSS Styles */\n/* TODO: Add styles */\n";
        } else if (fileName.endsWith(".js")) {
            return "// JavaScript\n// TODO: Add JavaScript code\n";
        } else if (fileName.endsWith(".sql")) {
            return "-- SQL Script\n-- TODO: Add SQL statements\n";
        }
        return "";
    }
    
    private static String generateJavaContent(String fileName, Path filePath) {
        String className = fileName.substring(0, fileName.lastIndexOf('.'));
        
        // Try to determine package from path
        String packageName = "";
        Path parent = filePath.getParent();
        if (parent != null) {
            List<String> pathParts = new ArrayList<>();
            boolean foundJava = false;
            
            // Walk up the path to find 'java' directory
            while (parent != null) {
                String dirName = parent.getFileName().toString();
                if ("java".equals(dirName)) {
                    foundJava = true;
                    break;
                }
                pathParts.add(0, dirName);
                parent = parent.getParent();
            }
            
            if (foundJava && !pathParts.isEmpty()) {
                packageName = "package " + String.join(".", pathParts) + ";\n\n";
            }
        }
        
        // Generate appropriate class type
        if (className.endsWith("Test")) {
            return packageName + String.format("""
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;
                
                public class %s {
                    
                    @Test
                    public void testExample() {
                        // TODO: Implement test
                        assertTrue(true);
                    }
                }
                """, className);
        } else if (className.endsWith("Exception")) {
            return packageName + String.format("""
                public class %s extends Exception {
                    
                    public %s(String message) {
                        super(message);
                    }
                    
                    public %s(String message, Throwable cause) {
                        super(message, cause);
                    }
                }
                """, className, className, className);
        } else if (className.endsWith("Interface") || className.startsWith("I")) {
            return packageName + String.format("""
                public interface %s {
                    // TODO: Define interface methods
                }
                """, className);
        } else {
            return packageName + String.format("""
                public class %s {
                    
                    public %s() {
                        // TODO: Implement constructor
                    }
                    
                    // TODO: Add methods
                }
                """, className, className);
        }
    }
    
    private static String generateHtmlContent(String fileName) {
        String title = fileName.substring(0, fileName.lastIndexOf('.'));
        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                </head>
                <body>
                    <h1>%s</h1>
                    <!-- TODO: Add content -->
                </body>
                </html>
                """, title, title);
    }
    
    private static String generatePomXml() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                         http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    
                    <groupId>com.example</groupId>
                    <artifactId>my-project</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    
                    <properties>
                        <maven.compiler.source>11</maven.compiler.source>
                        <maven.compiler.target>11</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                    
                    <dependencies>
                        <!-- TODO: Add dependencies -->
                    </dependencies>
                </project>
                """;
    }
    
    private static String generateBuildGradle() {
        return """
                plugins {
                    id 'java'
                }
                
                group 'com.example'
                version '1.0-SNAPSHOT'
                
                java {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
                
                repositories {
                    mavenCentral()
                }
                
                dependencies {
                    // TODO: Add dependencies
                }
                
                test {
                    useJUnitPlatform()
                }
                """;
    }
}