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

import static cz.sparko.boxitory.service.HashService.HashAlgorithm.DISABLED;

/**
 * Stores/reads calculated checksums on/from filesystem. Hash file is named
 * {@code {box_filename}.{hash_algorithm_extension}} and has one-line content
 * <pre>{@code {calculated_hash}  {box_filename}}</pre>
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
     * @param algorithm algorithm of hash. Must not be {@link HashAlgorithm#DISABLED}
     * @throws IllegalStateException when file provided by {@code box} does not exists
     * @throws IllegalStateException when {@code algorithm} is {@link HashAlgorithm#DISABLED}
     */
    @Override
    public void persist(String box, String hash, HashAlgorithm algorithm) {
        File boxFile = getBoxFile(box);

        if (algorithm == DISABLED) {
            LOG.error("No hash algorithm [{}]. Nothing to persist.", algorithm);
            throw new IllegalStateException("Trying to persist hash with [" + DISABLED + "] algorithm set. " +
                    "This is not a valid state.");
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
                    writer.write(ChecksumFileHandler.createValidChecksumFileContent(hash, boxFile));
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
        validateBoxFile(getBoxFile(box));

        final Optional<String> hash;

        File boxHashFile = new File(box + algorithm.getFileExtension());
        if (boxHashFile.exists() && boxHashFile.isFile()) {
            LOG.trace("Found hash file [{}] for box version [{}]", boxHashFile.getAbsolutePath(), box);
            try {
                hash = Optional.of(ChecksumFileHandler.readHashFromChecksumFile(boxHashFile));
                LOG.debug("Hash [{}] loaded from file [{}] for box [{}]", hash, boxHashFile.getAbsolutePath(), box);
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
     * Get {@link File} for provided {@code box}
     *
     * @param box path to the box file
     * @throws IllegalStateException when box file does not exist
     */
    private File getBoxFile(String box) {
        File boxFile = new File(box);
        validateBoxFile(boxFile);
        return boxFile;
    }

    /**
     * Throw {@link IllegalStateException} when box file does not exists.
     *
     * @param boxFile file to check
     */
    private void validateBoxFile(File boxFile) {
        if (!boxFile.exists() || !boxFile.isFile()) {
            throw new IllegalStateException("box [" + boxFile.getName() + "] does not exist!");
        }
    }

    /**
     * Can parse or create valid checksum file content. Valid format of file is one-line
     * <code>
     * <pre>{@code {calculated_hash}  {box_filename}}</pre>
     * </code>
     * This class uses {@link ChecksumFileHandler#CHECKSUM_FILE_SEPARATOR} as separator.
     */
    private static class ChecksumFileHandler {
        private final static String CHECKSUM_FILE_SEPARATOR = "  ";

        /**
         * Creates valid checksum file content from provided {@code hash} and {@link File} {@code forFile}.
         *
         * @param hash    calculated hash for file
         * @param forFile file of calculated hash
         * @return valid content of the checksum file
         */
        static String createValidChecksumFileContent(String hash, File forFile) {
            return hash + CHECKSUM_FILE_SEPARATOR + forFile.getName();
        }

        /**
         * Reads hash from given {@link File} {@code checksumFile}
         *
         * @param checksumFile read hash from this file. Filename must be in format {original_filename}.{checksum_alg}
         * @return {@code checksumFile}'s checksum
         * @throws IOException           when some error when reading the file occurs
         * @throws IllegalStateException when checksum file has wrong format
         */
        static String readHashFromChecksumFile(File checksumFile)
                throws IOException, IllegalStateException {
            List<String> hashFileLines = Files.readAllLines(checksumFile.toPath());
            if (hashFileLines.size() != 1) {
                throw new IllegalStateException("Checksum file has wrong format!");
            }

            String boxFilename = parseOriginalFilenameFromChecksumFile(checksumFile);
            String[] hashSplittedLine = hashFileLines.get(0).split(CHECKSUM_FILE_SEPARATOR);
            if (hashSplittedLine.length != 2 || !hashSplittedLine[1].equals(boxFilename)) {
                throw new IllegalStateException("Checksum file has wrong format!");
            }

            return hashSplittedLine[0];
        }

        private static String parseOriginalFilenameFromChecksumFile(File checksumFile) throws IllegalStateException {
            String checksumFilename = checksumFile.getName();
            int lastDotIndex = checksumFilename.lastIndexOf('.');
            if (lastDotIndex < 0) {
                throw new IllegalStateException("Checksum file has wrong name! [" + checksumFilename + "]");
            }
            return checksumFilename.substring(0, lastDotIndex);
        }
    }
}
