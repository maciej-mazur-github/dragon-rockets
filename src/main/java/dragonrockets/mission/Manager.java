package dragonrockets.mission;

import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketStatus;

import java.util.List;

public interface Manager {
    boolean addMission(String missionName);

    boolean containsMission(String missionName);

    void assignRocketToMission(Rocket rocket, String missionName);

    void setRocketStatus(Rocket rocket, String missionName, RocketStatus newStatus);

    boolean setMissionStatus(String missionName, MissionStatus newStatus);

    List<Mission> getMissions();
}
