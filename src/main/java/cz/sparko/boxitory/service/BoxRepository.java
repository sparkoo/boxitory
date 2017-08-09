package cz.sparko.boxitory.service;

import cz.sparko.boxitory.domain.Box;

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
     * returned name should get valid result.
     *
     * @return names of available {@link Box}es
     */
    List<String> getBoxes();
}
