package cz.sparko.boxitory.service;

public interface DescriptionProvider {
    /**
     * @param boxName
     * @param version
     * @return
     */
    String getDescription(String boxName, String version);
}
