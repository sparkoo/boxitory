package cz.sparko.boxitory.service;

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
                {"md5", new DigestHashService(MessageDigest.getInstance("MD5"))},
                {"sha1", new DigestHashService(MessageDigest.getInstance("SHA-1"))},
                {"sha256", new DigestHashService(MessageDigest.getInstance("SHA-256"))},
                {"disabled", new NoopHashService()}
        };
    }

    @Test(dataProvider = "hashServiceTypes")
    public void givenFactory_whenCreateHashService_thenGetExpectedInstance(String type, HashService expectedService) throws NoSuchAlgorithmException {
        HashService hashService = HashServiceFactory.createHashService(type);

        assertEquals(hashService, expectedService);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenFactory_whenCreateUnsupportedHashService_thenExceptionIsThrown() throws NoSuchAlgorithmException {
        HashService hashService = HashServiceFactory.createHashService("foo");
    }
}
