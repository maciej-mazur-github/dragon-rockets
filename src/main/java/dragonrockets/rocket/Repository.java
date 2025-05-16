package dragonrockets.rocket;

import java.util.Optional;

public interface Repository {
    boolean contains(String rocketName);

    Optional<Rocket> findRocket(String rocketName);
}
