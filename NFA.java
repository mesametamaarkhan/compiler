import java.util.*;

public class NFA {
    NFAState initialState;
    Set<NFAState> acceptingStateSet;

    NFA(NFAState initialState, Set<NFAState> acceptingStateSet) {
        this.initialState = initialState;
        this.acceptingStateSet = acceptingStateSet;
    }

    // Optional helper methods following our pattern
    public NFAState getInitialState() {
        return initialState;
    }

    public Set<NFAState> getAcceptingStates() {
        return acceptingStateSet;
    }
}