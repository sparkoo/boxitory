package cz.sparko.boxitory.service.filesystem;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import cz.sparko.boxitory.service.HashStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * Stores/reads calculated checksums on/from filesystem. Hash file is named
 * {@code {box_filename}.{hash_algorithm_extension}} and has one-line content {@code {calculated_hash}  {box_filename}}.
 * Checksum files can be created manually, but must have proper format.
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
        File boxFile = checkBoxFileExists(box);

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
                    writer.write(createValidChecksumFilecontent(hash, boxFile));
                    LOG.debug("Storing hash file [{}]", hashFile.getName());
                }
            } else {
                LOG.debug("Hash file [{}] was not created. Dropping.");
            }
        } catch (IOException e) {
            LOG.error("Something went wrong when persisting hash file.", e);
        }
    }

    /**
     * Reads checksum from filesystem, from file beside box file with extension derived from {@link HashAlgorithm}.
     *
     * @param box       absolute filesystem path to the box
     * @param algorithm algorithm of hash
     * @return hash when file found, {@link Optional#empty()} otherwise
     * @throws IllegalStateException when file provided by {@code box} does not exists
     */
    @Override
    public Optional<String> loadHash(String box, HashAlgorithm algorithm) {
        File boxFile = checkBoxFileExists(box);

        final Optional<String> hash;

        File boxHashFile = new File(box + algorithm.getFileExtension());
        if (boxHashFile.exists() && boxHashFile.isFile()) {
            LOG.trace("Found hash file [{}] for box [{}]", boxHashFile.getAbsolutePath(), box);
            try {
                hash = Optional.of(readHashFromChecksumFile(boxHashFile, boxFile.getName()));
                LOG.trace("Hash [{}] loaded from file [{}] for box [{}]", hash, boxHashFile.getAbsolutePath(), box);
            } catch (IllegalStateException ise) {
                LOG.error("Checksum file [{}] has probably wrong format.", boxHashFile, ise);
                return Optional.empty();
            } catch (IOException e) {
                LOG.error("Something went wrong when reading hash file.", e);
                return Optional.empty();
            }
        } else {
            hash = Optional.empty();
        }
        return hash;
    }

    /**
     * Throw {@link IllegalStateException} when box file does not exists.
     *
     * @param box path to the box file
     */
    private File checkBoxFileExists(String box) {
        File boxFile = new File(box);

        if (!boxFile.exists() || !boxFile.isFile()) {
            throw new IllegalStateException("box [" + box + "] does not exist!");
        }

        return boxFile;
    }

    private final String CHECKSUM_FILE_SEPARATOR = "  ";

    private String createValidChecksumFilecontent(String hash, File forFile) {
        return hash + CHECKSUM_FILE_SEPARATOR + forFile.getName();
    }

    private String readHashFromChecksumFile(File checksumFile, String boxFilename)
            throws IOException, IllegalStateException {
        List<String> hashFileLines = Files.readAllLines(checksumFile.toPath());
        if (hashFileLines.size() != 1) {
            throw new IllegalStateException("Checksum file has wrong format!");
        }

        String[] hashSplittedLine = hashFileLines.get(0).split(CHECKSUM_FILE_SEPARATOR);
        if (hashSplittedLine.length != 2 || !hashSplittedLine[1].equals(boxFilename)) {
            throw new IllegalStateException("Checksum file has wrong format!");
        }

        return hashSplittedLine[0];
    }
}
