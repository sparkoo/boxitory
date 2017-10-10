package cz.sparko.boxitory.factory;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.service.FilesystemDigestHashService;
import cz.sparko.boxitory.service.HashService.HashAlgoritm;
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
            return new FilesystemDigestHashService(
                    MessageDigest.getInstance(algorithm.getMessageDigestName()), appProperties);
        }
    }
}
