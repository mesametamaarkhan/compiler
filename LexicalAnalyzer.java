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
        input = preprocess(input); // Remove comments and trim spaces
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        int line = 1;

        while (index < input.length()) {
            char currentChar = input.charAt(index);

            // Handle Newline Character
            if (currentChar == '\n') {
                line++;
                index++;
                continue; // Move to next character
            }

            boolean matched = false;

            for (TokenDefinition definition : TOKEN_DEFINITIONS) {
                Matcher matcher = definition.getPattern().matcher(input.substring(index));

                if (matcher.find()) {
                    String matchedText = matcher.group();

                    // Skip comments, but keep track of newlines inside them
                    if (definition.getType().equals("COMMENT")) {
                        index += matchedText.length();
                        matched = true;
                        break;
                    }

                    // Add token with correct line number
                    tokens.add(new Token(definition.getType(), matchedText, line));

                    // Track Identifiers in Symbol Table
                    if (definition.getType().equals("IDENTIFIER")) {
                        symbolTable.add(matchedText, "UNKNOWN");
                    }

                    index += matchedText.length();
                    matched = true;
                    break;
                }
            }

            // Handle Unrecognized Characters
            if (!matched) {
                if (!Character.isWhitespace(currentChar)) { // Ignore spaces
                    System.out.println("Error on line " + line + ": Unrecognized token: " + currentChar);
                }
                index++;
            }
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