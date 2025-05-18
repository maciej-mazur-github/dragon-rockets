package dragonrockets.rocket;

import dragonrockets.mission.Manager;
import dragonrockets.mission.Mission;

import java.util.List;
import java.util.Optional;

public interface Repository {
    boolean contains(String rocketName);

    Optional<Rocket> findRocket(String rocketName);

    boolean addRocket(Rocket rocket);

    int getNumberOfRockets();

    void setRocketStatus(String rocketName, String missionName, RocketStatus newStatus, Manager missionManager);

    void removeRocket(Rocket rocket);

    void unassignAndRemoveAllRockets();

    List<Rocket> getRockets();

    void wipeOutRocketsLastMission(Mission mission);

    List<Rocket> findRocketsByMission(Mission mission);

//    List<Rocket> getRockets();
}
