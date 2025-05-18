package dragonrockets.rocket;

import dragonrockets.exception.MissionNotFoundException;
import dragonrockets.exception.RocketAssignedToAnotherMissionException;
import dragonrockets.exception.RocketNotAssignedToMissionException;
import dragonrockets.exception.RocketNotFoundException;
import dragonrockets.mission.Manager;
import dragonrockets.mission.Mission;
import dragonrockets.mission.MissionManager;

import java.util.*;

public class RocketRepository implements Repository {
    private final Map<String, Rocket> rockets = new HashMap<>();

    @Override
    public boolean contains(String rocketName) {
        return rockets.containsKey(rocketName);
    }

    @Override
    public Optional<Rocket> findRocket(String rocketName) {
        if (rockets.containsKey(rocketName)) {
            return Optional.of(rockets.get(rocketName));
        }
        return Optional.empty();
    }

    @Override
    public boolean addRocket(Rocket rocket) {
        if (!rockets.containsKey(rocket.getName())) {
            rockets.put(rocket.getName(), rocket);
            return true;
        }

        return false;
    }

    @Override
    public int getNumberOfRockets() {
        return rockets.size();
    }

    @Override
    public void setRocketStatus(String rocketName, String missionName, RocketStatus newStatus, Manager missionManager) {

        Rocket rocket = getRocketVerifiedByRocketNameAndByMissionName(rocketName, missionName, missionManager);

        // Ignoring attempt to set the exact same status as it was before
        if (rocket.getStatus() == newStatus) {
            return;
        }

        missionManager.setRocketStatus(rocket, missionName, newStatus);
    }

    private Rocket getRocketVerifiedByRocketNameAndByMissionName(String rocketName,
                                                                 String missionName,
                                                                 Manager missionManager) {

        if (!missionManager.containsMission(missionName)) {
            throw new MissionNotFoundException("Mission " + missionName + " does not exist");
        }

        Optional<Rocket> rocketOptional = findRocket(rocketName);
        if (rocketOptional.isEmpty()) {
            throw new RocketNotFoundException("Rocket " + rocketName + " does not exist");
        }

        Rocket rocket = rocketOptional.get();
        Optional<Mission> lastMissionOptional = rocket.getLastMission();
        if (lastMissionOptional.isEmpty()) {
            throw new RocketNotAssignedToMissionException("Rocket " + rocketName + " not assigned to any mission");
        }
        Mission lastMission = lastMissionOptional.get();
        if (!lastMission.getName().equalsIgnoreCase(missionName)) {
            throw new RocketAssignedToAnotherMissionException(
                    String.format("Rocket %s assigned to mission '%s', not to mission '%s'",
                            rocketName,
                            lastMission.getName(),
                            missionName)
            );
        }

        return rocket;
    }

    @Override
    public void removeRocket(Rocket rocket) {
        rockets.remove(rocket.getName());
    }

    @Override
    public void unassignAndRemoveAllRockets() {
        rockets.forEach((k, v) -> v.setLastMission(null));
        rockets.clear();
    }

    @Override
    public List<Rocket> getRockets() {
        return new ArrayList<>(rockets.values());
    }

    @Override
    public void wipeOutRocketsLastMission(Mission mission) {
        findRocketsByMission(mission).forEach(rocket -> rocket.setLastMission(null));
    }

    @Override
    public List<Rocket> findRocketsByMission(Mission mission) {
        return rockets.values().stream()
                .filter(rocket -> rocket.getLastMission().isPresent())
                .filter(rocket -> rocket.getLastMission().get().getName().equalsIgnoreCase(mission.getName()))
                .toList();
    }

//    private void wipeOutRocketsLastMission(Rocket rocket, Mission mission) {
//        if (rocket.getLastMission().isPresent()) {
//            if (rocket.getLastMission().get().equals(mission)) {
//                rocket.setLastMission(null);
//            }
//        }
//    }

//    @Override
//    public List<Rocket> getRockets() {
//        return new ArrayList<>(rockets.values());
//    }
}
