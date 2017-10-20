package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.domain.BoxProvider;
import cz.sparko.boxitory.service.filesystem.FilesystemBoxRepository;
import cz.sparko.boxitory.service.noop.NoopDescriptionProvider;
import cz.sparko.boxitory.service.noop.NoopHashService;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@SpringBootTest
public class FilesystemBoxRepositoryTest {

    private final String TEST_HOME = "target/test_repository";
    private final String TEST_BOX_PREFIX = "sftp://my_test_server:";
    private final String VERSION_DESCRIPTION = null;
    private File testHomeDir;

    private AppProperties testAppProperties;

    @BeforeClass
    public void setUp() throws IOException {
        testHomeDir = new File(TEST_HOME);

        createTestFolderStructure();
    }

    @BeforeMethod
    public void testSetUp() {
        testAppProperties = new AppProperties();
        testAppProperties.setHome(TEST_HOME);
        testAppProperties.setHost_prefix(TEST_BOX_PREFIX);
    }

    @AfterClass
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(testHomeDir);
    }

    private void createTestFolderStructure() throws IOException {
        testHomeDir.mkdir();
        File f25 = new File(testHomeDir.getAbsolutePath() + "/f25");
        File f26 = new File(testHomeDir.getAbsolutePath() + "/f26");
        File f27 = new File(testHomeDir.getAbsolutePath() + "/f27");
        File f28 = new File(testHomeDir.getAbsolutePath() + "/f28");
        File f29 = new File(testHomeDir.getAbsolutePath() + "/f29");

        f25.mkdir();
        f26.mkdir();
        f27.mkdir();
        f28.mkdir();
        f29.mkdir();

        new File(f25.getAbsolutePath() + "/f25_1_virtualbox.box").createNewFile();
        new File(f25.getAbsolutePath() + "/f25_2_virtualbox.box").createNewFile();

        new File(f26.getAbsolutePath() + "/f26_1_virtualbox.box").createNewFile();
        new File(f26.getAbsolutePath() + "/f26_2_virtualbox.box").createNewFile();
        new File(f26.getAbsolutePath() + "/f26_3_virtualbox.box").createNewFile();

        new File(f27.getAbsolutePath() + "/wrongFileFormat.box").createNewFile();

        new File(f28.getAbsolutePath() + "/f28_1_virtualbox.box").createNewFile();
        new File(f28.getAbsolutePath() + "/f28_1_vmware.box").createNewFile();
        new File(f28.getAbsolutePath() + "/f28_2_virtualbox.box").createNewFile();

        new File(f29.getAbsolutePath() + "/f29_1_virtualbox.box").createNewFile();
        new File(f29.getAbsolutePath() + "/f29_3_virtualbox.box").createNewFile();
        new File(f29.getAbsolutePath() + "/f29_2_virtualbox.box").createNewFile();
    }

    @DataProvider
    public Object[][] boxes() {
        return new Object[][]{
                {"f25", Optional.of(new Box("f25", "f25",
                        Arrays.asList(
                                new BoxVersion("1", VERSION_DESCRIPTION, Collections.singletonList(
                                        new BoxProvider(composePath("f25", "1", "virtualbox"),
                                                "virtualbox", null, null)
                                )),
                                new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                        new BoxProvider(composePath("f25", "2", "virtualbox"),
                                                "virtualbox", null, null)
                                ))
                        )))
                },
                {"f26", Optional.of(new Box("f26", "f26",
                        Arrays.asList(
                                new BoxVersion("1", VERSION_DESCRIPTION, Collections.singletonList(
                                        new BoxProvider(composePath("f26", "1", "virtualbox"),
                                                "virtualbox", null, null)
                                )),
                                new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                        new BoxProvider(composePath("f26", "2", "virtualbox"),
                                                "virtualbox", null, null)
                                )),
                                new BoxVersion("3", VERSION_DESCRIPTION, Collections.singletonList(
                                        new BoxProvider(composePath("f26", "3", "virtualbox"),
                                                "virtualbox", null, null)
                                ))
                        )))
                },
                {"f27", Optional.empty()},
                {"f28", Optional.of(new Box("f28", "f28",
                        Arrays.asList(
                                new BoxVersion("1", VERSION_DESCRIPTION, Arrays.asList(
                                        new BoxProvider(composePath("f28", "1", "virtualbox"),
                                                "virtualbox", null, null),
                                        new BoxProvider(composePath("f28", "1", "vmware"),
                                                "vmware", null, null)
                                )),
                                new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                        new BoxProvider(composePath("f28", "2", "virtualbox"),
                                                "virtualbox", null, null)
                                ))
                        )))
                },
                {"blabol", Optional.empty()},
                {"wrongBoxFileFormat", Optional.empty()}
        };
    }

    @Test(dataProvider = "boxes")
    public void givenRepository_whenGetBox_thenGetWhenFound(String boxName, Optional<Box> expectedResult) {
        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider());

        Optional<Box> providedBox = boxRepository.getBox(boxName);

        assertEquals(providedBox.isPresent(), expectedResult.isPresent());
        expectedResult.ifPresent(box -> assertEquals(providedBox.get(), box));
    }

    @Test
    public void givenSortAscending_whenGetBox_thenVersionsSortedAsc() {
        testAppProperties.setSort_desc(false);

        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider());

        List<BoxVersion> versions = boxRepository.getBox("f29").get().getVersions();
        assertEquals(versions.get(0).getVersion(), "1");
        assertEquals(versions.get(1).getVersion(), "2");
        assertEquals(versions.get(2).getVersion(), "3");
    }

    @Test
    public void givenSortDescending_whenGetBox_thenVersionsSortedDesc() {
        testAppProperties.setSort_desc(true);

        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider());

        List<BoxVersion> versions = boxRepository.getBox("f29").get().getVersions();
        assertEquals(versions.get(0).getVersion(), "3");
        assertEquals(versions.get(1).getVersion(), "2");
        assertEquals(versions.get(2).getVersion(), "1");
    }

    @Test
    public void givenValidRepositoryWithBoxes_whenGetBoxes_thenGetValidBoxes() {
        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider());

        List<String> boxes = boxRepository.getBoxes();
        assertTrue(boxes.containsAll(Arrays.asList("f25", "f26", "f28", "f29")));
        assertFalse(boxes.containsAll(Collections.singletonList("f27")));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void givenNonExistingRepositoryDir_whenGetBoxes_thenThrowNotFoundException() {
        testAppProperties.setHome("/some/not/existing/dir");
        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider());
        boxRepository.getBoxes();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void givenNonExistingRepositoryDir_whenGetBox_thenThrowNotFoundException() {
        testAppProperties.setHome("/some/not/existing/dir");
        BoxRepository boxRepository = new FilesystemBoxRepository(testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider());
        boxRepository.getBox("invalid_repo_dir");
    }

    private String composePath(String boxName, String version, String provider) {
        return String.format("%s%s/%s/%s_%s_%s.box", TEST_BOX_PREFIX, testHomeDir.getAbsolutePath(),
                boxName, boxName, version, provider);
    }
}