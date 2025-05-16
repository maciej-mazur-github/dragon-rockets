package dragonrockets.mission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MissionSummary {
    private final List<Mission> missions;

    public MissionSummary(List<Mission> missions) {
        this.missions = missions;
    }

    public Optional<Mission> findMission(String missionName) {
        return Optional.empty();
    }

    public int getNumberOfMissions() {
        return 0;
    }

    public List<Mission> getMissions() {
        return new ArrayList<>();
    }
}
