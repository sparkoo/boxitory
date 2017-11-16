package cz.sparko.boxitory.service.noop;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import cz.sparko.boxitory.service.HashStore;

import java.util.Optional;

/**
 * Does nothing. Used when checksum persist is disabled.
 */
public class NoopHashStore implements HashStore {

    @Override
    public void persist(String boxFilename, String hash, HashAlgorithm algorithm) {
    }

    @Override
    public Optional<String> loadHash(String box, HashAlgorithm algorithm) {
        return Optional.empty();
    }
}
