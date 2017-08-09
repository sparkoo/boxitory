package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.factory.HashServiceFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import static org.testng.Assert.assertEquals;

@SpringBootTest
public class HashServiceFactoryTest {

    @DataProvider
    public Object[][] hashServiceTypes() throws NoSuchAlgorithmException {
        return new Object[][]{
                {"md5", new FilesystemDigestHashService(MessageDigest.getInstance("MD5"), new AppProperties())},
                {"sha1", new FilesystemDigestHashService(MessageDigest.getInstance("SHA-1"), new AppProperties())},
                {"sha256", new FilesystemDigestHashService(MessageDigest.getInstance("SHA-256"), new AppProperties())},
                {"disabled", new NoopHashService()}
        };
    }

    @Test(dataProvider = "hashServiceTypes")
    public void givenFactory_whenCreateHashService_thenGetExpectedInstance(String type, HashService expectedService) throws NoSuchAlgorithmException {
        AppProperties appProperties = new AppProperties();
        appProperties.setChecksum(type);
        HashService hashService = HashServiceFactory.createHashService(appProperties);

        assertEquals(hashService, expectedService);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenFactory_whenCreateUnsupportedHashService_thenExceptionIsThrown() throws NoSuchAlgorithmException {
        AppProperties appProperties = new AppProperties();
        appProperties.setChecksum("foo");
        HashServiceFactory.createHashService(appProperties);
    }
}
