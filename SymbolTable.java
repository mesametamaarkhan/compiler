import java.util.*;

public class SymbolTable {
    private final Map<String, String> symbols = new HashMap<>();

    public void add(String name, String type) {
        symbols.put(name, type);
    }

    public boolean exists(String name) {
        return symbols.containsKey(name);
    }

    public void printTable() {
        System.out.println("Symbol Table:");
        symbols.forEach((key, value) -> System.out.println(key + " -> " + value));
    }
}