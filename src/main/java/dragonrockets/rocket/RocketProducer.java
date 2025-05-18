package dragonrockets.rocket;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RocketProducer implements Producer {
    private final Set<String> producedRocketsHistory = new HashSet<>();

    @Override
    public Optional<Rocket> createNewRocket(String rocketName) {
        if (!producedRocketsHistory.contains(rocketName)) {
            producedRocketsHistory.add(rocketName);
            return Optional.of(new Rocket(rocketName));
        } else {
            return Optional.empty();
        }
    }
}
