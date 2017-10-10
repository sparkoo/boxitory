package cz.sparko.boxitory.service;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;

/**
 * Responsible for persisting calculated hash to underlying store.
 */
public interface HashStore {
    /**
     * Persist hash to underlying storage. It's up to implementation whether replace previous stored value, ignore,
     * or throw {@link RuntimeException}.
     *
     * @param box       path to box. May vary depending on implementation.
     * @param hash      calculated hash
     * @param algorithm algorithm of hash
     */
    void persist(String box, String hash, HashAlgorithm algorithm);
}
