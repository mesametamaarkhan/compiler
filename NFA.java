import java.util.*;

public class NFA {
    NFAState startState;
    Set<NFAState> endStates;

    NFA(NFAState start, Set<NFAState> ends) {
        this.startState = start;
        this.endStates = ends;
    }
}
