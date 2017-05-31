package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.domain.BoxProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.testng.Assert.assertEquals;

@SpringBootTest
public class FilesystemBoxRepositoryTest {

    private final String TEST_HOME = "target/test-classes/test_repository";
    private final String TEST_BOX_PREFIX = "sftp://tester@hydra:";
    private File testHomeDir;

    private AppProperties testAppProperties;

    @BeforeClass
    public void setUp() {
        testAppProperties = new AppProperties();
        testAppProperties.setHome(TEST_HOME);
        testAppProperties.setHost_prefix(TEST_BOX_PREFIX);
        testHomeDir = new File(TEST_HOME);
    }

    @DataProvider
    public Object[][] boxes() {
        return new Object[][]{
                {"f25", Optional.of(new Box("f25", "f25",
                        Arrays.asList(
                                new BoxVersion("1", Collections.singletonList(
                                        new BoxProvider(composePath("f25", "1", "virtualbox"), "virtualbox")
                                )),
                                new BoxVersion("2", Collections.singletonList(
                                        new BoxProvider(composePath("f25", "2", "virtualbox"), "virtualbox")
                                ))
                        )))
                },
                {"f26", Optional.of(new Box("f26", "f26",
                        Arrays.asList(
                                new BoxVersion("1", Collections.singletonList(
                                        new BoxProvider(composePath("f26", "1", "virtualbox"), "virtualbox")
                                )),
                                new BoxVersion("2", Collections.singletonList(
                                        new BoxProvider(composePath("f26", "2", "virtualbox"), "virtualbox")
                                )),
                                new BoxVersion("3", Collections.singletonList(
                                        new BoxProvider(composePath("f26", "3", "virtualbox"), "virtualbox")
                                ))
                        )))
                },
                {"f27", Optional.empty()},
                {"f28", Optional.of(new Box("f28", "f28",
                        Arrays.asList(
                                new BoxVersion("1", Arrays.asList(
                                        new BoxProvider(composePath("f28", "1", "virtualbox"), "virtualbox"),
                                        new BoxProvider(composePath("f28", "1", "vmware"), "vmware")
                                )),
                                new BoxVersion("2", Collections.singletonList(
                                        new BoxProvider(composePath("f28",  "2", "virtualbox"), "virtualbox")
                                ))
                        )))
                },
                {"blabol", Optional.empty()},
                {"wrongBoxFileFormat", Optional.empty()}
        };
    }

    @Test(dataProvider = "boxes")
    public void givenRepository_whenGetBox_thenGetWhenFound(String boxName, Optional<Box> expectedResult) {
        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties);


        Optional<Box> providedBox = boxRepository.getBox(boxName);

        assertEquals(providedBox.isPresent(), expectedResult.isPresent());
        expectedResult.ifPresent(box -> assertEquals(providedBox.get(), box));
    }

    private String composePath(String boxName, String version, String providerName) {
        return String.format("%s%s/%s/%s_%s_%s.box", TEST_BOX_PREFIX, testHomeDir.getAbsolutePath(),
                boxName, boxName, version, providerName);
    }
}