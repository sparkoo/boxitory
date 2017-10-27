package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.factory.HashServiceFactory;
import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import cz.sparko.boxitory.service.filesystem.FilesystemDigestHashService;
import cz.sparko.boxitory.service.noop.NoopHashService;
import cz.sparko.boxitory.service.noop.NoopHashStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static cz.sparko.boxitory.service.HashService.HashAlgorithm.DISABLED;
import static cz.sparko.boxitory.service.HashService.HashAlgorithm.MD5;
import static cz.sparko.boxitory.service.HashService.HashAlgorithm.SHA1;
import static cz.sparko.boxitory.service.HashService.HashAlgorithm.SHA256;
import static org.testng.Assert.assertEquals;

@SpringBootTest
public class HashServiceFactoryTest {

    @DataProvider
    public Object[][] hashServiceTypes() {
        return new Object[][]{
                {MD5, new FilesystemDigestHashService(MD5)},
                {SHA1, new FilesystemDigestHashService(SHA1)},
                {SHA256, new FilesystemDigestHashService(SHA256)},
                {DISABLED, new NoopHashService()}
        };
    }

    @Test(dataProvider = "hashServiceTypes")
    public void givenFactory_whenCreateHashService_thenGetExpectedInstance(HashAlgorithm type,
                                                                           HashService expectedService) {
        AppProperties appProperties = new AppProperties();
        appProperties.setChecksum(type);
        HashService hashService = HashServiceFactory.createHashService(appProperties, new NoopHashStore());

        assertEquals(hashService.getHashType(), expectedService.getHashType());
    }
}
