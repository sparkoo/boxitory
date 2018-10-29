package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.conf.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.domain.BoxProvider;
import cz.sparko.boxitory.model.BoxStream;
import cz.sparko.boxitory.service.filesystem.FilesystemBoxRepository;
import cz.sparko.boxitory.service.noop.NoopDescriptionProvider;
import cz.sparko.boxitory.service.noop.NoopHashService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@SpringBootTest
public class FilesystemBoxRepositoryTest {

    private final String TEST_HOME = "target" + File.separator + "test_repository";
    private final String TEST_BOX_PREFIX = "sftp://my_test_server:";
    private final String VERSION_DESCRIPTION = null;
    private File testHomeDir;

    private AppProperties testAppProperties;

    Map<String, Box> testBoxes;

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
        File f25 = new File(testHomeDir.getAbsolutePath() + File.separator + "f25");
        File f26 = new File(testHomeDir.getAbsolutePath() + File.separator + "f26");
        File f27 = new File(testHomeDir.getAbsolutePath() + File.separator + "f27");
        File f28 = new File(testHomeDir.getAbsolutePath() + File.separator + "f28");
        File f29 = new File(testHomeDir.getAbsolutePath() + File.separator + "f29");

        f25.mkdir();
        f26.mkdir();
        f27.mkdir();
        f28.mkdir();
        f29.mkdir();

        createDummyBoxFile(f25, "f25_1_virtualbox.box");
        createDummyBoxFile(f25, "f25_2_virtualbox.box");

        createDummyBoxFile(f26, "f26_1_virtualbox.box");
        createDummyBoxFile(f26, "f26_2_virtualbox.box");
        createDummyBoxFile(f26, "f26_3_virtualbox.box");

        createDummyBoxFile(f27, "wrongFileFormat.box");
        createDummyBoxFile(f27, "f27_3_virtualbox.boxf");
        createDummyBoxFile(f27, "f27_3_virtualbox");
        createDummyBoxFile(f27, "f27_3_virtualbox.bo");

        createDummyBoxFile(f28, "f28_1_virtualbox.box");
        createDummyBoxFile(f28, "f28_1_vmware.box");
        createDummyBoxFile(f28, "f28_2_virtualbox.box");

        createDummyBoxFile(f29, "f29_1_virtualbox.box");
        createDummyBoxFile(f29, "f29_3_virtualbox.box");
        createDummyBoxFile(f29, "f29_2_virtualbox.box");

        testBoxes = new HashMap<>();
        testBoxes.put("f25", new Box("f25", "f25",
                                     Arrays.asList(
                                             new BoxVersion("1", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f25", "1", "virtualbox")
                                             )),
                                             new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f25", "2", "virtualbox")
                                             ))
                                     )));
        testBoxes.put("f26", new Box("f26", "f26",
                                     Arrays.asList(
                                             new BoxVersion("1", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f26", "1", "virtualbox")
                                             )),
                                             new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f26", "2", "virtualbox")
                                             )),
                                             new BoxVersion("3", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f26", "3", "virtualbox")
                                             ))
                                     )));
        testBoxes.put("f28", new Box("f28", "f28",
                                     Arrays.asList(
                                             new BoxVersion("1", VERSION_DESCRIPTION, Arrays.asList(
                                                     createProvider("f28", "1", "virtualbox"),
                                                     createProvider("f28", "1", "vmware")
                                             )),
                                             new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f28", "2", "virtualbox")
                                             ))
                                     )));
        testBoxes.put("f29", new Box("f29", "f29",
                                     Arrays.asList(
                                             new BoxVersion("1", VERSION_DESCRIPTION, Arrays.asList(
                                                     createProvider("f29", "1", "virtualbox")
                                             )),
                                             new BoxVersion("2", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f29", "2", "virtualbox")
                                             )),
                                             new BoxVersion("3", VERSION_DESCRIPTION, Collections.singletonList(
                                                     createProvider("f29", "3", "virtualbox")
                                             ))
                                     )));
    }

    private void createDummyBoxFile(File boxDir, String filename) throws IOException {
        File boxFile = new File(boxDir.getAbsolutePath() + File.separator + filename);
        boxFile.createNewFile();
        FileUtils.writeByteArrayToFile(boxFile, filename.getBytes());
    }

    private BoxProvider createProvider(String boxName, String boxVersion, String boxProvider) {
        return new BoxProvider(composePath(boxName, boxVersion, boxProvider),
                               composeLocalPath(boxName, boxVersion, boxProvider),
                               boxProvider, null, null);
    }

    @DataProvider
    public Object[][] boxesMap() {
        return new Object[][]{
                {"f25", Optional.of(testBoxes.get("f25"))},
                {"f26", Optional.of(testBoxes.get("f26"))},
                {"f27", Optional.empty()},
                {"f28", Optional.of(testBoxes.get("f28"))},
                {"f29", Optional.of(testBoxes.get("f29"))},
                {"blabol", Optional.empty()},
                {"wrongBoxFileFormat", Optional.empty()}
        };
    }

    @Test(dataProvider = "boxesMap")
    public void givenRepository_whenGetBox_thenGetWhenFound(String boxName, Optional<Box> expectedResult) {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        Optional<Box> providedBox = boxRepository.getBox(boxName);

        assertEquals(providedBox.isPresent(), expectedResult.isPresent());
        expectedResult.ifPresent(box -> assertEquals(providedBox.get(), box));
    }

    @Test
    public void givenSortAscending_whenGetBox_thenVersionsSortedAsc() {
        testAppProperties.setSort_desc(false);

        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        List<BoxVersion> versions = boxRepository.getBox("f29").get().getVersions();
        assertEquals(versions.get(0).getVersion(), "1");
        assertEquals(versions.get(1).getVersion(), "2");
        assertEquals(versions.get(2).getVersion(), "3");
    }

    @Test
    public void givenSortDescending_whenGetBox_thenVersionsSortedDesc() {
        testAppProperties.setSort_desc(true);

        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        List<BoxVersion> versions = boxRepository.getBox("f29").get().getVersions();
        assertEquals(versions.get(0).getVersion(), "3");
        assertEquals(versions.get(1).getVersion(), "2");
        assertEquals(versions.get(2).getVersion(), "1");
    }

    @Test
    public void givenValidRepositoryWithBoxes_whenGetBoxNames_thenGetValidBoxes() {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        List<String> boxes = boxRepository.getBoxNames();
        assertTrue(boxes.containsAll(Arrays.asList("f25", "f26", "f28", "f29")));
        assertFalse(boxes.contains("f27"));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void givenNonExistingRepositoryDir_whenGetBoxNames_thenThrowNotFoundException() {
        testAppProperties.setHome("/some/not/existing/dir");
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );
        boxRepository.getBoxNames();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void givenNonExistingRepositoryDir_whenGetBox_thenThrowNotFoundException() {
        testAppProperties.setHome("/some/not/existing/dir");
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );
        boxRepository.getBox("invalid_repo_dir");
    }

    @Test
    public void givenValidRepositoryWithBoxes_whenGetBoxes_thenGetValidListOfBoxes() {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        List<Box> boxes = boxRepository.getBoxes();
        assertEquals(boxes.size(), testBoxes.size());
        assertTrue(boxes.containsAll(testBoxes.values()));
        assertTrue(testBoxes.values().containsAll(testBoxes.values()));
    }

    @DataProvider
    public Object[][] streamData() {
        return new Object[][]{
                {"f25", "virtualbox", "1", "f25_1_virtualbox.box"},
                {"f28", "virtualbox", "1", "f28_1_virtualbox.box"},
                {"f28", "vmware", "1", "f28_1_vmware.box"}
        };
    }

    @Test(dataProvider = "streamData")
    public void givenValidRepository_whenGetBoxStream_thenProperStreamReturned(String boxName,
                                                                               String boxProvider,
                                                                               String boxVersion,
                                                                               String expectedFilename) throws IOException {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        BoxStream boxStream = boxRepository.getBoxStream(boxName, boxProvider, boxVersion).get();
        assertEquals(boxStream.getFilename(), expectedFilename);
        byte[] boxFileContent = IOUtils.toByteArray(boxStream.getStream());
        assertEquals(new String(boxFileContent), expectedFilename);
    }

    @DataProvider
    public Object[][] invalidBoxProviderVersions() {
        return new Object[][]{
                {"f25", "virtualboxx", "1"},
                {"f2", "virtualbox", "1"},
                {"f25", "virtualbox", "1337"},
                {"", "", ""}
        };
    }

    @Test(expectedExceptions = NotFoundException.class, dataProvider = "invalidBoxProviderVersions")
    public void givenValidRepository_whenGetBoxStreamToInvalidBox_thenGetOptionalEmpty(
            String boxName,
            String boxProvider,
            String boxVersion
    ) {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        boxRepository.getBoxStream(boxName, boxProvider, boxVersion);
    }

    @Test
    public void givenValidRepository_whenGetLatestVersionExist_thenReturn() {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        assertEquals(boxRepository.latestVersionOfBox("f25", "virtualbox"), "2");
        assertEquals(boxRepository.latestVersionOfBox("f28", "virtualbox"), "2");
        assertEquals(boxRepository.latestVersionOfBox("f28", "vmware"), "1");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void givenValidRepository_whenGetLatestVersionBoxDontExist_thenThrowNotFoundException() {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        boxRepository.latestVersionOfBox("f24", "virtualbox");
    }

    @DataProvider
    public Object[][] invalidBoxProviders() {
        return new Object[][]{
                {"f25", "virtualboxx"},
                {"f2", "virtualbox"},
                {"", ""}
        };
    }

    @Test(expectedExceptions = NotFoundException.class, dataProvider = "invalidBoxProviders")
    public void givenValidRepository_whenGetLatestVersionProviderDontExist_thenThrowNotFoundException(
            String boxName,
            String boxProvider) {
        BoxRepository boxRepository = new FilesystemBoxRepository(
                testAppProperties, new NoopHashService(),
                new NoopDescriptionProvider()
        );

        boxRepository.latestVersionOfBox(boxName, boxProvider);
    }

    private String composePath(String boxName, String version, String provider) {
        return String.format("%s%s" + File.separator + "%s" + File.separator + "%s_%s_%s.box",
                             TEST_BOX_PREFIX,
                             testHomeDir.getAbsolutePath(),
                             boxName, boxName, version, provider);
    }

    private String composeLocalPath(String boxName, String version, String provider) {
        return String.format("%s" + File.separator + "%s" + File.separator + "%s_%s_%s.box",
                             testHomeDir.getAbsolutePath(),
                             boxName, boxName, version, provider);
    }
}