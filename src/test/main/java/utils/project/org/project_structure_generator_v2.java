package java.utils.project.org;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class ProjectStructureGenerator {
    
    private static final Pattern FOLDER_PATTERN = Pattern.compile("^(\\s*)├──\\s*(.+?)/?\\s*$");
    private static final Pattern LAST_FOLDER_PATTERN = Pattern.compile("^(\\s*)└──\\s*(.+?)/?\\s*$");
    private static final Pattern FILE_PATTERN = Pattern.compile("^(\\s*)├──\\s*(.+?\\.[a-zA-Z0-9]+)\\s*$");
    private static final Pattern LAST_FILE_PATTERN = Pattern.compile("^(\\s*)└──\\s*(.+?\\.[a-zA-Z0-9]+)\\s*$");
    
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
        return FOLDER_PATTERN.matcher(line).matches() ||
               LAST_FOLDER_PATTERN.matcher(line).matches() ||
               FILE_PATTERN.matcher(line).matches() ||
               LAST_FILE_PATTERN.matcher(line).matches();
    }
    
    private static int getIndentLevel(String line) {
        int spaces = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                spaces++;
            } else {
                break;
            }
        }
        return spaces;
    }
    
    private static String extractItemName(String line) {
        // Try different patterns to extract the item name
        var folderMatcher = FOLDER_PATTERN.matcher(line);
        if (folderMatcher.matches()) {
            return folderMatcher.group(2);
        }
        
        var lastFolderMatcher = LAST_FOLDER_PATTERN.matcher(line);
        if (lastFolderMatcher.matches()) {
            return lastFolderMatcher.group(2);
        }
        
        var fileMatcher = FILE_PATTERN.matcher(line);
        if (fileMatcher.matches()) {
            return fileMatcher.group(2);
        }
        
        var lastFileMatcher = LAST_FILE_PATTERN.matcher(line);
        if (lastFileMatcher.matches()) {
            return lastFileMatcher.group(2);
        }
        
        return null;
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
            Files.createFile(filePath);
            
            // Add basic content based on file type
            String fileName = filePath.getFileName().toString();
            String content = generateBasicContent(fileName);
            if (!content.isEmpty()) {
                Files.write(filePath, content.getBytes());
            }
        }
    }
    
    private static String generateBasicContent(String fileName) {
        if (fileName.endsWith(".java")) {
            String className = fileName.substring(0, fileName.lastIndexOf('.'));
            return String.format("public class %s {\n    // TODO: Implement class\n}\n", className);
        } else if (fileName.endsWith(".xml")) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- TODO: Add XML content -->\n";
        } else if (fileName.endsWith(".properties")) {
            return "# Configuration properties\n# TODO: Add properties\n";
        } else if (fileName.endsWith(".md")) {
            return "# " + fileName.substring(0, fileName.lastIndexOf('.')) + "\n\nTODO: Add documentation\n";
        } else if (fileName.endsWith(".gitignore")) {
            return "# IDE files\n.idea/\n*.iml\n\n# Build files\ntarget/\nbuild/\n\n# OS files\n.DS_Store\nThumbs.db\n";
        } else if (fileName.equals("pom.xml")) {
            return generatePomXml();
        } else if (fileName.equals("build.gradle")) {
            return generateBuildGradle();
        }
        return "";
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