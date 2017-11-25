package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.service.filesystem.FilesystemDescriptionProvider;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static cz.sparko.boxitory.service.filesystem.FilesystemDescriptionProvider.DESCRIPTIONS_FILE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class FilesystemDescriptionProviderTest {
    private final String TEST_HOME = "target/test_repository";
    private final String TEST_BOX_PREFIX = "sftp://my_test_server:";
    private File testHomeDir;

    private AppProperties testAppProperties;

    private DescriptionProvider descriptionProvider;

    @BeforeClass
    public void setUp() {
        testAppProperties = new AppProperties();
        testAppProperties.setHome(TEST_HOME);
        testAppProperties.setHost_prefix(TEST_BOX_PREFIX);
        testHomeDir = new File(TEST_HOME);

        descriptionProvider = new FilesystemDescriptionProvider(
                testHomeDir,
                testAppProperties.getVersion_as_timestamp()
        );
    }

    @BeforeMethod
    public void createTestHomedir() {
        testHomeDir.mkdir();
    }

    @AfterMethod
    public void cleanTestHomedir() throws IOException {
        FileUtils.deleteDirectory(testHomeDir);
    }

    @DataProvider
    public Object[][] validDescriptions() {
        return new Object[][]{
                {"f25", "1", "this is description of version 1"},
                {"f25", "1234", "this is description of version 1234"},
                {"f25", "2", "this is description of version 2"},
                {"f25", "56498981", "this is description of version 56498981"},
                {"f26", "17", "this is desc of v 17"}
        };
    }

    @Test(dataProvider = "validDescriptions")
    public void givenValidDescriptionFile_whenGetDescription_thenReturnProperDescription(String box, String version,
                                                                                         String description)
            throws IOException {
        createDirWithValidDescriptions();

        assertEquals(descriptionProvider.getDescription(box, version).get(), description);
    }

    @Test
    public void givenNoDescriptionForVersion_whenGetDescription_thenReturnNull() throws IOException {
        createDirWithValidDescriptions();

        assertFalse(descriptionProvider.getDescription("f25", "666").isPresent());
    }

    @Test
    public void givenMultipleDescriptionsForVersion_whenGetDescription_thenReturnLatest() throws IOException {
        createDirWithValidDescriptions();
        File f25 = new File(testHomeDir.getAbsolutePath() + "/f25");
        File descriptionFile = new File(f25.getAbsolutePath() + "/" + DESCRIPTIONS_FILE);
        writeStringToFile(descriptionFile, "1;;;this is second description of version 1\n", UTF_8, true);
        writeStringToFile(descriptionFile, "2;;;this is second description of version 2\n", UTF_8, true);

        assertEquals(descriptionProvider.getDescription("f25", "1").get(), "this is second description of version 1");
        assertEquals(descriptionProvider.getDescription("f25", "2").get(), "this is second description of version 2");
    }

    @Test
    public void givenInvalidFileButOneLineMatches_whenGet_thenReturnValidDescription() throws IOException {
        File versionFile = createDescriptionFileForBox("f27", false);
        writeStringToFile(versionFile, "blablabla blebleble fbsajl lsa\n", UTF_8, true);
        writeStringToFile(versionFile, "sfqfqs;;;qweeeee\n", UTF_8, true);
        writeStringToFile(versionFile, "1;;;this is valid line\n", UTF_8, true);
        writeStringToFile(versionFile, "\n", UTF_8, true);
        writeStringToFile(versionFile, "1234564789\n", UTF_8, true);
        writeStringToFile(versionFile, "     \n", UTF_8, true);

        assertEquals(descriptionProvider.getDescription("f27", "1").get(), "this is valid line");
    }

    @Test
    public void givenInvalidFile_whenGet_thenReturnNull() throws IOException {
        File versionFile = createDescriptionFileForBox("f27", false);
        writeStringToFile(versionFile, "blablabla blebleble fbsajl lsa\n", UTF_8, true);
        writeStringToFile(versionFile, "sfqfqs;;;qweeeee\n", UTF_8, true);
        writeStringToFile(versionFile, "1;;this is valid line\n", UTF_8, true);
        writeStringToFile(versionFile, "\n", UTF_8, true);
        writeStringToFile(versionFile, "1234564789\n", UTF_8, true);
        writeStringToFile(versionFile, "     \n", UTF_8, true);

        assertFalse(descriptionProvider.getDescription("f27", "1").isPresent());
    }

    @Test
    public void givenNoFileExists_whenGet_thenReturnNull() throws IOException {
        createDirWithValidDescriptions();
        assertFalse(descriptionProvider.getDescription("f27", "1").isPresent());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenNullBox_whenGet_thenThrowIae() {
        descriptionProvider.getDescription(null, "1");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenNullVersion_whenGet_thenThrowIae() {
        descriptionProvider.getDescription("1", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenEmptyBox_whenGet_thenThrowIae() {
        descriptionProvider.getDescription("", "1");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenEmptyVersion_whenGet_thenThrowIae() {
        descriptionProvider.getDescription("1", "");
    }

    private void createDirWithValidDescriptions() throws IOException {
        File descriptionFile = createDescriptionFileForBox("f25", true);
        writeStringToFile(descriptionFile, "1;;;this is description of version 1\n", UTF_8, true);
        writeStringToFile(descriptionFile, "1234;;;this is description of version 1234\n", UTF_8, true);
        writeStringToFile(descriptionFile, "2;;;this is description of version 2\n", UTF_8, true);
        writeStringToFile(descriptionFile, "56498981;;;this is description of version 56498981\n", UTF_8, true);

        descriptionFile = createDescriptionFileForBox("f26", true);
        writeFileHeader(descriptionFile);
        writeStringToFile(descriptionFile, "17;;;this is desc of v 17\n", UTF_8, true);
    }

    private File createDescriptionFileForBox(String box, boolean header) throws IOException {
        File boxDir = new File(testHomeDir.getAbsolutePath() + "/" + box);
        boxDir.mkdir();
        File descriptionsFile = new File(boxDir.getAbsolutePath() + "/" + DESCRIPTIONS_FILE);
        if (header) {
            writeFileHeader(descriptionsFile);
        }
        return descriptionsFile;
    }

    private void writeFileHeader(File descriptionFile) throws IOException {
        writeStringToFile(descriptionFile, "version;;;description\n", UTF_8);
    }
}