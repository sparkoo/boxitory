package cz.sparko.boxitory.factory;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.service.FilesystemDigestHashService;
import cz.sparko.boxitory.service.HashService;
import cz.sparko.boxitory.service.HashStore;
import cz.sparko.boxitory.service.NoopHashService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashServiceFactory {

    public static HashService createHashService(AppProperties appProperties, HashStore hashStore) throws
            NoSuchAlgorithmException {
        HashAlgorithm algorithm = appProperties.getChecksum();
        if (algorithm == HashAlgorithm.DISABLED) {
            return new NoopHashService();
        } else {
            return new FilesystemDigestHashService(MessageDigest.getInstance(algorithm.getMessageDigestName()),
                    hashStore, appProperties);
        }
    }

    public enum HashAlgorithm {
        MD5("MD5", ".md5"),
        SHA1("SHA-1", ".sha1"),
        SHA256("SHA-256", ".sha256"),
        DISABLED("", ".noop");

        private final String messageDigestName;
        private final String fileExtension;

        HashAlgorithm(String messageDigestName, String fileExtension) {
            this.messageDigestName = messageDigestName;
            this.fileExtension = fileExtension;
        }

        public String getMessageDigestName() {
            return messageDigestName;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }
}
