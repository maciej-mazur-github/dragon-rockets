package dragonrockets.exception;

public class RocketAssignedToAnotherMissionException extends RuntimeException {
    public RocketAssignedToAnotherMissionException(String message) {
        super(message);
    }
}
