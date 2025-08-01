package org.nrg.xnat.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DirectoryToJsonTreeConverter {

    // Node class to represent file/directory structure
    public class TreeNode {
        private String name;
        private String type; // "file" or "folder"
        private long size;
        private String lastModified;
        private String absolutePath;
        private List<TreeNode> children;

        public TreeNode(String name, String type, long size, String lastModified, String absolutePath) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.lastModified = lastModified;
            this.absolutePath = absolutePath;
            this.children = new ArrayList<>();
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }

        public String getAbsolutePath() { return absolutePath; }
        public void getAbsolutePath(String getAbsolutePath) { this.absolutePath = absolutePath; }

        public List<TreeNode> getChildren() { return children; }
        public void setChildren(List<TreeNode> children) { this.children = children; }

        public void addChild(TreeNode child) {
            this.children.add(child);
        }
    }

    private  TreeNode buildDirectoryTree(final Path path, final String rootNodeLabel) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

        String name = rootNodeLabel == null ? path.getFileName() != null ? path.getFileName().toString() : path.toString() : rootNodeLabel;
        String type = Files.isDirectory(path) ? "folder" : "file";
        String lastModified = attrs.lastModifiedTime().toString();
        String absolutePath = path.toAbsolutePath().toString();

        TreeNode node = new TreeNode(name, type, 0, lastModified, absolutePath);

        // If it's a directory, recursively process children and calculate total size
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                List<TreeNode> children = new ArrayList<>();
                long totalSize = 0;

                for (Path child : stream) {
                    try {
                        TreeNode childNode = buildDirectoryTree(child, null);
                        children.add(childNode);
                        totalSize += childNode.getSize();
                    } catch (IOException e) {
                        log.error("Error processing: " + child + " - " + e.getMessage());
                    }
                }

                // Sort children: directories first, then files, both alphabetically
                children.sort((a, b) -> {
                    if (a.getType().equals(b.getType())) {
                        return a.getName().compareToIgnoreCase(b.getName());
                    }
                    return a.getType().equals("folder") ? -1 : 1;
                });

                node.setChildren(children);
                node.setSize(totalSize);
            }
        } else {
            // For files, use the actual file size
            node.setSize(attrs.size());
        }
        return node;
    }

    public String toJson(final String directoryPath, final String parentNodeLabel) throws IOException {
        Path path = Paths.get(directoryPath);
        return toJson(buildDirectoryTree(path, parentNodeLabel));
    }

    /**
     * Converts TreeNode to JSON string
     */
    public String toJson(TreeNode root) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(root);
    }
}
