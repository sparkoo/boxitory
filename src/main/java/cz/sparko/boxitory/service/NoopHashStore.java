package cz.sparko.boxitory.service;

import cz.sparko.boxitory.factory.HashServiceFactory;

/**
 * Does nothing. Used when checksum persist is disabled.
 */
public class NoopHashStore implements HashStore {
    @Override
    public void persist(String boxFilename, String hash, HashServiceFactory.HashAlgorithm algorithm) {
    }
}
