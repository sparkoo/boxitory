package cz.sparko.boxitory.service;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import cz.sparko.boxitory.service.filesystem.FilesystemHashStore;
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
import java.util.Optional;

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
    private String validBoxAbsPath;
    private String validBoxFilename;


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

        validBoxFilename = "f25_1_virtualbox.box";

        File f25box = new File(f25.getAbsolutePath() + "/" + validBoxFilename);
        f25box.createNewFile();

        validBoxAbsPath = f25box.getAbsolutePath();
    }

    @DataProvider
    public Object[][] validData() {
        return new Object[][]{{MD5}, {SHA1}, {SHA256}};
    }

    @Test(dataProvider = "validData")
    public void givenExistingBoxAndEnabledAlg_whenPersist_thenHashIsProperlyStored
            (HashAlgorithm algorithm) throws IOException {
        final String testHashValue = "blabol";

        this.hashStore.persist(validBoxAbsPath, testHashValue, algorithm);

        File expectedFile = new File(validBoxAbsPath + algorithm.getFileExtension());

        assertTrue(expectedFile.exists());
        assertTrue(expectedFile.isFile());

        List<String> expectedFileLines = Files.readAllLines(expectedFile.toPath(), Charset.defaultCharset());

        assertEquals(expectedFileLines.size(), 1);
        assertTrue(expectedFileLines.get(0).split("  ")[0].equals(testHashValue));
    }

    @Test(dataProvider = "validData")
    public void givenExistingBoxEnabledAlgAndFileAlreadyExist_whenPersist_thenOriginalFileIsNotReplaced
            (HashAlgorithm algorithm) throws IOException {
        final String testHashValue = "blabol";

        File existingFile = new File(validBoxAbsPath + algorithm.getFileExtension());
        existingFile.createNewFile();
        try (FileWriter writer = new FileWriter(existingFile)) {
            writer.write(testHashValue);
        }


        this.hashStore.persist(validBoxAbsPath, "some_new_hash", algorithm);

        assertTrue(existingFile.exists());
        assertTrue(existingFile.isFile());

        List<String> expectedFileLines = Files.readAllLines(existingFile.toPath(), Charset.defaultCharset());

        assertEquals(expectedFileLines.size(), 1);
        assertTrue(expectedFileLines.get(0).equals(testHashValue));
    }

    @Test
    public void givenExistingBoxAndDisabledAlg_whenPersist_thenNoFileStored() {
        final String testHashValue = "blabol";

        this.hashStore.persist(validBoxAbsPath, testHashValue, DISABLED);

        File expectedFile = new File(validBoxAbsPath + DISABLED.getFileExtension());
        assertFalse(expectedFile.exists());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void givenNonExistingBoxAndEnabledAlg_whenPersist_thenIllegalStateExceptionThrown() {
        this.hashStore.persist(validBoxAbsPath + "noise", "blabol", MD5);
    }

    @DataProvider
    public Object[][] wrongHashFileContentFormats() {
        return new Object[][] {
                {"this_is_hash  this_file_does_not_exists"},
                {"this_is_just_hash_without_filename"}
        };
    }

    @Test(dataProvider = "wrongHashFileContentFormats")
    public void givenWrongFileFormat_whenLoad_thenReturnEmpty(String content) throws IOException {
        HashAlgorithm algorithm = MD5;
        createTestHashFile(content, validBoxAbsPath + algorithm.getFileExtension());

        Optional<String> loadedHash = hashStore.loadHash(validBoxAbsPath, algorithm);
        assertFalse(loadedHash.isPresent(), "Should be empty, but is[" + loadedHash.orElse("") + "]");
    }

    @Test
    public void givenValidHashFile_whenLoad_thenProperlyLoaded() throws IOException {
        HashAlgorithm algorithm = MD5;
        final String hash = "this_is_fake_hash";
        createTestHashFile(hash + "  " + validBoxFilename, validBoxAbsPath + algorithm.getFileExtension());

        Optional<String> loadedHash = hashStore.loadHash(validBoxAbsPath, algorithm);
        assertTrue(loadedHash.isPresent());
        assertEquals(loadedHash.get(), hash, "hash is properly loaded " + loadedHash.get());
    }

    @Test
    public void givenHashFileWithWrongName_whenLoad_thenNothingLoaded() throws IOException {
        HashAlgorithm algorithm = MD5;
        final String hash = "this_is_fake_hash";
        createTestHashFile(hash + "  " + validBoxFilename, validBoxAbsPath + algorithm.getFileExtension() + "_some_noise");

        Optional<String> loadedHash = hashStore.loadHash(validBoxAbsPath, algorithm);
        assertFalse(loadedHash.isPresent());
    }

    @Test
    public void givenHashFileWithDifferentExtension_whenLoad_thenNothingLoaded() throws IOException {
        HashAlgorithm algorithm = MD5;
        final String hash = "this_is_fake_hash";
        createTestHashFile(hash + "  " + validBoxFilename, validBoxAbsPath + SHA1.getFileExtension());

        Optional<String> loadedHash = hashStore.loadHash(validBoxAbsPath, algorithm);
        assertFalse(loadedHash.isPresent());
    }

    private File createTestHashFile(String content, String fileFullPath) throws IOException {
        File hashFile = new File(fileFullPath);
        try (FileWriter fileWriter = new FileWriter(hashFile)) {
            fileWriter.write(content);
        }
        return hashFile;
    }
}