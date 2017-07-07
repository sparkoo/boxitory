package cz.sparko.boxitory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Objects;

public class DigestHashService implements HashService {

    private static final Logger LOG = LoggerFactory.getLogger(DigestHashService.class);
    private MessageDigest messageDigest;

    public DigestHashService(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    @Override
    public String getHashType() {
        return messageDigest.getAlgorithm().replaceAll("-", "").toLowerCase();
    }

    @Override
    public String getChecksum(String string) {
        byte[] bytes;
        File file = new File(string);
        try {
            bytes = getByteArrayFromFile(file);
        } catch (IOException e) {
            LOG.error("Error during processing file [{}], message: [{}]", file, e.getMessage());
            return "";
        }

        return getHash(
                getDigestBytes(bytes)
        );
    }

    private byte[] getByteArrayFromFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    private byte[] getDigestBytes(byte[] bytes) {
        messageDigest.update(bytes);
        return messageDigest.digest();
    }

    private String getHash(byte[] diggestBytes) {
        return DatatypeConverter.printHexBinary(diggestBytes).toLowerCase();
    }

    @Override
    public String toString() {
        return "DigestHashService{" +
                "messageDigest=" + messageDigest +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DigestHashService that = (DigestHashService) o;
        return messageDigest.getAlgorithm().equals(that.messageDigest.getAlgorithm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageDigest);
    }
}
