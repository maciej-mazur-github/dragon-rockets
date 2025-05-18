package dragonrockets.utils;

import dragonrockets.mission.Mission;
import dragonrockets.mission.MissionSummary;
import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketSummary;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SummaryUtils {
    public static MissionSummary convertMissionToMissionSummary(Mission mission) {
        List<RocketSummary> rocketSummaries = mission.getInSpaceRocketsRepository().getRockets().stream()
                .map(Rocket::convertToRocketSummary)
                .toList();
        return new MissionSummary(mission.getName(), mission.getStatus().getSummaryForm(), rocketSummaries);
    }

    public static MissionSummary sortRockets(MissionSummary missionSummary) {
        List<RocketSummary> sortedRocketSummaries = missionSummary.rocketSummaries().stream()
                .sorted(Comparator.comparing(RocketSummary::status).reversed()
                        .thenComparing(RocketSummary::name))
                .toList();
        return new MissionSummary(missionSummary.name(), missionSummary.status(), sortedRocketSummaries);
    }

    public static MissionSummary convertEntryToMissionSummary(Map.Entry<String, List<Rocket>> entry) {
        String missionName = entry.getKey();
        // At this point the mission contains at least 1 rocket, otherwise it would be filtered out earlier
        String missionStatus = entry.getValue().get(0).getLastMission().orElseThrow().getStatus().getSummaryForm();
        List<Rocket> rockets = entry.getValue();
        List<RocketSummary> rocketSummaries = convertRocketsToRocketSummaries(rockets);
        return new MissionSummary(missionName, missionStatus, rocketSummaries);
    }

    public static List<RocketSummary> convertRocketsToRocketSummaries(List<Rocket> rockets) {
        return rockets.stream()
                .map(Rocket::convertToRocketSummary)
                .toList();
    }
}
