package dragonrockets.rocket;

import dragonrockets.mission.Mission;

import java.util.Optional;

public class Rocket {
    private final String name;
    private RocketStatus status;
    private Mission lastMission;

    public Rocket(String name) {
        this.name = name;
        this.status = RocketStatus.ON_GROUND;
    }

    public String getName() {
        return name;
    }

    public RocketStatus getStatus() {
        return status;
    }

    public void setStatus(RocketStatus status) {
        this.status = status;
    }

    public Optional<Mission> getLastMission() {
        if (lastMission == null) {
            return Optional.empty();
        }
        return Optional.of(lastMission);
    }

    public void setLastMission(Mission lastMission) {
        this.lastMission = lastMission;
    }

    public static RocketSummary convertToRocketSummary(Rocket rocket) {
        return new RocketSummary(rocket.getName(), rocket.getStatus().getSummaryForm());
    }
}
