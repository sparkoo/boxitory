package cz.sparko.boxitory.service;

import java.util.Arrays;
import java.util.List;

public interface HashService {
    String getHashType();

    String getChecksum(String box);

    String getChecksum(String box, boolean canCalculate);

    enum HashAlgorithm {
        MD5("MD5", ".md5", "md5"),
        SHA1("SHA-1", ".sha1", "sha1"),
        SHA256("SHA-256", ".sha256", "sha256"),
        DISABLED("", ".noop", "");

        public final static List<HashAlgorithm> CHECKSUMS = Arrays.asList(MD5, SHA1, SHA256);

        private final String messageDigestName;
        private final String fileExtension;
        private final String vagrantInterfaceName;

        HashAlgorithm(String messageDigestName, String fileExtension, String vagrantInterfaceName) {
            this.messageDigestName = messageDigestName;
            this.fileExtension = fileExtension;
            this.vagrantInterfaceName = vagrantInterfaceName;
        }

        public String getMessageDigestName() {
            return messageDigestName;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public String getVagrantInterfaceName() {
            return vagrantInterfaceName;
        }
    }
}
