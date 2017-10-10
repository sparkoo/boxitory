package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.factory.HashServiceFactory;
import cz.sparko.boxitory.service.HashService.HashAlgorithm;
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
                {HashAlgorithm.MD5, new FilesystemDigestHashService(MessageDigest.getInstance("MD5"),new NoopHashStore(), new AppProperties())},
                {HashAlgorithm.SHA1, new FilesystemDigestHashService(MessageDigest.getInstance("SHA-1"),new NoopHashStore(), new AppProperties())},
                {HashAlgorithm.SHA256, new FilesystemDigestHashService(MessageDigest.getInstance("SHA-256"),new NoopHashStore(), new AppProperties())},
                {HashAlgorithm.DISABLED, new NoopHashService()}
        };
    }

    @Test(dataProvider = "hashServiceTypes")
    public void givenFactory_whenCreateHashService_thenGetExpectedInstance(HashAlgorithm type,
                                                                           HashService
            expectedService)
            throws NoSuchAlgorithmException {
        AppProperties appProperties = new AppProperties();
        appProperties.setChecksum(type);
        HashService hashService = HashServiceFactory.createHashService(appProperties, new NoopHashStore());

        assertEquals(hashService, expectedService);
    }
}
