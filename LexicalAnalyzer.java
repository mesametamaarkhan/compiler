import java.util.*;
import java.util.regex.*;

public class LexicalAnalyzer {
    private static final List<TokenDefinition> TOKEN_DEFINITIONS = List.of(
            new TokenDefinition("KEYWORD", "^(boolean|integer|decimal|char|const)\\b"),
            new TokenDefinition("IDENTIFIER", "^[a-zA-Z_][a-zA-Z_0-9]*"),
            new TokenDefinition("NUMBER", "^\\d+(\\.\\d{1,5})?"),
            new TokenDefinition("OPERATOR", "^[+\\-*/%]"),
            new TokenDefinition("ASSIGNMENT", "^="),
            new TokenDefinition("SEMICOLON", "^;")
    );

    public List<Token> tokenize(String input) {
        input = preprocess(input); // Remove comments and trim spaces
        List<Token> tokens = new ArrayList<>();
        int index = 0;

        while (index < input.length()) {
            boolean matched = false;

            for (TokenDefinition definition : TOKEN_DEFINITIONS) {
                Matcher matcher = definition.getPattern().matcher(input.substring(index));
                if (matcher.find()) {
                    String matchedText = matcher.group();
                    tokens.add(new Token(definition.getType(), matchedText));
                    index += matchedText.length();
                    matched = true;
                    break; // Move to next token
                }
            }

            if (!matched) {
                System.out.println("Unrecognized token at index " + index + ": " + input.charAt(index));
                index++; // Skip unrecognized character
            }
        }

        return tokens;
    }

    private String preprocess(String input) {
        input = input.replaceAll("//.*|/\\*.*?\\*/", ""); // Remove comments
        return input.replaceAll("\\s+", " ").trim(); // Normalize spaces
    }

    public static void main(String[] args) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        String code = "boolean flag = true; integer num = 100; // Example code";
        List<Token> tokens = lexer.tokenize(code);
        System.out.println("Tokens:");
        tokens.forEach(System.out::println);
    }
}




