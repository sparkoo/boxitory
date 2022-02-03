package cz.sparko.boxitory.service.filesystem;

import cz.sparko.boxitory.service.HashService;
import cz.sparko.boxitory.service.HashStore;
import cz.sparko.boxitory.service.noop.NoopHashStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This implementation of {@link HashService} calculates checksums from files on filesystem using {@link MessageDigest}
 */
public class FilesystemDigestHashService implements HashService {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemDigestHashService.class);

    private static final int DEFAULT_STREAM_BUFFER_LENGTH = 1024;

    private final int streamBufferLength;
    private final HashStore hashStore;
    private final HashAlgorithm hashAlgorithm;

    /**
     * @param hashAlgorithm       algorithm used to calculate hashes
     * @param streamBufferLength  buffer used when calculating hashes
     * @param hashStore           store used to persist already calculated hashes
     */
    public FilesystemDigestHashService(HashAlgorithm hashAlgorithm, int streamBufferLength, HashStore hashStore) {
        this.hashAlgorithm = hashAlgorithm;
        this.streamBufferLength = streamBufferLength;
        this.hashStore = hashStore;
    }

    /**
     * See {@link FilesystemDigestHashService#FilesystemDigestHashService(HashAlgorithm, int, HashStore)}
     * <p>
     * Uses {@link NoopHashStore} as store.
     */
    public FilesystemDigestHashService(HashAlgorithm hashAlgorithm, int streamBufferLength) {
        this(hashAlgorithm, streamBufferLength, new NoopHashStore());
    }

    /**
     * See {@link FilesystemDigestHashService#FilesystemDigestHashService(HashAlgorithm, int, HashStore)}
     * <p>
     * Uses {@link NoopHashStore} as store.
     * <br>
     * Uses {@link FilesystemDigestHashService#DEFAULT_STREAM_BUFFER_LENGTH} as {@code streamBufferLength}.
     */
    public FilesystemDigestHashService(HashAlgorithm hashAlgorithm) {
        this(hashAlgorithm, DEFAULT_STREAM_BUFFER_LENGTH, new NoopHashStore());
    }

    /**
     * See {@link FilesystemDigestHashService#FilesystemDigestHashService(HashAlgorithm, int, HashStore)}
     * <p>
     * Uses {@link FilesystemDigestHashService#DEFAULT_STREAM_BUFFER_LENGTH} as {@code streamBufferLength}.
     */
    public FilesystemDigestHashService(HashAlgorithm hashAlgorithm, HashStore hashStore) {
        this(hashAlgorithm, DEFAULT_STREAM_BUFFER_LENGTH, hashStore);
    }

    @Override
    public String getHashType() {
        return hashAlgorithm.getVagrantInterfaceName();
    }

    @Override
    public String getChecksum(String box) {
        return hashStore.loadHash(box, hashAlgorithm)
                .orElseGet(() -> calculateHashAndStore(box));
    }

    @Override
    public String getChecksum(String box, boolean canCalculate) {
        if (canCalculate) {
            return getChecksum(box);
        }

        return hashStore.loadHash(box, hashAlgorithm)
                .orElse(null);
    }

    private String calculateHashAndStore(String box) {
        final String hash = calculateHash(box);
        hashStore.persist(box, hash, hashAlgorithm);
        return hash;
    }

    private String calculateHash(String box) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm.getMessageDigestName());
            LOG.debug("calculating [{}] hash for box [{}]", hashAlgorithm.name(), box);
            try (InputStream boxDataStream = Files.newInputStream(new File(box).toPath());
                 InputStream digestInputStream = new DigestInputStream(boxDataStream, messageDigest)) {
                LOG.trace("buffering box data (buffer size [{}]b) ...", streamBufferLength);
                final byte[] buffer = new byte[streamBufferLength];
                //noinspection StatementWithEmptyBody
                while (digestInputStream.read(buffer) > 0) ;

                return getHash(messageDigest.digest());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.error("Error during processing file [{}], message: [{}]", box, e.getMessage());
            throw new RuntimeException(
                    "Error while getting checksum for file " + box + " reason: " + e.getMessage(), e);
        }
    }

    private String getHash(byte[] digestBytes) {
        return DatatypeConverter.printHexBinary(digestBytes).toLowerCase();
    }

    @Override
    public String toString() {
        return "FilesystemDigestHashService{" +
                "streamBufferLength=" + streamBufferLength +
                ", hashStore=" + hashStore +
                ", hashAlgorithm=" + hashAlgorithm +
                '}';
    }
}
