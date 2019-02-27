package cz.sparko.boxitory.service;

import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.exception.NotFoundException;
import cz.sparko.boxitory.model.BoxStream;

import java.util.List;
import java.util.Optional;

public interface BoxRepository {
    /**
     * Returns {@link Box} by given {@code boxName} from source that implementation provides.
     *
     * @param boxName name of the requested box
     * @return {@link Box} when found, {@link Optional#empty()} when not found
     */
    Optional<Box> getBox(String boxName);

    /**
     * Returns {@link List} of names of available {@link Box}es. Call {@link BoxRepository#getBox(String)} with any of
     * returned name should get full {@link Box} instance.
     *
     * @return names of available {@link Box}es
     */
    List<String> getBoxNames();

    /**
     * Finds all available and valid boxes and provides full {@link Box} instances of them.
     *
     * @return list of available {@link Box}es
     */
    List<Box> getBoxes();

    /**
     * Provides byte stream of box found by given parameters.
     *
     * @param boxName     name of box to find
     * @param boxProvider provider of box to find
     * @param boxVersion  version of box to find
     * @return {@link BoxStream} found by given parameters, {@link Optional#empty} when box file not found
     * @throws {@link NotFoundException} when box does not exist or don't have given provider or don't
     * have given version
     */
    Optional<BoxStream> getBoxStream(String boxName, String boxProvider, String boxVersion);

    /**
     * Gets latest version of box with given name and provider
     *
     * @param boxName     name of box to find
     * @param boxProvider provider of box to find
     * @return latest version of box with provider
     * @throws {@link NotFoundException} when box does not exist of does not have given provider
     */
    String latestVersionOfBox(String boxName, String boxProvider);
}
