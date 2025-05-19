package dragonrockets.mission;

import dragonrockets.exception.MissionNotFoundException;
import dragonrockets.rocket.Repository;
import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionManager implements Manager {
    private final Map<String, Mission> missions;
    private final Repository mainRepository;

    public MissionManager(Repository mainRepository) {
        this.mainRepository = mainRepository;
        this.missions = new HashMap<>();
    }

    @Override
    public boolean addMission(String missionName) {
        if (!missions.containsKey(missionName)) {
            missions.put(missionName, new Mission(missionName));
            return true;
        }
        return false;
    }

    @Override
    public boolean containsMission(String missionName) {
        return missions.containsKey(missionName);
    }

    @Override
    public void assignRocketToMission(Rocket rocket, String missionName) {
        Mission mission = missions.get(missionName);

        // Final status will depend on the original status - ON_GROUND rocket will be set to IN_SPACE,
        // IN_REPAIR rocket (only the one that was grounded after being set IN_REPAIR)
        RocketStatus desiredStatus = rocket.getStatus() == RocketStatus.ON_GROUND ? RocketStatus.IN_SPACE : RocketStatus.IN_REPAIR;
        setRocketStatus(rocket, missionName, desiredStatus);
        rocket.setLastMission(mission);
        mission.addRegularRocket(rocket);
    }

    @Override
    public void setRocketStatus(Rocket rocket, String missionName, RocketStatus newStatus) {
        RocketStatus oldStatus = rocket.getStatus();
        rocket.setStatus(newStatus);

        Mission mission = missions.get(missionName);

        if (newStatus == RocketStatus.IN_REPAIR) {
            mission.setStatus(MissionStatus.PENDING);
            mission.getInSpaceRocketsRepository().removeRocket(rocket);
            mission.getInRepairRocketsRepository().addRocket(rocket);
        } else if (newStatus == RocketStatus.IN_SPACE && oldStatus == RocketStatus.ON_GROUND) {
            mission.getInSpaceRocketsRepository().addRocket(rocket);
            if (mission.getInRepairRocketsRepository().getNumberOfRockets() == 0) {
                mission.setStatus(MissionStatus.IN_PROGRESS);
            }
        } else if (newStatus == RocketStatus.IN_SPACE && oldStatus == RocketStatus.IN_REPAIR) {
            mission.getInRepairRocketsRepository().removeRocket(rocket);
            mission.getInSpaceRocketsRepository().addRocket(rocket);
            if (mission.getInRepairRocketsRepository().getNumberOfRockets() == 0) {
                mission.setStatus(MissionStatus.IN_PROGRESS);
            }
        } else if (newStatus == RocketStatus.ON_GROUND) {
            // This rocket must be in one of below sub-repositories, so to avoid iterative search
            // the removal is called for both sub-repositories
            mission.getInRepairRocketsRepository().removeRocket(rocket);
            mission.getInSpaceRocketsRepository().removeRocket(rocket);

            if (mission.getInSpaceRocketsRepository().getNumberOfRockets() == 0
                    && mission.getInRepairRocketsRepository().getNumberOfRockets() == 0) {

                setMissionStatus(missionName, MissionStatus.SCHEDULED);
                mainRepository.wipeOutRocketsLastMission(mission);
            }
        }
    }

    @Override
    public boolean setMissionStatus(String missionName, MissionStatus newStatus) {

        if (!missions.containsKey(missionName)) {
            throw new MissionNotFoundException("Mission '" + missionName + "' does not exist");
        }

        // Mission can turn IN_PROGRESS or PENDING only by proper rocket management scenarios
        if (newStatus == MissionStatus.IN_PROGRESS || newStatus == MissionStatus.PENDING) {
            return false;
        }

        Mission mission = missions.get(missionName);
        MissionStatus oldStatus = mission.getStatus();

        if (oldStatus == MissionStatus.ENDED || oldStatus == newStatus) {
            return false;
        }

        if (newStatus == MissionStatus.SCHEDULED || newStatus == MissionStatus.ENDED) {
            mission.getInRepairRocketsRepository().unassignAndRemoveAllRockets();
            mission.getInSpaceRocketsRepository().unassignAndRemoveAllRockets();
            mission.setStatus(newStatus);
        }

        return true;
    }

    @Override
    public List<Mission> getMissions() {
        return new ArrayList<>(missions.values());
    }
}
