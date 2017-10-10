package cz.sparko.boxitory.service;

public interface HashService {
    String getHashType();
    String getChecksum(String string);

    enum HashAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        DISABLED("");

        private final String messageDigestName;

        HashAlgorithm(String messageDigestName) {
            this.messageDigestName = messageDigestName;
        }

        public String getMessageDigestName() {
            return messageDigestName;
        }
    }
}
