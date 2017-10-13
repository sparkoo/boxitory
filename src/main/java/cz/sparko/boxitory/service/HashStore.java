package cz.sparko.boxitory.service;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;

import java.util.Optional;

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
     * @param algorithm algorithm of the hash
     */
    void persist(String box, String hash, HashAlgorithm algorithm);

    /**
     * Load previously persisted hash for given {@code box}.
     *
     * @param box       path to box. May vary depending on implementation.
     * @param algorithm algorithm of the hash
     * @return hash for {@code box} when found, {@link Optional#empty()} otherwise
     */
    Optional<String> loadHash(String box, HashAlgorithm algorithm);
}
