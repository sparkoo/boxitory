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

    List<String> getBoxes();
}
