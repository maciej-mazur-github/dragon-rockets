package dragonrockets.exception;

public class RocketNotAssignedToMissionException extends RuntimeException {
    public RocketNotAssignedToMissionException(String message) {
        super(message);
    }
}
