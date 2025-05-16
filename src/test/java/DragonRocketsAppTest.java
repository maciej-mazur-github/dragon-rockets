import dragonrockets.DragonRocketsApp;
import dragonrockets.exception.MissionNotFoundException;
import dragonrockets.exception.RocketNotFoundException;
import dragonrockets.mission.Mission;
import dragonrockets.mission.MissionStatus;
import dragonrockets.mission.MissionSummary;
import dragonrockets.rocket.Rocket;
import dragonrockets.rocket.RocketStatus;
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
        MissionSummary missionSummary = app.getMissionSummary();

        // then
        assertThat(missionSummary.getNumberOfMissions()).isEqualTo(1);
        assertThat(missionSummary.findMission("Luna")).isPresent();
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1")).isPresent();
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(1);
    }

    @Test
    void shouldAddNewMissionWithNoRockets() {
        // given
        app.addNewMission("Luna");

        // when
        MissionSummary missionSummary = app.getMissionSummary();

        // then
        assertThat(missionSummary.getNumberOfMissions()).isEqualTo(1);
        assertThat(missionSummary.findMission("Luna")).isPresent();
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(0);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.SCHEDULED);

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
        assertThat(app.getMissionSummary().findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(1);
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
        assertThat(app.getMissionSummary().findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(1);
        assertThat(app.getMissionSummary().findMission("Transit").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(0);
        assertThat(app.getMissionSummary().findMission("Transit").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
    }

    @Test
    void shouldNotBeAbleToDuplicateMissions() {
        // given, when
        app.addNewMission("Luna");

        // then
        assertThat(app.addNewMission("Luna")).isFalse();
        assertThat(app.getMissionSummary().getNumberOfMissions()).isEqualTo(1);
        assertThat(app.getMissionSummary().findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(0);
        assertThat(app.getMissionSummary().findMission("Luna").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
    }

    @Test
    void shouldNotBeAbleToDuplicateRockets() {
        // given, when
        app.addNewRocketToRepository("Dragon1");

        // then
        assertThat(app.addNewRocketToRepository("Dragon1")).isFalse();
        assertThat(app.getRocketRepository().findRocket("Dragon1").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.ON_GROUND);
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
        Mission mission = app.getMissionSummary().findMission("Luna").orElseThrow();

        assertThat(mission.containsRocket("Dragon1")).isTrue();
        assertThat(mission.getStatus()).isEqualTo(MissionStatus.SCHEDULED);
        assertThat(mission.findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
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
        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.PENDING);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(2);
//        assertThat(missionSummary.findMission("Luna").orElseThrow()
//                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(app.getRocketRepository().findRocket("Dragon1").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon2").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon3").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
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

        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(3);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon2").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon3").orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
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

        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.ENDED);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(0);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon1").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon2").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon3").orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
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
        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair()).isEqualTo(0);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.ENDED);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon1").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon2").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow()
                .findRocket("Dragon3").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.ON_GROUND);
    }

    @Test
    void shouldNotChangeMissionStatusWhenOneOfMissionRocketsIsGrounded() {
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
        MissionSummary missionSummary = app.getMissionSummary();

        // then
        assertThat(missionSummary.findMission("Transit").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.findMission("Transit").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(3);
        assertThat(missionSummary.findMission("Transit").orElseThrow()
                .findRocket("Red Dragon").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Transit").orElseThrow()
                .findRocket("Dragon XL").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.findMission("Transit").orElseThrow()
                .findRocket("Falcon Heavy").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.IN_SPACE);
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
        List<Mission> missions = app.getMissionSummary().getMissions();

        // then
        assertThat(missions.size()).isEqualTo(6);

        Mission mission0 = missions.get(0);
        assertThat(mission0.getNumberOfRocketsNotInRepair()).isEqualTo(3);
        assertThat(mission0.getName()).isEqualTo("Transit");
        assertThat(mission0.getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        List<Rocket> mission0Rockets = mission0.getRockets();
        Rocket mission0Rocket0 = mission0Rockets.get(0);
        assertThat(mission0Rocket0.getName()).isEqualTo("Red Dragon");
        assertThat(mission0Rocket0.getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        Rocket mission0Rocket1 = mission0Rockets.get(1);
        assertThat(mission0Rocket1.getName()).isEqualTo("Dragon XL");
        assertThat(mission0Rocket1.getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        Rocket mission0Rocket2 = mission0Rockets.get(2);
        assertThat(mission0Rocket2.getName()).isEqualTo("Falcon Heavy");
        assertThat(mission0Rocket2.getStatus()).isEqualTo(RocketStatus.IN_SPACE);

        Mission mission1 = missions.get(1);
        assertThat(mission1.getNumberOfRocketsNotInRepair()).isEqualTo(2);
        assertThat(mission1.getName()).isEqualTo("Luna1");
        assertThat(mission1.getStatus()).isEqualTo(MissionStatus.PENDING);
        List<Rocket> mission1Rockets = mission1.getRockets();
        Rocket mission1Rocket0 = mission1Rockets.get(0);
        assertThat(mission1Rocket0.getName()).isEqualTo("Dragon1");
        assertThat(mission1Rocket0.getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        Rocket mission1Rocket1 = mission1Rockets.get(1);
        assertThat(mission1Rocket1.getName()).isEqualTo("Dragon2");
        assertThat(mission1Rocket1.getStatus()).isEqualTo(RocketStatus.ON_GROUND);

        Mission mission2 = missions.get(2);
        assertThat(mission2.getNumberOfRocketsNotInRepair()).isEqualTo(0);
        assertThat(mission2.getName()).isEqualTo("Vertical Landing");
        assertThat(mission2.getStatus()).isEqualTo(MissionStatus.ENDED);

        Mission mission3 = missions.get(3);
        assertThat(mission3.getNumberOfRocketsNotInRepair()).isEqualTo(0);
        assertThat(mission3.getName()).isEqualTo("Mars");
        assertThat(mission3.getStatus()).isEqualTo(MissionStatus.SCHEDULED);

        Mission mission4 = missions.get(4);
        assertThat(mission4.getNumberOfRocketsNotInRepair()).isEqualTo(0);
        assertThat(mission4.getName()).isEqualTo("Luna2");
        assertThat(mission4.getStatus()).isEqualTo(MissionStatus.SCHEDULED);

        Mission mission5 = missions.get(5);
        assertThat(mission5.getNumberOfRocketsNotInRepair()).isEqualTo(0);
        assertThat(mission5.getName()).isEqualTo("Double Landing");
        assertThat(mission5.getStatus()).isEqualTo(MissionStatus.ENDED);
    }

    @Test
    void shouldIgnoreAttemptToChangeMissionStatusFromScheduledToInProgressWhenNoRocketsAssigned() {
        // given, when
        app.addNewMission("Luna");

        // then
        assertThat(app.setMissionStatus("Luna", MissionStatus.IN_PROGRESS)).isFalse();

        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(0);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.SCHEDULED);
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
        // given
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.assignRocketToMission("Dragon1", "Luna");
        app.setMissionStatus("Luna", MissionStatus.ENDED);

        // when


        // then
        assertThat(app.assignRocketToMission("Dragon1", "Luna")).isFalse();
        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(1);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.ENDED);
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

        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(1);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1").orElseThrow()
                .getStatus()).isEqualTo(RocketStatus.IN_SPACE);
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

        MissionSummary missionSummary = app.getMissionSummary();

        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(0);
        assertThat(missionSummary.findMission("Transit").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(1);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus()).isEqualTo(MissionStatus.SCHEDULED);
        assertThat(missionSummary.findMission("Transit").orElseThrow().getStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.findMission("Transit").orElseThrow().findRocket("Dragon1").orElseThrow()
                .getStatus()).isEqualTo(RocketStatus.IN_SPACE);
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
        MissionSummary missionSummary = app.getMissionSummary();
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(3);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
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
        MissionSummary missionSummary = app.getMissionSummary();
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(3);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon2")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon3")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
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
        MissionSummary missionSummary = app.getMissionSummary();
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(3);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon1")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon2")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon3")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.IN_SPACE);
    }

    @Test
    void shouldBeAbleToGroundAllNonInRepairRocketsAndToNotChangeInRepairOnesStatusWhenMissionStatusChangedFromPendingToScheduled() {
        app.addNewMission("Luna");
        app.addNewRocketToRepository("Dragon1");
        app.addNewRocketToRepository("Dragon2");
        app.addNewRocketToRepository("Dragon3");
        app.assignRocketToMission("Dragon1", "Luna");
        app.assignRocketToMission("Dragon2", "Luna");
        app.assignRocketToMission("Dragon3", "Luna");
        app.setRocketStatus("Dragon1", "Luna", RocketStatus.IN_REPAIR);

        // when
        app.setMissionStatus("Luna", MissionStatus.SCHEDULED);

        // then
        MissionSummary missionSummary = app.getMissionSummary();
        assertThat(missionSummary.findMission("Luna").orElseThrow().getNumberOfRocketsNotInRepair())
                .isEqualTo(2);
        assertThat(missionSummary.findMission("Luna").orElseThrow().getStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon2")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(missionSummary.findMission("Luna").orElseThrow().findRocket("Dragon3")
                .orElseThrow().getStatus()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(app.getRocketRepository().findRocket("Dragon1").orElseThrow().getStatus())
                .isEqualTo(RocketStatus.IN_REPAIR);
    }
}
