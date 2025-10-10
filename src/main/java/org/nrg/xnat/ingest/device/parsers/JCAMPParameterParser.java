package org.nrg.xnat.ingest.device.parsers;
import org.nrg.xnat.ingest.device.handler.DeviceFile;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Parser for JCAMP-DX parameter files from Bruker BioSpin MRI equipment
 */
public class JCAMPParameterParser {

    // Data structure to hold parsed parameters

    // Patterns for parsing different line types
    private static final Pattern HEADER_PATTERN = Pattern.compile("^##([A-Z]+)=(.*)$");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("^##\\$([A-Za-z_][A-Za-z0-9_]*)=(.*)$");
    private static final Pattern ARRAY_SIZE_PATTERN = Pattern.compile("^\\(\\s*(\\d+(?:\\s*,\\s*\\d+)*)\\s*\\)$");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^\\$\\$(.*)$");
    private static final Pattern VISIBILITY_PATTERN = Pattern.compile("^\\$\\$ @vis=(.*)$");
    private static final Pattern STRING_VALUE_PATTERN = Pattern.compile("^<(.*)>$");
    private static final Pattern TUPLE_PATTERN = Pattern.compile("^\\(([^)]+)\\)$");

    /**
     * Parse a JCAMP parameter file
     */
    public static ParameterFile parseFile(String filename) throws IOException {
        return parseFile(new File(filename));
    }

    public static ParameterFile parseFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return parse(reader);
        }
    }

    public static ParameterFile parseFile(DeviceFile deviceFile) throws IOException {
        return parseFile(deviceFile.getFilePath().toFile());
    }

    public static ParameterFile parseString(String content) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            return parse(reader);
        }
    }

    private static ParameterFile parse(BufferedReader reader) throws IOException {
        ParameterFile paramFile = new ParameterFile();
        String line;
        String currentParam = null;
        List<String> continuationLines = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Skip empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Handle comments
            Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
            if (commentMatcher.matches()) {
                String comment = commentMatcher.group(1).trim();

                // Check if it's a visibility comment
                if (comment.startsWith("@vis=")) {
                    paramFile.addComment("VISIBILITY: " + comment.substring(5).trim());
                } else {
                    paramFile.addComment(comment);
                }
                continue;
            }

            // Handle headers (##HEADER=value)
            Matcher headerMatcher = HEADER_PATTERN.matcher(line);
            if (headerMatcher.matches()) {
                String key = headerMatcher.group(1);
                String value = headerMatcher.group(2);

                // Special handling for END marker
                if ("END".equals(key)) {
                    break;
                }

                paramFile.addHeader(key, parseValue(value));
                continue;
            }

            // Handle parameters (##$PARAM=value)
            Matcher paramMatcher = PARAMETER_PATTERN.matcher(line);
            if (paramMatcher.matches()) {
                // If we were processing a multi-line parameter, finish it first
                if (currentParam != null) {
                    finishMultiLineParameter(paramFile, currentParam, continuationLines);
                }

                String paramName = paramMatcher.group(1);
                String paramValue = paramMatcher.group(2);

                // Check if this is an array parameter
                Matcher arraySizeMatcher = ARRAY_SIZE_PATTERN.matcher(paramValue);
                if (arraySizeMatcher.matches()) {
                    // This is a multi-line array parameter
                    currentParam = paramName;
                    continuationLines.clear();
                    continuationLines.add(paramValue); // Store the size specification
                } else {
                    // Single-line parameter
                    paramFile.addParameter(paramName, parseValue(paramValue));
                }
                continue;
            }

            // Handle continuation lines for multi-line parameters
            if (currentParam != null) {
                continuationLines.add(line);
            }
        }

        // Handle any remaining multi-line parameter
        if (currentParam != null) {
            finishMultiLineParameter(paramFile, currentParam, continuationLines);
        }

        return paramFile;
    }

    private static void finishMultiLineParameter(ParameterFile paramFile, String paramName, List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }

        String sizeSpec = lines.get(0);
        Matcher sizeMatcher = ARRAY_SIZE_PATTERN.matcher(sizeSpec);

        if (sizeMatcher.matches()) {
            String[] dimensions = sizeMatcher.group(1).split("\\s*,\\s*");
            int totalSize = 1;
            for (String dim : dimensions) {
                totalSize *= Integer.parseInt(dim.trim());
            }

            // Collect all values from continuation lines
            List<Object> values = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                Object value = parseValue(line);

                if (value instanceof List) {
                    values.addAll((List<?>) value);
                } else {
                    values.add(value);
                }
            }

            paramFile.addParameter(paramName, values);
        }
    }

    private static Object parseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        value = value.trim();

        // Handle string values in angle brackets
        Matcher stringMatcher = STRING_VALUE_PATTERN.matcher(value);
        if (stringMatcher.matches()) {
            return stringMatcher.group(1);
        }

        // Handle tuples (parentheses with comma-separated values)
        Matcher tupleMatcher = TUPLE_PATTERN.matcher(value);
        if (tupleMatcher.matches()) {
            String content = tupleMatcher.group(1);
            String[] parts = content.split("\\s*,\\s*");

            List<Object> tupleValues = new ArrayList<>();
            for (String part : parts) {
                tupleValues.add(parseSimpleValue(part.trim()));
            }
            return tupleValues;
        }

        // Handle space-separated values
        if (value.contains(" ") && !value.startsWith("(") && !value.startsWith("<")) {
            String[] parts = value.split("\\s+");
            if (parts.length > 1) {
                List<Object> listValues = new ArrayList<>();
                for (String part : parts) {
                    listValues.add(parseSimpleValue(part.trim()));
                }
                return listValues;
            }
        }

        return parseSimpleValue(value);
    }

    private static Object parseSimpleValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        // Try to parse as integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Not an integer
        }

        // Try to parse as double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Not a double
        }

        // Check for boolean-like values
        if ("Yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("No".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return false;
        }

        // Return as string
        return value;
    }

    // Utility methods for accessing parsed data
    public static void printParameterFile(ParameterFile paramFile) {
        System.out.println("=== JCAMP Parameter File ===");

        System.out.println("\nHeaders:");
        for (Map.Entry<String, Object> entry : paramFile.getHeaders().entrySet()) {
            System.out.printf("  %s = %s%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nParameters:");
        for (Map.Entry<String, Object> entry : paramFile.getParameters().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (list.size() > 5) {
                    System.out.printf("  %s = [%s, %s, %s, ... (%d items)]%n",
                            entry.getKey(), list.get(0), list.get(1), list.get(2), list.size());
                } else {
                    System.out.printf("  %s = %s%n", entry.getKey(), value);
                }
            } else {
                System.out.printf("  %s = %s%n", entry.getKey(), value);
            }
        }

        if (!paramFile.getComments().isEmpty()) {
            System.out.println("\nComments:");
            for (String comment : paramFile.getComments()) {
                System.out.println("  " + comment);
            }
        }
    }

}
