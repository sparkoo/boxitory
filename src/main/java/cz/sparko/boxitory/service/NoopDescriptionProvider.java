package cz.sparko.boxitory.service;

public class NoopDescriptionProvider implements DescriptionProvider {
    @Override
    public String getDescription(String boxName, String version) {
        return null;
    }
}
