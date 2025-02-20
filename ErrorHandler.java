public class ErrorHandler {
    public void reportError(String message, int line) {
        System.out.println("Error on line " + line + ": " + message);
    }
}