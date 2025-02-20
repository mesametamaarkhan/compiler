public class Token {
    String type, value;
    int line;

    public Token(String type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    @Override
    public String toString() {
        return "<" + type + ", " + value + ">";
    }
}