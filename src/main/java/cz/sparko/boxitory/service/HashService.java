package cz.sparko.boxitory.service;

public interface HashService {
    String getHashType();
    String getChecksum(String box);

    enum HashAlgorithm {
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
