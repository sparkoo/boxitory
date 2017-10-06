package cz.sparko.boxitory.factory;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.service.FilesystemDigestHashService;
import cz.sparko.boxitory.service.NoopHashService;
import cz.sparko.boxitory.service.HashService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashServiceFactory {

    public static HashService createHashService(AppProperties appProperties) throws NoSuchAlgorithmException {
        HashAlgoritm algorithm = appProperties.getChecksum();
        if (algorithm == HashAlgoritm.DISABLED) {
            return new NoopHashService();
        } else {
            return new FilesystemDigestHashService(MessageDigest.getInstance(algorithm.getMessageDigestName()), appProperties);
        }
    }

    public enum HashAlgoritm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        DISABLED("");

        private final String messageDigestName;

        HashAlgoritm(String messageDigestName) {
            this.messageDigestName = messageDigestName;
        }

        public String getMessageDigestName() {
            return messageDigestName;
        }
    }
}
