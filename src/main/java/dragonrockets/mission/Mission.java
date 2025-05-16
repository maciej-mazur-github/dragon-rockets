package dragonrockets.mission;

import dragonrockets.rocket.Repository;
import dragonrockets.rocket.Rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mission {
    private String name;
    private Repository repository;
    private MissionStatus status;

    public String getName() {
        return null;
    }

    public boolean containsRocket(String rocketName) {
        return repository.contains(rocketName);
    }

    public MissionStatus getStatus() {
        return status;
    }

    public int getNumberOfRocketsNotInRepair() {
        return 0;
    }

    public Optional<Rocket> findRocket(String rocketName) {
        return Optional.empty();
    }

    public List<Rocket> getRockets() {
        return new ArrayList<>();
    }
}
