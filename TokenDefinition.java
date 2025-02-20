import java.util.regex.Pattern;

public class TokenDefinition {
    private final String type;
    private final Pattern pattern;

    public TokenDefinition(String type, String regex) {
        this.type = type;
        this.pattern = Pattern.compile(regex);
    }

    public String getType() { return type; }
    public Pattern getPattern() { return pattern; }
}
