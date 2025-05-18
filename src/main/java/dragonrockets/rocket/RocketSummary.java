package dragonrockets.rocket;

public record RocketSummary(String name, String status) {
    @Override
    public String toString() {
        return String.format("\t\u25E6 %s - %s%n", name, status);
    }
}
