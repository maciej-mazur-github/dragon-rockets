package dragonrockets.rocket;

public enum RocketStatus {
    ON_GROUND("On ground"),
    IN_SPACE("In space"),
    IN_REPAIR("In repair");

    private final String summaryForm;

    RocketStatus(String summaryForm) {
        this.summaryForm = summaryForm;
    }

    public String getSummaryForm() {
        return summaryForm;
    }
}
