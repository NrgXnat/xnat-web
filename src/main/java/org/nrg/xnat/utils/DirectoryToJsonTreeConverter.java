package org.nrg.xnat.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    public class TreeNode {
        private String name;
        private String type; // "file" or "folder"
        private long size;
        private String absolutePath;
        private List<TreeNode> children;

        public TreeNode(String name, String type, long size, String absolutePath) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.absolutePath = absolutePath;
            this.children = new ArrayList<>();
        }
    }

    private  TreeNode buildDirectoryTree(final Path path, final String rootNodeLabel) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

        String name = rootNodeLabel == null ? path.getFileName() != null ? path.getFileName().toString() : path.toString() : rootNodeLabel;
        String type = Files.isDirectory(path) ? "folder" : "file";
        String absolutePath = path.toAbsolutePath().toString();

        TreeNode node = new TreeNode(name, type, 0, absolutePath);

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
                        log.error("Error processing: {} - {}", child, e.getMessage());
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
        TreeNode rootNode = buildDirectoryTree(path, parentNodeLabel);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(rootNode);
    }}