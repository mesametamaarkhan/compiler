import java.util.List;

public class Main {
    public static void main(String[] args) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        String code = "integer num = 100;\n" +
                "output \"Hello, World\";\n" +
                "// This is a comment\n" +
                "boolean flag = true;";

        List<Token> tokens = lexer.tokenize(code);
        System.out.println("Tokens:");
        tokens.forEach(System.out::println);

        lexer.getSymbolTable().printTable();
    }
}
