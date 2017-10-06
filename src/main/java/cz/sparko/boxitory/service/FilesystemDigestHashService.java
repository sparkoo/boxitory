package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.factory.HashServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Objects;

public class FilesystemDigestHashService implements HashService {

    private static final Logger LOG = LoggerFactory.getLogger(FilesystemDigestHashService.class);
    private final MessageDigest messageDigest;
    private final int streamBufferLength;
    private final HashStore hashStore;
    private final HashServiceFactory.HashAlgorithm hashAlgorithm;

    public FilesystemDigestHashService(MessageDigest messageDigest, HashStore hashStore, AppProperties appProperties) {
        this.hashAlgorithm = appProperties.getChecksum();
        this.messageDigest = messageDigest;
        streamBufferLength = appProperties.getChecksum_buffer_size();
        this.hashStore = hashStore;
    }

    @Override
    public String getHashType() {
        return messageDigest.getAlgorithm().replaceAll("-", "").toLowerCase();
    }

    @Override
    public String getChecksum(String string) {
        try (InputStream boxDataStream = Files.newInputStream(new File(string).toPath())) {
            LOG.trace("buffering box data (buffer size [{}]b) ...", streamBufferLength);
            final byte[] buffer = new byte[streamBufferLength];
            int read = boxDataStream.read(buffer, 0, streamBufferLength);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = boxDataStream.read(buffer, 0, streamBufferLength);
            }
        } catch (IOException e) {
            LOG.error("Error during processing file [{}], message: [{}]", string, e.getMessage());
            throw new RuntimeException(
                    "Error while getting checksum for file " + string + " reason: " + e.getMessage(), e
            );
        }

        String calculatedHash = getHash(messageDigest.digest());
        hashStore.persist(string, calculatedHash, hashAlgorithm);
        return calculatedHash;
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
