import java.util.*;

public class DFA {
    DFAState startState;
    Set<DFAState> remainingStates;

    DFA(DFAState start) {
        this.startState = start;
        this.remainingStates = new HashSet<>();
    }
}
