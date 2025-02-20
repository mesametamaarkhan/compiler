import java.util.*;
import java.util.regex.*;

public class LexicalAnalyzer {
    private static final List<TokenDefinition> TOKEN_DEFINITIONS = List.of(
            new TokenDefinition("KEYWORD", "^(boolean|integer|decimal|char|const|input|output|true|false)\\b"),
            new TokenDefinition("IDENTIFIER", "^[a-zA-Z_][a-zA-Z_0-9]*"),
            new TokenDefinition("NUMBER", "^\\d+(\\.\\d{1,5})?"),
            new TokenDefinition("STRING", "^\".*?\""),
            new TokenDefinition("OPERATOR", "^[+\\-*/%]"),
            new TokenDefinition("ASSIGNMENT", "^="),
            new TokenDefinition("SEMICOLON", "^;"),
            new TokenDefinition("COMMENT", "^//.*|^/\\*.*?\\*/")
    );

    private final SymbolTable symbolTable = new SymbolTable();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<Token> tokenize(String input) {
        input = preprocess(input);
        List<Token> tokens = new ArrayList<>();
        String[] lines = input.split("\n");
        int lineNumber = 1;
        String lastSeenType = null;  // Track datatype for identifiers

        for (String line : lines) {
            int index = 0;
            line = line.trim();

            while (index < line.length()) {
                boolean matched = false;

                for (TokenDefinition definition : TOKEN_DEFINITIONS) {
                    Matcher matcher = definition.getPattern().matcher(line.substring(index));

                    if (matcher.find()) {
                        String matchedText = matcher.group();

                        // Skip comments entirely
                        if (definition.getType().equals("COMMENT")) {
                            index += matchedText.length();
                            matched = true;
                            break;
                        }

                        // Assign identifier types correctly
                        if (definition.getType().equals("KEYWORD") &&
                                (matchedText.equals("integer") || matchedText.equals("boolean") ||
                                        matchedText.equals("decimal") || matchedText.equals("char"))) {
                            lastSeenType = matchedText;  // Track datatype
                        } else if (definition.getType().equals("IDENTIFIER") && lastSeenType != null) {
                            symbolTable.add(matchedText, lastSeenType);  // Store correct type
                            lastSeenType = null;  // Reset after storing
                        } else if (definition.getType().equals("IDENTIFIER")) {
                            symbolTable.add(matchedText, "UNKNOWN");  // Default to UNKNOWN
                        }

                        // Add token with correct line number
                        tokens.add(new Token(definition.getType(), matchedText, lineNumber));

                        index += matchedText.length();
                        matched = true;
                        break;
                    }
                }

                // Handle Unrecognized Characters
                if (!matched) {
                    if (!Character.isWhitespace(line.charAt(index))) {
                        System.out.println("Error on line " + lineNumber + ": Unrecognized token: " + line.charAt(index));
                    }
                    index++;
                }
            }

            lineNumber++;
        }

        return tokens;
    }


    private String preprocess(String input) {
        return input.replaceAll("\r", "")  // Remove carriage returns (important for Windows)
                .replaceAll("\t", " ")  // Normalize tabs into spaces
                .replaceAll("//.*|/\\*.*?\\*/", "")  // Remove comments
                .replaceAll("\\s+", " ")  // Convert multiple spaces to one
                .trim();  // Remove leading/trailing spaces
    }


}