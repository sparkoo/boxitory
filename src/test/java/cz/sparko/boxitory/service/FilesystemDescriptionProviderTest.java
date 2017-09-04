package cz.sparko.boxitory.service;

import ch.qos.logback.core.util.FileUtil;
import cz.sparko.boxitory.conf.AppProperties;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.testng.Assert.*;

public class FilesystemDescriptionProviderTest {
    private final String TEST_HOME = "target/test_repository";
    private final String TEST_BOX_PREFIX = "sftp://my_test_server:";
    private File testHomeDir;

    private AppProperties testAppProperties;

    @BeforeClass
    public void setUp() throws IOException {
        testAppProperties = new AppProperties();
        testAppProperties.setHome(TEST_HOME);
        testAppProperties.setHost_prefix(TEST_BOX_PREFIX);
        testHomeDir = new File(TEST_HOME);
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
        return new Object[][] {
                {"f25", "1", "this is description of version 1"},
                {"f25", "1234", "this is description of version 1234"},
                {"f25", "2", "this is description of version 2"},
                {"f25", "56498981", "this is description of version 56498981"},
                {"f26", "17", "this is desc of v 17"}
        };
    }

    @Test(dataProvider = "validDescriptions")
    public void givenValidDescriptionFile_whenGetDescription_thenReturnProperDescription(String box, String version, String description) throws IOException {
        createDirWithValidDescriptions();

        DescriptionProvider descriptionProvider = new FilesystemDescriptionProvider(testHomeDir);

        assertEquals(descriptionProvider.getDescription(box, version), description);
    }

    private void createDirWithValidDescriptions() throws IOException {
        File f25 = new File(testHomeDir.getAbsolutePath() + "/f25");
        f25.mkdir();
        File descriptionFile = new File(f25.getAbsolutePath() + "/" + FilesystemDescriptionProvider.DESCRIPTIONS_FILE);
        writeFileHeader(descriptionFile);
        FileUtils.writeStringToFile(descriptionFile, "1;;;this is description of version 1\n", UTF_8, true);
        FileUtils.writeStringToFile(descriptionFile, "1234;;;this is description of version 1234\n", UTF_8, true);
        FileUtils.writeStringToFile(descriptionFile, "2;;;this is description of version 2\n", UTF_8, true);
        FileUtils.writeStringToFile(descriptionFile, "56498981;;;this is description of version 56498981\n", UTF_8, true);

        File f26 = new File(testHomeDir.getAbsolutePath() + "/f26");
        f26.mkdir();
        descriptionFile = new File(f26.getAbsolutePath() + "/" + FilesystemDescriptionProvider.DESCRIPTIONS_FILE);
        writeFileHeader(descriptionFile);
        FileUtils.writeStringToFile(descriptionFile, "17;;;this is desc of v 17\n", UTF_8, true);
    }

    private void writeFileHeader(File descriptionFile) throws IOException {
        FileUtils.writeStringToFile(descriptionFile, "version;;;description\n", UTF_8);
    }
}