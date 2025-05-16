package dragonrockets.exception;

public class RocketNotFoundException extends RuntimeException {
    public RocketNotFoundException(String message) {
        super(message);
    }
}
