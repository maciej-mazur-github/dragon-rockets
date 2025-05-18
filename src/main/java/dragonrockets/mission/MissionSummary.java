package dragonrockets.mission;

import dragonrockets.rocket.RocketSummary;

import java.util.List;

public record MissionSummary(String name, String status, List<RocketSummary> rocketSummaries) {

    public int getRocketNumber() {
        return rocketSummaries.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(String.format(
                "\u2022 %s - %s - Dragons: %d%n",
                name,
                status,
                getRocketNumber()));
        for (RocketSummary rocketSummary : rocketSummaries) {
            builder.append(rocketSummary.toString());
        }

        return builder.toString();
    }

    //    public MissionDto(List<Mission> missions) {
//        this.missions = missions;
//    }
//
//    public Optional<Mission> findMission(String missionName) {
//        return Optional.empty();
//    }
//
//    public int getNumberOfMissions() {
//        return 0;
//    }
//
//    public List<Mission> getMissions() {
//        return new ArrayList<>();
//    }
}
