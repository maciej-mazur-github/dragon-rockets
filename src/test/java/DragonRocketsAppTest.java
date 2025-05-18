import dragonrockets.DragonRocketsApp;
import dragonrockets.exception.MissionNotFoundException;
import dragonrockets.exception.RocketAssignedToAnotherMissionException;
import dragonrockets.exception.RocketNotAssignedToMissionException;
import dragonrockets.exception.RocketNotFoundException;
import dragonrockets.mission.Mission;
import dragonrockets.mission.MissionStatus;
import dragonrockets.mission.MissionSummary;
import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketStatus;
import dragonrockets.rocket.RocketSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class DragonRocketsAppTest {

    private final DragonRocketsApp app = new DragonRocketsApp();

    @Test
    void shouldAssignProperRocketToProperMission() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");

        // when
        List<MissionSummary> summary = app.getSummary();

        // then
//        assertThat(missionSummary.getNumberOfMissions()).isEqualTo(1);
        assertThat(summary.get(0).getRocketNumber()).isEqualTo(1);

//        assertThat(missionSummary.findMission("Luna")).isPresent();
        assertThat(summary.get(0).name()).isEqualTo("Luna");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1")).isPresent();
        assertThat(summary.get(0).rocketSummaries().get(0).name()).isEqualTo("Dragon1");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(summary.get(0).status()).isEqualTo("In progress");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(summary.get(0).rocketSummaries().get(0).status()).isEqualTo("In space");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(1);
        assertThat(summary.get(0).getRocketNumber()).isEqualTo(1);
    }

    @Test
    void shouldAddNewMissionWithNoRockets() {
        // given
        app.addNewMission("Luna");

        // when
//        MissionSummary missionSummary = app.getSummary();
        List<MissionSummary> summary = app.getSummary();

        // then
//        assertThat(missionSummary.getNumberOfMissions()).isEqualTo(1);
        assertThat(summary.size()).isEqualTo(1);

//        assertThat(missionSummary.findMission("Luna")).isPresent();
        assertThat(summary.get(0).name()).isEqualTo("Luna");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(0);
        assertThat(summary.get(0).getRocketNumber()).isEqualTo(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.SCHEDULED);
        assertThat(summary.get(0).status()).isEqualTo("Scheduled");

    }

    @Test
    void shouldNotAddExistingRocketToNonExistentMission() {
        // given, when
        app.addNewRocketToRepository("Dragon1");

        // then
        assertThatThrownBy(() -> app.assignRocketToMission("Dragon1", "Luna"))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @Test
    void shouldNotAddNonExistentRocketToExistingMission() {
        // given, when
        app.addNewMission("Luna");

        // then
        assertThatThrownBy(() -> app.assignRocketToMission("Dragon1", "Luna"))
                .isInstanceOf(RocketNotFoundException.class);
    }

    @Test
    void shouldNotBeAbleToAssignTheSameRocketToMissionMoreThanOnce() {
        // given, when
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");

        // then
        assertThat(app.assignRocketToMission("Dragon1", "Luna")).isFalse();
//        assertThat(app.getSummary().findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(1);
        assertThat(app.getSummary().get(0).getRocketNumber()).isEqualTo(1);
    }

    @Test
    void shouldNotBeAbleToAssignTheRocketToOneMissionWhenThisRocketIsAlreadyAssignedToAnotherOne() {
        // given, when
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");
        app.addNewMission("Transit");

        // then
        assertThat(app.assignRocketToMission("Dragon1", "Transit")).isFalse();
//        assertThat(app.getSummary().findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(1);
        assertThat(app.getSummary().get(0).getRocketNumber()).isEqualTo(1);

//        assertThat(app.getSummary().findMission("Transit").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(0);
        assertThat(app.getSummary().get(1).getRocketNumber()).isEqualTo(0);

//        assertThat(app.getSummary().findMission("Transit").orElseThrow().getStatus())
//                .isEqualTo(MissionStatus.SCHEDULED);
        assertThat(app.getSummary().get(1).status()).isEqualTo("Scheduled");
    }

    @Test
    void shouldNotBeAbleToDuplicateMissions() {
        // given, when
        app.addNewMission("Luna");

        // then
        assertThat(app.addNewMission("Luna")).isFalse();
//        assertThat(app.getSummary().getNumberOfMissions()).isEqualTo(1);
        assertThat(app.getSummary().size()).isEqualTo(1);

//        assertThat(app.getSummary().findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(0);
        assertThat(app.getSummary().get(0).getRocketNumber()).isEqualTo(0);

//        assertThat(app.getSummary().findMission("Luna").orElseThrow().getStatus())
//                .isEqualTo(MissionStatus.SCHEDULED);
        assertThat(app.getSummary().get(0).status()).isEqualTo("Scheduled");
    }

    @Test
    void shouldNotBeAbleToDuplicateRockets() {
        // given, when
        app.addNewRocketToRepository("Dragon1");

        // then
        assertThat(app.addNewRocketToRepository("Dragon1")).isFalse();
//        assertThat(app.getRocketRepository().findRocket("Dragon1").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.ON_GROUND);
    }

    @Test
    void shouldNotBeAbleToChangeRocketStatusIfRocketHasNotBeenAssignedToAnyMission() {
        // given, when
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");

        // then
        assertThatThrownBy(
                () -> app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_SPACE))
                .isInstanceOf(RocketNotAssignedToMissionException.class);
    }

    @Test
    void shouldNotBeAbleToChangeRocketStatusIfRocketHasBeenAssignedToOtherMissionThanSpecifiedInMethodArgument() {
        // given, when
        app.addNewMission("Luna");
        app.addNewMission("Transit");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");

        // then
        assertThatThrownBy(
                () -> app.setRocketStatus("Dragon1", "Transit", RocketStatus.IN_SPACE))
                .isInstanceOf(RocketAssignedToAnotherMissionException.class);
    }

    @Test
    void shouldProperlyChangeRocketStatus() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");

        // when
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND);

        // then
//        Mission mission = app.getSummary().findMission("Luna").orElseThrow();
        MissionSummary missionSummary = app.getSummary().get(0);

//        assertThat(mission.containsNonInRepairRocket("Dragon1")).isTrue();
//        assertThat(missionSummary.rocketSummaries().get(0).name()).isEqualTo("Dragon1");

//        assertThat(mission.getStatus()).isEqualTo(MissionStatus.SCHEDULED);

        assertThat(missionSummary.status()).isEqualTo("Scheduled");
        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);

//        assertThat(mission.findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("On ground");
    }

    @Test
    void shouldNotChangeStatusOfNonExistentRocket() {
        // given, when
        app.addNewMission("Luna");

        // then
        assertThatThrownBy(() -> app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND))
                .isInstanceOf(RocketNotFoundException.class);
    }

    @Test
    void shouldChangeMissionStatusToPendingAndKeepRemainingRocketsStatusUnchangedWhenOneRocketStatusIsSetToInRepair() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        // when
        app.setRocketStatus("Dragon2", "Luna", RocketStatus.ON_GROUND);
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_REPAIR);

        // then
//        MissionSummary missionSummary = app.getSummary();
        MissionSummary missionSummary = app.getSummary().get(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.PENDING);
        assertThat(missionSummary.status()).isEqualTo("Pending");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(2);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(2);

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_REPAIR);
//        assertThat(app.getRocketRepository().findRocket("Dragon1").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.IN_REPAIR);

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon2").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("On ground");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon3").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(1).status()).isEqualTo("In space");
    }

    @Test
    void shouldIgnoreManualAttemptToChangeMissionStatusFromInProgressToPending() {
        // given, when
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        // then
        assertThat(app.setMissionStatus("Luna", MissionStatus.PENDING)).isFalse();

//        MissionSummary missionSummary = app.getSummary();
        MissionSummary missionSummary = app.getSummary().get(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.status()).isEqualTo("In progress");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(3);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(3);

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("In space");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon2").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(1).status()).isEqualTo("In space");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon3").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(2).status()).isEqualTo("In space");
    }

    @Test
    void shouldIgnoreManualAttemptToChangeMissionStatusFromEndedToInProgress() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        // when
        app.setMissionStatus("Luna", MissionStatus.ENDED);

        // then
        assertThat(app.setMissionStatus("Luna", MissionStatus.IN_PROGRESS)).isFalse();

        MissionSummary missionSummary = app.getSummary().get(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.ENDED);
        assertThat(missionSummary.status()).isEqualTo("Ended");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(0);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("On ground");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon2").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.rocketSummaries().get(1).status()).isEqualTo("On ground");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon3").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.rocketSummaries().get(2).status()).isEqualTo("On ground");
    }

    @Test
    void shouldProperlyChangeMissionStatusAndMoveRocketsBackToRepositoryWhenMissionStatusSetToEnded() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        // when
        app.setMissionStatus("Luna", MissionStatus.ENDED);

        // then
        MissionSummary missionSummary = app.getSummary().get(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair()).isEqualTo(0);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.ENDED);
        assertThat(missionSummary.status()).isEqualTo("Ended");

//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon1").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon2").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon3").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.ON_GROUND);
    }

    @Test
    void shouldKeepMissionStatusInProgressWhenOneOfMissionRocketsIsGrounded() {
        // given
        app.addNewMission("Transit");
        app.addNewRocketToRepository("Red Dragon");
        app.addNewRocketToRepository("Dragon XL");
        app.addNewRocketToRepository("Falcon Heavy");
        app.assignRocketToMission("Red Dragon", "Transit");
        app.assignRocketToMission("Dragon XL", "Transit");
        app.assignRocketToMission("Falcon Heavy", "Transit");

        // when
        app.setRocketStatus("Red Dragon", "Transit", RocketStatus.ON_GROUND);
        MissionSummary missionSummary = app.getSummary().get(0);

        // then
//        assertThat(missionSummary.findMission("Transit").orElseThrow().getStatus())
//                .isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.status()).isEqualTo("In progress");

//        assertThat(missionSummary.findMission("Transit").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(3);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(3);

//        assertThat(missionSummary.findMission("Transit").orElseThrow()
//                .findRocket("Red Dragon").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.rocketSummaries().get(0).name()).isEqualTo("Red Dragon");

        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("On ground");

        assertThat(missionSummary.rocketSummaries().get(1).name()).isEqualTo("Dragon XL");

        assertThat(missionSummary.rocketSummaries().get(1).status()).isEqualTo("In space");

        assertThat(missionSummary.rocketSummaries().get(2).name()).isEqualTo("Falcon Heavy");

        assertThat(missionSummary.rocketSummaries().get(2).status()).isEqualTo("In space");

//        assertThat(missionSummary.findMission("Transit").orElseThrow()
//                .findRocket("Dragon XL").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.IN_SPACE);
//        assertThat(missionSummary.findMission("Transit").orElseThrow()
//                .findRocket("Falcon Heavy").orElseThrow().getStatus())
//                .isEqualTo(RocketStatus.IN_SPACE);
    }

    @Test
    void shouldHaveMissionSummaryMissionsInProperOrder() {
        // given
        app.addNewMission("Mars");

        app.addNewMission("Luna1");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna1");
        app.assignRocketToMission("Dragon2", "Luna1");
        app.assignRocketToMission("Dragon3", "Luna1");
        app.setRocketStatus("Dragon1", "Luna1", RocketStatus.ON_GROUND);
        app.setRocketStatus("Dragon2", "Luna1", RocketStatus.ON_GROUND);
        app.setRocketStatus("Dragon3", "Luna1", RocketStatus.IN_REPAIR);

        app.addNewMission("Double Landing");
        app.addNewRocketToRepository("Dragon4");
        app.addNewRocketToRepository("Dragon5");
        app.addNewRocketToRepository("Dragon6");
        app.assignRocketToMission("Dragon4", "Double Landing");
        app.assignRocketToMission("Dragon5", "Double Landing");
        app.assignRocketToMission("Dragon6", "Double Landing");
        app.setMissionStatus("Double Landing", MissionStatus.ENDED);

        app.addNewMission("Transit");
        app.addNewRocketToRepository("Red Dragon");
        app.addNewRocketToRepository("Dragon XL");
        app.addNewRocketToRepository("Falcon Heavy");
        app.assignRocketToMission("Red Dragon", "Transit");
        app.assignRocketToMission("Dragon XL", "Transit");
        app.assignRocketToMission("Falcon Heavy", "Transit");
        app.setRocketStatus("Red Dragon", "Transit", RocketStatus.ON_GROUND);

        app.addNewMission("Luna2");

        app.addNewMission("Vertical Landing");
        app.addNewRocketToRepository("Dragon7");
        app.addNewRocketToRepository("Dragon8");
        app.assignRocketToMission("Dragon7", "Vertical Landing");
        app.assignRocketToMission("Dragon8", "Vertical Landing");
        app.setMissionStatus("Vertical Landing", MissionStatus.ENDED);

        // when
        List<MissionSummary> summaries = app.getSummary();

        // then
        assertThat(summaries.size()).isEqualTo(6);

        MissionSummary mission0Summary = summaries.get(0);
        assertThat(mission0Summary.getRocketNumber()).isEqualTo(3);
        assertThat(mission0Summary.name()).isEqualTo("Transit");
        assertThat(mission0Summary.status()).isEqualTo("In progress");
        List<RocketSummary> mission0RocketSummaries = mission0Summary.rocketSummaries();
        RocketSummary mission0Rocket0Summary = mission0RocketSummaries.get(0);
        assertThat(mission0Rocket0Summary.name()).isEqualTo("Red Dragon");
        assertThat(mission0Rocket0Summary.status()).isEqualTo("On ground");
        RocketSummary mission0Rocket1Summary = mission0RocketSummaries.get(1);
        assertThat(mission0Rocket1Summary.name()).isEqualTo("Dragon XL");
        assertThat(mission0Rocket1Summary.status()).isEqualTo("In space");
        RocketSummary mission0Rocket2Summary = mission0RocketSummaries.get(2);
        assertThat(mission0Rocket2Summary.name()).isEqualTo("Falcon Heavy");
        assertThat(mission0Rocket2Summary.status()).isEqualTo("In space");

        MissionSummary mission1Summary = summaries.get(1);
        assertThat(mission1Summary.getRocketNumber()).isEqualTo(2);
        assertThat(mission1Summary.name()).isEqualTo("Luna1");
        assertThat(mission1Summary.status()).isEqualTo("Pending");
        List<RocketSummary> mission1RocketSummaries = mission1Summary.rocketSummaries();
        RocketSummary mission1Rocket0 = mission1RocketSummaries.get(0);
        assertThat(mission1Rocket0.name()).isEqualTo("Dragon1");
        assertThat(mission1Rocket0.status()).isEqualTo("On ground");
        RocketSummary mission1Rocket1 = mission1RocketSummaries.get(1);
        assertThat(mission1Rocket1.name()).isEqualTo("Dragon2");
        assertThat(mission1Rocket1.status()).isEqualTo("On ground");

        MissionSummary mission2Summary = summaries.get(2);
        assertThat(mission2Summary.getRocketNumber()).isEqualTo(0);
        assertThat(mission2Summary.name()).isEqualTo("Vertical Landing");
        assertThat(mission2Summary.status()).isEqualTo("Ended");

        MissionSummary mission3Summary = summaries.get(3);
        assertThat(mission3Summary.getRocketNumber()).isEqualTo(0);
        assertThat(mission3Summary.name()).isEqualTo("Mars");
        assertThat(mission3Summary.status()).isEqualTo("Scheduled");

        MissionSummary mission4Summary = summaries.get(4);
        assertThat(mission4Summary.getRocketNumber()).isEqualTo(0);
        assertThat(mission4Summary.name()).isEqualTo("Luna2");
        assertThat(mission4Summary.status()).isEqualTo("Scheduled");

        MissionSummary mission5Summary = summaries.get(5);
        assertThat(mission5Summary.getRocketNumber()).isEqualTo(0);
        assertThat(mission5Summary.name()).isEqualTo("Double Landing");
        assertThat(mission5Summary.status()).isEqualTo("Ended");
    }

    @Test
    void shouldIgnoreAttemptToChangeMissionStatusFromScheduledToInProgressWhenNoRocketsAssigned() {
        // given, when
        app.addNewMission("Luna");

        // then
        assertThat(app.setMissionStatus("Luna", MissionStatus.IN_PROGRESS)).isFalse();

        MissionSummary missionSummary = app.getSummary().get(0);

        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);
        assertThat(missionSummary.status()).isEqualTo("Scheduled");
    }

//    @Test
//    void shouldIgnoreAttemptToChangeMissionStatusFromInProgressToScheduledWhenAnyRocketIsAlreadyAssigned() {
//        // given
//        app.addNewMission("Luna");
//        app.addNewRocketToRepository("Dragon1");
//        app.addNewRocketToRepository("Dragon2");
//        app.addNewRocketToRepository("Dragon3");
//        app.assignRocketToMission("Dragon1", "Luna");
//        app.assignRocketToMission("Dragon2", "Luna");
//        app.assignRocketToMission("Dragon3", "Luna");
//
//        // when
//        app.setMissionStatus("Luna", MissionStatus.SCHEDULED);
//
//        // then
//        MissionSummary missionSummary = app.getMissionSummary();
//
//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair()).isEqualTo(3);
//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
//    }

//    @Test
//    void shouldIgnoreAttemptToChangeMissionStatusFromPendingToScheduledWhenAnyRocketIsInRepair() {
//        // given
//        app.addNewMission("Luna");
//        app.addNewRocketToRepository("Dragon1");
//        app.assignRocketToMission("Dragon1", "Luna");
//        app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_REPAIR);
//
//        // when
//        app.setMissionStatus("Luna", MissionStatus.SCHEDULED);
//
//        // then
//        MissionSummary missionSummary = app.getMissionSummary();
//
//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair()).isEqualTo(0);
//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.PENDING);
//    }

    @Test
    void shouldNotAddNewRocketToMissionThatHasAlreadyBeenEnded() {
        // given, when
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");
        app.setMissionStatus("Luna", MissionStatus.ENDED);

        // then
        assertThat(app.assignRocketToMission("Dragon1", "Luna")).isFalse();
        MissionSummary missionSummary = app.getSummary().get(0);

        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);
        assertThat(missionSummary.status()).isEqualTo("Ended");
    }

    @Test
    void shouldBeAbleToAddRocketThatWasInSpaceAndLaterGroundedBackToTheLastMission() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");

        // when
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND);

        // then
        assertThatCode(() -> app.assignRocketToMission("Dragon1", "Luna")).doesNotThrowAnyException();

        MissionSummary missionSummary = app.getSummary().get(0);

        assertThat(missionSummary.getRocketNumber()).isEqualTo(1);
        assertThat(missionSummary.status()).isEqualTo("In progress");
        assertThat(missionSummary.rocketSummaries().get(0).name()).isEqualTo("Dragon1");
        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("In space");
    }

    @Test
    void shouldNotBeAbleToChangeRocketStatusFromOnGroundToInSpaceWithoutAssigningItBackToMission() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");

        // when
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND);

        // then
        assertThatThrownBy(() -> app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_SPACE))
                .isInstanceOf(RocketNotAssignedToMissionException.class);

        MissionSummary missionSummary = app.getSummary().get(0);

        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);
        assertThat(missionSummary.status()).isEqualTo("Scheduled");
//        assertThat(missionSummary.rocketSummaries().get(0).name()).isEqualTo("Dragon1");
//        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("In space");
    }

    @Test
    void shouldBeAbleToAddRocketThatWasInSpaceAndLaterGroundedToAnotherMission() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");
        app.addNewMission("Transit");

        // when
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND);

        // then
        assertThatCode(() -> app.assignRocketToMission("Dragon1", "Transit")).doesNotThrowAnyException();

        List<MissionSummary> summaries = app.getSummary();

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(0);
        assertThat(summaries.get(0).getRocketNumber()).isEqualTo(1);

//        assertThat(missionSummary.findMission("Transit").orElseThrow().getNumberOfRocketsNotInRepair())
//                .isEqualTo(1);
        assertThat(summaries.get(1).getRocketNumber()).isEqualTo(0);

//        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.SCHEDULED);
        assertThat(summaries.get(0).status()).isEqualTo("In progress");

//        assertThat(missionSummary.findMission("Transit").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(summaries.get(1).status()).isEqualTo("Scheduled");

//        assertThat(missionSummary.findMission("Transit").orElseThrow().findRocket("Dragon1").orElseThrow()
//                .getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(summaries.get(0).rocketSummaries().get(0).status()).isEqualTo("In space");
    }

    @Test
    void shouldAutomaticallyChangeMissionStatusFromInProgressToScheduledWhenAllItsRocketsAreManuallyGrounded() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        // when
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND);
        app.setRocketStatus("Dragon2", "Luna", RocketStatus.ON_GROUND);
        app.setRocketStatus("Dragon3", "Luna", RocketStatus.ON_GROUND);

        // then
        MissionSummary missionSummary = app.getSummary().get(0);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);
        assertThat(missionSummary.status()).isEqualTo("Scheduled");
    }

    @Test
    void shouldAutomaticallyGroundAllRocketsInMissionWhenMissionStatusIsChangedFromInProgressToScheduled() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        // when
        app.setMissionStatus("Luna", MissionStatus.SCHEDULED);

        // then
        MissionSummary missionSummary = app.getSummary().get(0);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(0);
        assertThat(missionSummary.status()).isEqualTo("Scheduled");
//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1")
//                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon2")
//                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon3")
//                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
    }

    @Test
    void shouldBeAbleToAutomaticallyRestoreInProgressStatusForMissionAfterFirstMovingOneRocketToInRepairAndThenChangingItsStatusBackToInSpace() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_REPAIR);

        // when
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_SPACE);

        // then
        MissionSummary missionSummary = app.getSummary().get(0);
        assertThat(missionSummary.getRocketNumber()).isEqualTo(3);
        assertThat(missionSummary.status()).isEqualTo("In progress");
//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1")
//                .orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(0).name()).isEqualTo("Dragon1");
        assertThat(missionSummary.rocketSummaries().get(0).status()).isEqualTo("In space");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon2")
//                .orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(1).name()).isEqualTo("Dragon2");
        assertThat(missionSummary.rocketSummaries().get(1).status()).isEqualTo("In space");

//        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon3")
//                .orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.rocketSummaries().get(2).name()).isEqualTo("Dragon3");
        assertThat(missionSummary.rocketSummaries().get(2).status()).isEqualTo("In space");
    }

    @Test
    void shouldChangeInProgressMissionStatusToPendingWhenRocketWasFirstInAnotherMissionThenSetToInRepairAndThenGroundedAndThenAssignedToAnotherMission() {
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");

        app.addNewMission("Transit");
        app.addNewRocketToRepository("Dragon4");
        app.assignRocketToMission("Dragon4", "Transit");

        app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_REPAIR);
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.ON_GROUND);

        // when
        app.assignRocketToMission("Dragon1", "Transit");

        // then
        List<MissionSummary> summaries = app.getSummary();

        assertThat(summaries.get(0).getRocketNumber()).isEqualTo(2);
        assertThat(summaries.get(0).status()).isEqualTo("In progress");
        assertThat(summaries.get(1).status()).isEqualTo("Pending");
        assertThat(summaries.get(1).getRocketNumber()).isEqualTo(2);
    }
}
