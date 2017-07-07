package cz.sparko.boxitory.service;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.testng.Assert.assertEquals;

@SpringBootTest
public class DigestHashServiceTest {

    private final String TEST_HOME = "test_repository";
    private File testHomeDir;

    @BeforeClass
    public void setUp() throws IOException {
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
        new File(f25.getAbsolutePath() + "/f25_3_virtualbox.box").createNewFile();
    }

    @AfterClass
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(testHomeDir);
    }

    @DataProvider
    public Object[][] filesAndHashes() {
        return new Object[][]{
                {
                        "MD5",
                        new File(testHomeDir.getAbsolutePath() + "/f25/f25_1_virtualbox.box"),
                        "d41d8cd98f00b204e9800998ecf8427e"
                },
                {
                        "SHA-1",
                        new File(testHomeDir.getAbsolutePath() + "/f25/f25_2_virtualbox.box"),
                        "da39a3ee5e6b4b0d3255bfef95601890afd80709"
                },
                {
                        "SHA-256",
                        new File(testHomeDir.getAbsolutePath() + "/f25/f25_3_virtualbox.box"),
                        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
                }
        };
    }

    @Test(dataProvider = "filesAndHashes")
    public void givenHashService_whenGetChecksum_thenChecksumsAreEquals(String algorithm, File file, String expectedChecksum) throws NoSuchAlgorithmException {
        HashService hashService = new DigestHashService(MessageDigest.getInstance(algorithm));

        String checksum = hashService.getChecksum(file.getAbsolutePath());

        assertEquals(checksum, expectedChecksum);
    }

}
