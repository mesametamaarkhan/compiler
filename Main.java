import java.util.List;

public class Main {
    public static void main(String[] args) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();

        // Hardcoded test input
        String code = "boolean flag = true; integer num = 100;";

        // Tokenize the input
        List<Token> tokens = lexer.tokenize(code);

        // Print the tokens
        System.out.println("Tokens:");
        tokens.forEach(System.out::println);
    }
}
