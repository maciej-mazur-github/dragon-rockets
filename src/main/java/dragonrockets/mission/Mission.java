package dragonrockets.mission;

import dragonrockets.rocket.Repository;
import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketRepository;

public class Mission {
    private final String name;
    private final Repository inSpaceRocketsRepository;
    private final Repository inRepairRocketsRepository;
    private MissionStatus status;

    public Mission(String name) {
        this.name = name;
        this.inSpaceRocketsRepository = new RocketRepository();
        this.inRepairRocketsRepository = new RocketRepository();
        this.status = MissionStatus.SCHEDULED;
    }

    public String getName() {
        return name;
    }

    public void addRegularRocket(Rocket rocket) {
        inSpaceRocketsRepository.addRocket(rocket);
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public Repository getInSpaceRocketsRepository() {
        return inSpaceRocketsRepository;
    }

    public Repository getInRepairRocketsRepository() {
        return inRepairRocketsRepository;
    }
}
