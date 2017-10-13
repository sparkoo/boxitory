package cz.sparko.boxitory.service.noop;

import cz.sparko.boxitory.service.DescriptionProvider;

import java.util.Optional;

public class NoopDescriptionProvider implements DescriptionProvider {
    @Override
    public Optional<String> getDescription(String boxName, String version) {
        return Optional.empty();
    }
}
