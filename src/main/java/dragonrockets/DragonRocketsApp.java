package dragonrockets;

import dragonrockets.mission.Mission;
import dragonrockets.mission.MissionStatus;
import dragonrockets.mission.MissionSummary;
import dragonrockets.rocket.Repository;
import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DragonRocketsApp {

    public boolean addNewRocketToRepository(String rocketName) {
        return false;
    }

    public boolean addNewMission(String missionName) {
        return false;
    }

    public boolean assignRocketToMission(String rocketName, String missionName) {
        return false;
    }

    public void setRocketStatus(String rocketName, String missionName, RocketStatus newStatus) {

    }

    public boolean setMissionStatus(String missionName, MissionStatus newStatus) {
        return false;
    }

    public Repository getRocketRepository() {
        return new Repository() {
            @Override
            public boolean contains(String rocketName) {
                return false;
            }

            @Override
            public Optional<Rocket> findRocket(String rocketName) {
                return Optional.empty();
            }
        };
    }

    public MissionSummary getMissionSummary() {
        return new MissionSummary(new ArrayList<>());
    }

    public void printMissionSummary() {

    }
}
