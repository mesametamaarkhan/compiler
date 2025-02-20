import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LexicalAnalyzer {
    private static final Map<String, String> symbolTable = new HashMap<>();
    private static final Set<String> KEYWORDS = Set.of(
            "yo", "ghalat", "dekha", "dosra", "apna" , "is", "sun"
    );

    private static final Set<String> OPERATORS = Set.of(
            "+",
            "-",
            "*",
            "/",
            "%",
            "=",
            "==",
            ">",
            "<",
            ">=",
            "<="
    );

    private static NFA buildIntegerNFA() {
        NFAState s0 = new NFAState(0, false);
        NFAState s1 = new NFAState(1, true);
        for (char ch = '0'; ch <= '9'; ch++) {
            s0.addTransition(ch, s1);
            s1.addTransition(ch, s1);
        }
        return new NFA(s0, Set.of(s1));
    }

    private static NFA buildDecimalNFA() {
        NFAState s0 = new NFAState(0, false);
        NFAState s1 = new NFAState(1, false);
        NFAState s2 = new NFAState(2, true);

        for (char c = '0'; c <= '9'; c++) {
            s0.addTransition(c, s0);
            s1.addTransition(c, s2);
            s2.addTransition(c, s2);
        }
        s0.addTransition('.', s1);
        return new NFA(s0, Set.of(s2));
    }

    private static DFA convertNFAtoDFA(NFA nfa) {
        Map<Set<NFAState>, DFAState> mapping = new HashMap<>();
        Queue<Set<NFAState>> queue = new LinkedList<>();

        Set<NFAState> startSet = Set.of(nfa.startState);
        DFAState startDFAState = new DFAState(startSet);
        mapping.put(startSet, startDFAState);
        queue.add(startSet);

        while (!queue.isEmpty()) {
            Set<NFAState> currentSet = queue.poll();
            DFAState currentDFAState = mapping.get(currentSet);

            Map<Character, Set<NFAState>> transitions = new HashMap<>();

            for (NFAState state : currentSet) {
                for (Map.Entry<Character, List<NFAState>> entry : state.transitions.entrySet()) {
                    char symbol = entry.getKey();
                    transitions.putIfAbsent(symbol, new HashSet<>());
                    transitions.get(symbol).addAll(entry.getValue());
                }
            }

            for (Map.Entry<Character, Set<NFAState>> entry : transitions.entrySet()) {
                char symbol = entry.getKey();
                Set<NFAState> targetSet = entry.getValue();

                mapping.putIfAbsent(targetSet, new DFAState(targetSet));
                currentDFAState.transitions.put(symbol, mapping.get(targetSet));

                if (!mapping.containsKey(targetSet)) {
                    queue.add(targetSet);
                }
            }
        }
        return new DFA(startDFAState);
    }

    private static void tokenizeFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            int length = line.length();
            int i = 0;

            while (i < length) {
                char ch = line.charAt(i);

                if (Character.isWhitespace(ch)) {
                    i++;
                    continue;
                }

                StringBuilder token = new StringBuilder();

                if (Character.isLetter(ch)) {
                    // Identifier or keyword
                    while (i < length && (Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '_')) {
                        token.append(line.charAt(i));
                        i++;
                    }
                    processToken(token.toString());
                } else if (Character.isDigit(ch)) {
                    // Number (integer or decimal)
                    boolean isDecimal = false;
                    while (i < length && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.')) {
                        if (line.charAt(i) == '.') {
                            isDecimal = true;
                        }
                        token.append(line.charAt(i));
                        i++;
                    }
                    processToken(token.toString());
                } else if (ch == '\'' || ch == '\"') {
                    // Character or string literal
                    char quoteType = ch;
                    token.append(ch);
                    i++;
                    while (i < length && line.charAt(i) != quoteType) {
                        token.append(line.charAt(i));
                        i++;
                    }
                    if (i < length) {
                        token.append(quoteType); // Closing quote
                        i++;
                    }
                    processToken(token.toString());
                } else if (ch == '#' && i + 2 < length && line.charAt(i + 1) == '#' && line.charAt(i + 2) == '#') {
                    // Single-line comment
                    token.append("###");
                    i += 3;
                    while (i < length && line.charAt(i) != '\n') {
                        token.append(line.charAt(i));
                        i++;
                    }
                    processToken(token.toString());
                } else if (ch == '.' && i + 1 < length && line.charAt(i + 1) == '+') {
                    // Multi-line comment
                    token.append(".+");
                    i += 2;
                    while (i < length && !(line.charAt(i) == '+' && i + 1 < length && line.charAt(i + 1) == '.')) {
                        token.append(line.charAt(i));
                        i++;
                    }
                    if (i < length) {
                        token.append("+.");
                        i += 2;
                    }
                    processToken(token.toString());
                } else {
                    // Operator or unknown token
                    token.append(ch);
                    i++;
                    processToken(token.toString());
                }
            }
        }
        reader.close();
    }

    private static void printSymbolTable() {
        System.out.println("Symbol Table Contents:");
        for (Map.Entry<String, String> entry : symbolTable.entrySet()) {
            System.out.println("Variable: " + entry.getKey() + " | Type: " + entry.getValue());
        }
    }

    private static void processToken(String token) {
        if (KEYWORDS.contains(token)) {
            System.out.println("[KEYWORD: " + token + "]");
        } else if (OPERATORS.contains(token)) {
            System.out.println("[OPERATOR: " + token + "]");
        } else if (token.matches("\\d+")) {
            System.out.println("[INTEGER: " + token + "]");
        } else if (token.matches("\\d+\\.\\d+")) {
            System.out.println("[DECIMAL: " + token + "]");
        } else if (token.startsWith("\"") && token.endsWith("\"")) {
            System.out.println("[STRING_LITERAL: " + token + "]");
        } else if (token.startsWith("'") && token.endsWith("'") && token.length() == 3) {
            System.out.println("[CHAR_LITERAL: " + token + "]");
        } else if (token.startsWith("###")) {
            System.out.println("[COMMENT: " + token + "]");
        } else if (token.startsWith(".+") && token.endsWith("+.")) {
            System.out.println("[MULTI_LINE_COMMENT: " + token + "]");
        } else {
            // For an identifier, check for implicit declaration.
            if (!symbolTable.containsKey(token)) {
                // For simplicity, we'll assume a default type.
                // In a real system, you'd infer type based on context.
                symbolTable.put(token, "auto");
                System.out.println("[IMPLICIT DECLARATION: " + token + " of type auto]");
            }
            System.out.println("[IDENTIFIER: " + token + "]");
        }
    }
    private static void printTransitionTable(DFA dfa) {
        System.out.println("DFA Transition Table:");
        // Use a set to keep track of visited DFAStates
        Set<DFAState> visited = new HashSet<>();
        Queue<DFAState> queue = new LinkedList<>();
        queue.add(dfa.startState);
        visited.add(dfa.startState);

        while (!queue.isEmpty()) {
            DFAState state = queue.poll();
            System.out.print("State " + state.nfaStates + " (Final: " + state.isFinal + ") -> ");
            for (Map.Entry<Character, DFAState> entry : state.transitions.entrySet()) {
                System.out.print("[" + entry.getKey() + " -> " + entry.getValue().nfaStates + "] ");
                if (!visited.contains(entry.getValue())) {
                    visited.add(entry.getValue());
                    queue.add(entry.getValue());
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Tokenizing 'example.bro'...");

        NFA integerNFA = buildIntegerNFA();
        NFA decimalNFA = buildDecimalNFA();

        DFA integerDFA = convertNFAtoDFA(integerNFA);
        DFA decimalDFA = convertNFAtoDFA(decimalNFA);

        tokenizeFile("example.bro");
        printTransitionTable(integerDFA);
        printSymbolTable();
    }
}