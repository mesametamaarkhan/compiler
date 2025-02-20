import java.util.*;

public class DFAState {
    private static int stateCounter = 0;
    int stateId;
    Set<NFAState> nfaStateSet;
    boolean isAcceptState;
    Map<Character, DFAState> transitionMap;

    DFAState(Set<NFAState> nfaStateSet) {
        this.stateId = generateStateId();
        this.nfaStateSet = nfaStateSet;
        this.isAcceptState = checkIfAcceptingState(nfaStateSet);
        this.transitionMap = new HashMap<>();
    }

    private int generateStateId() {
        return stateCounter++;
    }

    private boolean checkIfAcceptingState(Set<NFAState> states) {
        for (NFAState state : states) {
            if (state.isAcceptState) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("State_").append(stateId).append(" [");
        result.append("Contains: ");

        List<Integer> stateIdList = new ArrayList<>();
        for (NFAState currentState : nfaStateSet) {
            stateIdList.add(currentState.stateId);
        }
        Collections.sort(stateIdList);

        result.append(String.join(",", stateIdList.stream()
                .map(String::valueOf)
                .toArray(String[]::new)));
        result.append("] Accept: ").append(isAcceptState);

        return result.toString();
    }
}