import java.util.*;

public class DFAState {
    private static int count = 0;
    int id;
    Set<NFAState> nfaStates;
    boolean isFinal;
    Map<Character, DFAState> transitions;

    DFAState(Set<NFAState> nfaStates) {
        this.id = count++;
        this.nfaStates = nfaStates;
        this.isFinal = nfaStates.stream().anyMatch(state -> state.isFinal);
        this.transitions = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DFAState ").append(id).append(" (");
        sb.append("NFA States: ");
        List<String> stateIds = new ArrayList<>();
        for (NFAState s : nfaStates) {
            stateIds.add(String.valueOf(s.id));
        }
        sb.append(String.join(", ", stateIds));
        sb.append(") Final: ").append(isFinal);
        return sb.toString();
    }
}
