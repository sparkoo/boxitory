package cz.sparko.boxitory.service;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;

/**
 * Does nothing. Used when checksum persist is disabled.
 */
public class NoopHashStore implements HashStore {
    @Override
    public void persist(String boxFilename, String hash, HashAlgorithm algorithm) {
    }
}
