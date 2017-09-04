package cz.sparko.boxitory.service;

import java.util.Optional;

/**
 * Provides descriptions for box's versions. Implementation is not responsible for storing or handling descriptions
 * in any way. It just takes one by given parameters from underlaying storage and provides it to caller.
 */
public interface DescriptionProvider {
    /**
     * Implementation provides description for given box's version, when available in it's storage.
     *
     * @param boxName name of the box of which we're requesting description
     * @param version particular box version of which we're requesting description
     * @return {@link Optional} of description of particular box version. When not found, {@link Optional#empty()}
     * returned.
     * @throws NullPointerException     when provided boxName or version is null
     * @throws IllegalArgumentException when provided boxName or version is empty
     */
    Optional<String> getDescription(String boxName, String version);
}
