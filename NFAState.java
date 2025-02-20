import java.util.*;

public class NFAState {
    int id;
    boolean isFinal;
    Map<Character, List<NFAState>> transitions;

    NFAState(int id, boolean isFinal) {
        this.id = id;
        this.isFinal = isFinal;
        this.transitions = new HashMap<>();
    }

    void addTransition(char symbol, NFAState target) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(target);
    }
}
