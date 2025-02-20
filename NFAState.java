import java.util.*;

public class NFAState {
    int stateId;
    boolean isAcceptState;
    Map<Character, List<NFAState>> transitionMap;

    NFAState(int stateId, boolean isAcceptState) {
        this.stateId = stateId;
        this.isAcceptState = isAcceptState;
        this.transitionMap = new HashMap<>();
    }

    void addStateTransition(char inputSymbol, NFAState targetState) {
        transitionMap.computeIfAbsent(inputSymbol, key -> new ArrayList<>())
                    .add(targetState);
    }
    
    // Optional helper methods following our pattern
    public int getStateId() {
        return stateId;
    }
    
    public boolean isAcceptingState() {
        return isAcceptState;
    }
    
    public Map<Character, List<NFAState>> getTransitions() {
        return transitionMap;
    }
}