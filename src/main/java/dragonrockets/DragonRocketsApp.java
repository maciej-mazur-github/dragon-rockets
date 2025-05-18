package dragonrockets;

import dragonrockets.exception.MissionNotFoundException;
import dragonrockets.exception.RocketNotFoundException;
import dragonrockets.mission.*;
import dragonrockets.rocket.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DragonRocketsApp {

    private final Manager missionManager;
    private final Producer rocketProducer;
    private final Repository mainRepository;

    public DragonRocketsApp() {
        this.rocketProducer = new RocketProducer();
        this.mainRepository = new RocketRepository();
        this.missionManager = new MissionManager(mainRepository);
    }

    public boolean addNewRocketToRepository(String rocketName) {
        Optional<Rocket> rocketOptional = rocketProducer.createNewRocket(rocketName);
        if (rocketOptional.isPresent()) {
            mainRepository.addRocket(rocketOptional.get());
            return true;
        }
        return false;
    }

    public boolean addNewMission(String missionName) {
        return missionManager.addMission(missionName);
    }

    public boolean assignRocketToMission(String rocketName, String missionName) {
        Optional<Rocket> rocketOptional = mainRepository.findRocket(rocketName);
        if (rocketOptional.isPresent()) {
            Rocket rocket = rocketOptional.get();
            // Only ON_GROUND and IN_REPAIR (having last mission wiped out) rockets can be assigned to mission
            if (missionManager.containsMission(missionName) &&
                    (rocket.getStatus() == RocketStatus.ON_GROUND ||
                            (rocket.getStatus() == RocketStatus.IN_REPAIR && rocket.getLastMission().isEmpty()))) {
                missionManager.assignRocketToMission(rocket, missionName);
                return true;
            }
            if (!missionManager.containsMission(missionName)) {
                throw new MissionNotFoundException("Mission " + missionName + " does not exist");
            }
            // IN_SPACE and IN_REPAIR (those still assigned to any mission) rockets cannot be re-assigned at this point
            if (rocket.getStatus() == RocketStatus.IN_SPACE ||
                    (rocket.getStatus() == RocketStatus.IN_REPAIR && rocket.getLastMission().isPresent())) {
                return false;
            }
        }
        throw new RocketNotFoundException("Rocket " + rocketName + " does not exist");
    }

    public void setRocketStatus(String rocketName, String missionName, RocketStatus newStatus) {
        mainRepository.setRocketStatus(rocketName, missionName, newStatus, missionManager);
    }

    public boolean setMissionStatus(String missionName, MissionStatus newStatus) {
        return missionManager.setMissionStatus(missionName, newStatus);
    }

    public List<MissionSummary> getSummary() {
        List<MissionSummary> inProgressAndPendingMissionSummaries = mainRepository.getRockets().stream()
                .filter(rocket -> rocket.getStatus() != RocketStatus.IN_REPAIR)
                .filter(rocket -> rocket.getLastMission().isPresent())
                .collect(Collectors.groupingBy(rocket -> rocket.getLastMission().get().getName()))
                .entrySet().stream()
                .map(this::convertEntryToMissionSummary)
                .toList();

        List<MissionSummary> scheduledAndEndedMissionSummaries = missionManager.getMissions().stream()
                .filter(mission -> mission.getStatus() == MissionStatus.SCHEDULED || mission.getStatus() == MissionStatus.ENDED)
                .map(this::convertMissionToMissionSummary)
                .toList();

        return Stream.concat(inProgressAndPendingMissionSummaries.stream(), scheduledAndEndedMissionSummaries.stream())
                .sorted(Comparator.comparing(MissionSummary::getRocketNumber).reversed()
                        .thenComparing(Comparator.comparing(MissionSummary::name).reversed()))
                .map(this::sortRockets)
                .toList();
    }

    private MissionSummary convertMissionToMissionSummary(Mission mission) {
        List<RocketSummary> rocketSummaries = mission.getInSpaceRocketsRepository().getRockets().stream()
                .map(Rocket::convertToRocketSummary)
                .toList();
        return new MissionSummary(mission.getName(), mission.getStatus().getSummaryForm(), rocketSummaries);
    }

    private MissionSummary sortRockets(MissionSummary missionSummary) {
        List<RocketSummary> sortedRocketSummaries = missionSummary.rocketSummaries().stream()
                .sorted(Comparator.comparing(RocketSummary::status).reversed()
                        .thenComparing(RocketSummary::name))
                .toList();
        return new MissionSummary(missionSummary.name(), missionSummary.status(), sortedRocketSummaries);
    }

    private MissionSummary convertEntryToMissionSummary(Map.Entry<String, List<Rocket>> entry) {
        String missionName = entry.getKey();
        // At this point the mission contains at least 1 rocket, otherwise it would be filtered out earlier
        String missionStatus = entry.getValue().get(0).getLastMission().orElseThrow().getStatus().getSummaryForm();
        List<Rocket> rockets = entry.getValue();
        List<RocketSummary> rocketSummaries = convertRocketsToRocketSummaries(rockets);
        return new MissionSummary(missionName, missionStatus, rocketSummaries);
    }

    private List<RocketSummary> convertRocketsToRocketSummaries(List<Rocket> rockets) {
        return rockets.stream()
                .map(Rocket::convertToRocketSummary)
                .toList();
    }

    public void printMissionSummary() {
        getSummary().forEach(System.out::println);
    }
}
