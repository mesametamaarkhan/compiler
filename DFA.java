import java.util.*;

public class DFA {
    DFAState initialState;
    Set<DFAState> stateCollection;

    DFA(DFAState initialState) {
        this.initialState = initialState;
        this.stateCollection = new HashSet<>();
    }

    // You might want to add methods to:
    public void addToStateCollection(DFAState state) {
        stateCollection.add(state);
    }

    public Set<DFAState> getStateCollection() {
        return stateCollection;
    }
}