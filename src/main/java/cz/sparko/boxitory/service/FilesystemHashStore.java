package cz.sparko.boxitory.service;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Stores calculated checksum on filesystem
 */
public class FilesystemHashStore implements HashStore {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemHashStore.class);

    /**
     * Store checksum on filesystem, beside box file with appended extension derived from
     * {@link HashAlgorithm}. When already exists, original file is not replaced.
     * When file can't be created for any reason (e.g. write permissions), error is logged but no exception raised.
     *
     * @param box       absolute filesystem path to the box
     * @param hash      calculated hash
     * @param algorithm algorithm of hash
     * @throws IllegalStateException when file provided by {@code box} does not exists
     */
    @Override
    public void persist(String box, String hash, HashAlgorithm algorithm) {
        File boxFile = new File(box);

        if (!boxFile.exists() || !boxFile.isFile()) {
            throw new IllegalStateException("box [" + box + "] does not exist and checksum can't be stored for it!");
        }

        if (algorithm == HashAlgorithm.DISABLED) {
            LOG.debug("Hash algorithm [{}]. Nothing to persist.", algorithm);
            return;
        }

        try {
            String boxHashFilename = box + algorithm.getFileExtension();
            File hashFile = new File(boxHashFilename);
            if (hashFile.exists()) {
                LOG.trace("Hash file [{}] already exist. Not replacing!", boxHashFilename);
                return;
            }
            boolean fileCreated = hashFile.createNewFile();
            if (fileCreated) {
                try (FileWriter writer = new FileWriter(hashFile)) {
                    writer.write(hash);
                    LOG.debug("Storing hash file [{}]", boxHashFilename);
                }
            } else {
                LOG.debug("Hash file [{}] was not created. Dropping.");
            }
        } catch (IOException e) {
            LOG.error("Something went wrong with persisting hash file.", e);
        }
    }
}
