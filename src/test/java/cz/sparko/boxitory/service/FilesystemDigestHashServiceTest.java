package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.testng.Assert.assertEquals;

@SpringBootTest
public class FilesystemDigestHashServiceTest {

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

        File f25box1 = new File(f25.getAbsolutePath() + "/f25_1_virtualbox.box");
        f25box1.createNewFile();
        FileWriter fileWriter = new FileWriter(f25box1);
        fileWriter.write("123456789\n987654321\nabcdefghi");
        fileWriter.close();
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
                        "86462c346f1358ddbf4f137fb5da43cf"
                },
                {
                        "SHA-1",
                        new File(testHomeDir.getAbsolutePath() + "/f25/f25_1_virtualbox.box"),
                        "6efeafd3d3304cf5d7fd37db2a7ddbaac09f425d"
                },
                {
                        "SHA-256",
                        new File(testHomeDir.getAbsolutePath() + "/f25/f25_1_virtualbox.box"),
                        "ae4fe7f29f683d3901d4c620ef2e3c7ed17ebb6813158efd6a16f81b71a0aa43"
                }
        };
    }

    @Test(dataProvider = "filesAndHashes")
    public void givenHashService_whenGetChecksum_thenChecksumsAreEquals(String algorithm, File file, String
            expectedChecksum) throws NoSuchAlgorithmException {
        HashService hashService = new FilesystemDigestHashService(MessageDigest.getInstance(algorithm), new NoopHashStore(), new AppProperties());

        String checksum = hashService.getChecksum(file.getAbsolutePath());

        assertEquals(checksum, expectedChecksum);
    }

}
