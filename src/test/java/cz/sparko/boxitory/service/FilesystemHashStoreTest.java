package cz.sparko.boxitory.service;

import cz.sparko.boxitory.factory.HashServiceFactory;
import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static cz.sparko.boxitory.service.HashService.HashAlgorithm.DISABLED;
import static cz.sparko.boxitory.service.HashService.HashAlgorithm.MD5;
import static cz.sparko.boxitory.service.HashService.HashAlgorithm.SHA1;
import static cz.sparko.boxitory.service.HashService.HashAlgorithm.SHA256;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FilesystemHashStoreTest {
    private final String TEST_HOME = "test_repository";
    private File testHomeDir;
    private String validBoxPath;

    private HashStore hashStore;

    @BeforeMethod
    public void setUpTest() throws IOException {
        this.hashStore = new FilesystemHashStore();

        testHomeDir = new File(TEST_HOME);

        createTestFolderStructure();
    }

    @AfterMethod
    public void tearDownTest() throws IOException {
        FileUtils.deleteDirectory(testHomeDir);
    }

    private void createTestFolderStructure() throws IOException {
        testHomeDir.mkdir();
        File f25 = new File(testHomeDir.getAbsolutePath() + "/f25");

        f25.mkdir();

        File f25box = new File(f25.getAbsolutePath() + "/f25_1_virtualbox.box");
        f25box.createNewFile();

        validBoxPath = f25box.getAbsolutePath();
    }

    @DataProvider
    public Object[][] validData() {
        return new Object[][]{{MD5}, {SHA1}, {SHA256}};
    }

    @Test(dataProvider = "validData")
    public void givenExistingBoxAndEnabledAlg_whenPersist_thenHashIsProperlyStored
            (HashAlgorithm algorithm) throws IOException {
        final String testHashValue = "blabol";

        this.hashStore.persist(validBoxPath, testHashValue, algorithm);

        File expectedFile = new File(validBoxPath + algorithm.getFileExtension());

        assertTrue(expectedFile.exists());
        assertTrue(expectedFile.isFile());

        List<String> expectedFileLines = Files.readAllLines(expectedFile.toPath(), Charset.defaultCharset());

        assertEquals(expectedFileLines.size(), 1);
        assertTrue(expectedFileLines.get(0).equals(testHashValue));
    }

    @Test(dataProvider = "validData")
    public void givenExistingBoxEnabledAlgAndFileAlreadyExist_whenPersist_thenOriginalFileIsNotReplaced
            (HashAlgorithm algorithm) throws IOException {
        final String testHashValue = "blabol";

        File existingFile = new File(validBoxPath + algorithm.getFileExtension());
        existingFile.createNewFile();
        try (FileWriter writer = new FileWriter(existingFile)) {
            writer.write(testHashValue);
        }


        this.hashStore.persist(validBoxPath, "some_new_hash", algorithm);

        assertTrue(existingFile.exists());
        assertTrue(existingFile.isFile());

        List<String> expectedFileLines = Files.readAllLines(existingFile.toPath(), Charset.defaultCharset());

        assertEquals(expectedFileLines.size(), 1);
        assertTrue(expectedFileLines.get(0).equals(testHashValue));
    }

    @Test
    public void givenExistingBoxAndDisabledAlg_whenPersist_thenNoFileStored() {
        final String testHashValue = "blabol";

        this.hashStore.persist(validBoxPath, testHashValue, DISABLED);

        File expectedFile = new File(validBoxPath + DISABLED.getFileExtension());
        assertFalse(expectedFile.exists());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void givenNonExistingBoxAndEnabledAlg_whenPersist_thenIllegalStateExceptionThrown() {
        this.hashStore.persist(validBoxPath + "noise", "blabol", MD5);
    }
}