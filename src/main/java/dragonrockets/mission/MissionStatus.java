package dragonrockets.mission;

public enum MissionStatus {
    SCHEDULED("Scheduled"),
    PENDING("Pending"),
    IN_PROGRESS("In progress"),
    ENDED("Ended");

    private final String summaryForm;

    MissionStatus(String summaryForm) {
        this.summaryForm = summaryForm;
    }

    public String getSummaryForm() {
        return summaryForm;
    }
}
