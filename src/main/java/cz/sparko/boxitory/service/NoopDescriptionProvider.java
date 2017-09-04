package cz.sparko.boxitory.service;

import java.util.Optional;

public class NoopDescriptionProvider implements DescriptionProvider {
    @Override
    public Optional<String> getDescription(String boxName, String version) {
        return Optional.empty();
    }
}
