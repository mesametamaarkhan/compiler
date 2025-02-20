import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LexicalAnalyzer {
    private static final Map<String, String> symbolRegistry = new HashMap<>();
    private static final Set<String> MATH_OPERATORS = Set.of("+", "-", "*", "/", "%", "=");
    private static final Set<String> RESERVED_WORDS = Set.of("faal", "haraf", "ishariyah", "adad");

    private static NFA makeIntNFA() {
        NFAState start = new NFAState(0, false);
        NFAState accept = new NFAState(1, true);
        for (char digit = '0'; digit <= '9'; digit++) {
            start.addStateTransition(digit, accept);
            accept.addStateTransition(digit, accept);
        }
        return new NFA(start, Set.of(accept));
    }

    private static NFA makeDecNFA() {
        NFAState start = new NFAState(0, false);
        NFAState decimal = new NFAState(1, false);
        NFAState accept = new NFAState(2, true);

        for (char digit = '0'; digit <= '9'; digit++) {
            start.addStateTransition(digit, start);
            decimal.addStateTransition(digit, accept);
            accept.addStateTransition(digit, accept);
        }
        start.addStateTransition('.', decimal);
        return new NFA(start, Set.of(accept));
    }

    private static DFA NFAtoDFA(NFA nfa) {
        Map<Set<NFAState>, DFAState> stateMapping = new HashMap<>();
        Queue<Set<NFAState>> queue = new LinkedList<>();

        Set<NFAState> initialSet = Set.of(nfa.initialState);
        DFAState initialDFAState = new DFAState(initialSet);
        stateMapping.put(initialSet, initialDFAState);
        queue.add(initialSet);

        while (!queue.isEmpty()) {
            Set<NFAState> currentSet = queue.poll();
            DFAState currentDFAState = stateMapping.get(currentSet);
            Map<Character, Set<NFAState>> transitionMap = new HashMap<>();

            for (NFAState state : currentSet) {
                for (Map.Entry<Character, List<NFAState>> entry : state.transitionMap.entrySet()) {
                    char symbol = entry.getKey();
                    transitionMap.putIfAbsent(symbol, new HashSet<>());
                    transitionMap.get(symbol).addAll(entry.getValue());
                }
            }

            for (Map.Entry<Character, Set<NFAState>> entry : transitionMap.entrySet()) {
                char symbol = entry.getKey();
                Set<NFAState> targetSet = entry.getValue();

                stateMapping.putIfAbsent(targetSet, new DFAState(targetSet));
                currentDFAState.transitionMap.put(symbol, stateMapping.get(targetSet));

                if (!stateMapping.containsKey(targetSet)) {
                    queue.add(targetSet);
                }
            }
        }
        return new DFA(initialDFAState);
    }

    private static void analyzeTokens(String filePath) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
        String codeLine;

        while ((codeLine = fileReader.readLine()) != null) {
            int length = codeLine.length();
            int index = 0;

            while (index < length) {
                char currentChar = codeLine.charAt(index);
                if (Character.isWhitespace(currentChar)) {
                    index++;
                    continue;
                }
                StringBuilder tokenBuffer = new StringBuilder();

                if (Character.isLetter(currentChar)) {
                    while (index < length && (Character.isLetterOrDigit(codeLine.charAt(index)) || codeLine.charAt(index) == '_')) {
                        tokenBuffer.append(codeLine.charAt(index++));
                    }
                    classifyToken(tokenBuffer.toString());
                }
                else if (Character.isDigit(currentChar)) {
                    boolean hasDecimal = false;
                    while (index < length && (Character.isDigit(codeLine.charAt(index)) || codeLine.charAt(index) == '.')) {
                        if (codeLine.charAt(index) == '.') hasDecimal = true;
                        tokenBuffer.append(codeLine.charAt(index++));
                    }
                    classifyToken(tokenBuffer.toString());
                }
                else {
                    tokenBuffer.append(currentChar);
                    index++;
                    classifyToken(tokenBuffer.toString());
                }
            }
        }
        fileReader.close();
    }

    private static void displaySymbolRegistry() {
        System.out.println("\n--- Symbol Registry ---");
        symbolRegistry.forEach((key, value) -> System.out.println("| " + key + " | Type: " + value + " |"));
    }

    private static void classifyToken(String token) {
        if (RESERVED_WORDS.contains(token)) {
            System.out.println("[Keyword] -> " + token);
        } else if (MATH_OPERATORS.contains(token)) {
            System.out.println("[Operator] -> " + token);
        } else if (token.matches("\\d+")) {
            System.out.println("[Integer] -> " + token);
        } else if (token.matches("\\d+\\.\\d+")) {
            System.out.println("[Decimal] -> " + token);
        } else {
            symbolRegistry.putIfAbsent(token, "auto");
            System.out.println("[Identifier] -> " + token + " (Auto Declared)");
        }
    }

    private static void renderStateTable(DFA dfa) {
        System.out.println("\n--- DFA Transition Table ---");
        Set<DFAState> visited = new HashSet<>();
        Queue<DFAState> queue = new LinkedList<>();
        queue.add(dfa.initialState);
        visited.add(dfa.initialState);

        while (!queue.isEmpty()) {
            DFAState state = queue.poll();
            System.out.print("State " + state.nfaStateSet + " (Final: " + state.isAcceptState + ") -> ");
            for (Map.Entry<Character, DFAState> entry : state.transitionMap.entrySet()) {
                System.out.print("[" + entry.getKey() + " -> " + entry.getValue().nfaStateSet + "] ");
                if (!visited.contains(entry.getValue())) {
                    visited.add(entry.getValue());
                    queue.add(entry.getValue());
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Processing file: example.bro\n");

        NFA intNFA = makeIntNFA();
        NFA decimalNFA = makeDecNFA();

        DFA intDFA = NFAtoDFA(intNFA);
        DFA decimalDFA = NFAtoDFA(decimalNFA);

        analyzeTokens("example.bro");
        renderStateTable(intDFA);
        renderStateTable(decimalDFA);
        displaySymbolRegistry();
    }
}