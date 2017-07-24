package cz.sparko.boxitory.factory;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.service.FilesystemDigestHashService;
import cz.sparko.boxitory.service.NoopHashService;
import cz.sparko.boxitory.service.HashService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashServiceFactory {

    public static HashService createHashService(AppProperties appProperties) throws NoSuchAlgorithmException {
        String algorithm = appProperties.getChecksum().toUpperCase();

        switch (algorithm) {
            case "MD5":
                return new FilesystemDigestHashService(MessageDigest.getInstance(algorithm), appProperties);
            case "SHA1":
                return new FilesystemDigestHashService(MessageDigest.getInstance("SHA-1"), appProperties);
            case "SHA256":
                return new FilesystemDigestHashService(MessageDigest.getInstance("SHA-256"), appProperties);
            case "DISABLED":
                return new NoopHashService();
            default:
                throw new IllegalArgumentException(
                        "Configured checksum type (box.checksum=" + algorithm + ") is not supported"
                );
        }
    }
}
