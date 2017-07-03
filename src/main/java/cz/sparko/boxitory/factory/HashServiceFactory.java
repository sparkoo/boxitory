package cz.sparko.boxitory.factory;

import cz.sparko.boxitory.service.BlankHashService;
import cz.sparko.boxitory.service.DigestHashService;
import cz.sparko.boxitory.service.HashService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashServiceFactory {

    public HashService createHashService(String algorithm) throws NoSuchAlgorithmException {
        algorithm = algorithm.toUpperCase();

        switch (algorithm) {
            case "MD5":
                return new DigestHashService(MessageDigest.getInstance(algorithm));
            case "SHA1":
                return new DigestHashService(MessageDigest.getInstance("SHA-1"));
            case "SHA256":
                return new DigestHashService(MessageDigest.getInstance("SHA-256"));
            case "DISABLED":
                return new BlankHashService();
            default:
                throw new IllegalArgumentException(
                        "Configured checksum type (box.checksum=" + algorithm + ") is not supported"
                );
        }
    }
}
