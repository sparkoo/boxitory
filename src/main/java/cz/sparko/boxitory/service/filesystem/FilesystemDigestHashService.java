package cz.sparko.boxitory.service.filesystem;

import cz.sparko.boxitory.service.HashService;
import cz.sparko.boxitory.service.HashStore;
import cz.sparko.boxitory.service.noop.NoopHashStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * This implementation of {@link HashService} calculates checksums from files on filesystem using {@link MessageDigest}
 */
public class FilesystemDigestHashService implements HashService {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemDigestHashService.class);

    private static final int DEFAULT_STREAM_BUFFER_LENGTH = 1024;

    private final MessageDigest messageDigest;
    private final int streamBufferLength;
    private final HashStore hashStore;
    private final HashAlgorithm hashAlgorithm;

    /**
     * @param messageDigest      instance of {@link MessageDigest} which is used to calculate hashes
     * @param hashAlgorithm      algorithm used to calculate hashes
     * @param streamBufferLength buffer used when calculating hashes
     * @param hashStore          store used to persist already calculated hashes
     */
    public FilesystemDigestHashService(MessageDigest messageDigest, HashAlgorithm hashAlgorithm,
                                       int streamBufferLength, HashStore hashStore) {
        this.hashAlgorithm = hashAlgorithm;
        this.messageDigest = messageDigest;
        this.streamBufferLength = streamBufferLength;
        this.hashStore = hashStore;
    }

    /**
     * See {@link FilesystemDigestHashService#FilesystemDigestHashService(MessageDigest, HashAlgorithm, int, HashStore)}
     * <p>
     * Uses {@link NoopHashStore} as store.
     */
    public FilesystemDigestHashService(MessageDigest messageDigest, HashAlgorithm hashAlgorithm,
                                       int streamBufferLength) {
        this(messageDigest, hashAlgorithm, streamBufferLength, new NoopHashStore());
    }

    /**
     * See {@link FilesystemDigestHashService#FilesystemDigestHashService(MessageDigest, HashAlgorithm, int, HashStore)}
     * <p>
     * Uses {@link NoopHashStore} as store.
     * <br>
     * Uses {@link FilesystemDigestHashService#DEFAULT_STREAM_BUFFER_LENGTH} as {@code streamBufferLength}.
     */
    public FilesystemDigestHashService(MessageDigest messageDigest, HashAlgorithm hashAlgorithm) {
        this(messageDigest, hashAlgorithm, DEFAULT_STREAM_BUFFER_LENGTH, new NoopHashStore());
    }

    /**
     * See {@link FilesystemDigestHashService#FilesystemDigestHashService(MessageDigest, HashAlgorithm, int, HashStore)}
     * <p>
     * Uses {@link FilesystemDigestHashService#DEFAULT_STREAM_BUFFER_LENGTH} as {@code streamBufferLength}.
     */
    public FilesystemDigestHashService(MessageDigest messageDigest, HashAlgorithm hashAlgorithm, HashStore hashStore) {
        this(messageDigest, hashAlgorithm, DEFAULT_STREAM_BUFFER_LENGTH, hashStore);
    }

    @Override
    public String getHashType() {
        return messageDigest.getAlgorithm().replaceAll("-", "").toLowerCase();
    }

    @Override
    public String getChecksum(String box) {
        final String hash = hashStore.loadHash(box, hashAlgorithm)
                .orElseGet(() -> calculateHash(box));
        hashStore.persist(box, hash, hashAlgorithm);
        return hash;
    }

    private String calculateHash(String box) {
        LOG.debug("calculating [{}] hash for box [{}]", hashAlgorithm.name(), box);
        try (InputStream boxDataStream = Files.newInputStream(new File(box).toPath());
             InputStream digestInputStream = new DigestInputStream(boxDataStream, messageDigest)) {
            LOG.trace("buffering box data (buffer size [{}]b) ...", streamBufferLength);
            final byte[] buffer = new byte[streamBufferLength];
            //noinspection StatementWithEmptyBody
            while (digestInputStream.read(buffer) > 0) ;
        } catch (IOException e) {
            LOG.error("Error during processing file [{}], message: [{}]", box, e.getMessage());
            throw new RuntimeException(
                    "Error while getting checksum for file " + box + " reason: " + e.getMessage(), e);
        }

        return getHash(messageDigest.digest());
    }

    private String getHash(byte[] digestBytes) {
        return DatatypeConverter.printHexBinary(digestBytes).toLowerCase();
    }

    @Override
    public String toString() {
        return "FilesystemDigestHashService{" +
                "messageDigest=" + messageDigest +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        FilesystemDigestHashService that = (FilesystemDigestHashService) o;
        return messageDigest.getAlgorithm().equals(that.messageDigest.getAlgorithm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageDigest);
    }
}
