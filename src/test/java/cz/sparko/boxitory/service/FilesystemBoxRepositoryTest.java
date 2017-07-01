package cz.sparko.boxitory.service;

import ch.qos.logback.core.util.FileUtil;
import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.domain.BoxProvider;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.testng.Assert.assertEquals;

@SpringBootTest
public class FilesystemBoxRepositoryTest {

    private final String TEST_HOME = "test_repository";
    private final String TEST_BOX_PREFIX = "sftp://my_test_server:";
    private File testHomeDir;

    private AppProperties testAppProperties;

    @BeforeClass
    public void setUp() throws IOException {
        testAppProperties = new AppProperties();
        testAppProperties.setHome(TEST_HOME);
        testAppProperties.setHost_prefix(TEST_BOX_PREFIX);
        testHomeDir = new File(TEST_HOME);

        createTestFolderStructure();
    }

    private void createTestFolderStructure() throws IOException {
        testHomeDir.mkdir();
        File f25 = new File(testHomeDir.getAbsolutePath() + "/f25");
        File f26 = new File(testHomeDir.getAbsolutePath() + "/f26");
        File f27 = new File(testHomeDir.getAbsolutePath() + "/f27");

        f25.mkdir();
        f26.mkdir();
        f27.mkdir();

        new File(f25.getAbsolutePath() + "/f25_1_virtualbox.box").createNewFile();
        new File(f25.getAbsolutePath() + "/f25_2_virtualbox.box").createNewFile();

        new File(f26.getAbsolutePath() + "/f26_1_virtualbox.box").createNewFile();
        new File(f26.getAbsolutePath() + "/f26_2_virtualbox.box").createNewFile();
        new File(f26.getAbsolutePath() + "/f26_3_virtualbox.box").createNewFile();

        new File(f27.getAbsolutePath() + "/wrongFileFormat.box").createNewFile();
    }

    @AfterClass
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(testHomeDir);
    }

    @DataProvider
    public Object[][] boxes() {
        return new Object[][]{
                {"f25", Optional.of(new Box("f25", "f25",
                        Arrays.asList(
                                new BoxVersion("1", Collections.singletonList(new BoxProvider(composePath
                                        ("f25", "1", "virtualbox"),
                                        "virtualbox"))),
                                new BoxVersion("2", Collections.singletonList(new BoxProvider(composePath("f25", "2", "virtualbox"),
                                        "virtualbox")))
                        )))},
                {"f26", Optional.of(new Box("f26", "f26",
                        Arrays.asList(new BoxVersion("1", Collections.singletonList(new BoxProvider(composePath
                                        ("f26", "1", "virtualbox"),
                                        "virtualbox"))),
                                new BoxVersion("2", Collections.singletonList(new BoxProvider(composePath("f26", "2", "virtualbox"),
                                        "virtualbox"))),
                                new BoxVersion("3", Collections.singletonList(new BoxProvider(composePath("f26", "3", "virtualbox"),
                                        "virtualbox")))
                        )))},
                {"f27", Optional.empty()},
                {"blabol", Optional.empty()}
        };
    }

    @Test(dataProvider = "boxes")
    public void givenRepository_whenGetBox_thenGetWhenFound(String boxName, Optional<Box> expectedResult) {
        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new BlankHashService());


        Optional<Box> providedBox = boxRepository.getBox(boxName);

        assertEquals(providedBox.isPresent(), expectedResult.isPresent());
        expectedResult.ifPresent(box -> assertEquals(providedBox.get(), box));
    }

    private String composePath(String boxName, String version, String provider) {
        return String.format("%s%s/%s/%s_%s_%s.box", TEST_BOX_PREFIX, testHomeDir.getAbsolutePath(),
                boxName, boxName, version, provider);
    }
}