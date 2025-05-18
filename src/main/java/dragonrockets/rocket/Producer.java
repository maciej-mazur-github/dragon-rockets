package dragonrockets.rocket;

import java.util.Optional;

public interface Producer {
    Optional<Rocket> createNewRocket(String rocketName);
}
