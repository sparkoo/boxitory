package cz.sparko.boxitory.test.integration;

import org.hamcrest.Matchers;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@TestPropertySource(properties = {
        "box.checksum=md5",
        "box.checksum_persist=true"
})
public class PersistChecksumTest extends AbstractIntegrationTest {
    private static final String VM = "vm";
    private static final String BOX = "vm_1_vb.box";
    private static final String CHECKSUM_FILE = "vm_1_vb.box.md5";

    private File vmDir;

    @Override
    void createFolderStructure() throws IOException {
        super.createFolderStructure();
        vmDir = createDirInRepository(VM);
        File box = createFile(vmDir.getPath() + File.separator + BOX);
        try (FileWriter fw = new FileWriter(box)) {
            fw.write("blabol");
        }
    }

    @Test
    public void givenMd5WithPersist_whenGetBox_thenFileWithChecksumIsStored() throws Exception {
        File checksumFile = new File(vmDir + File.separator + CHECKSUM_FILE);
        assertFalse(checksumFile.exists());

        mockMvc.perform(get("/" + VM));

        assertTrue(checksumFile.exists());

        List<String> checksumFileContent = Files.readAllLines(checksumFile.toPath());
        System.out.println(checksumFileContent);
        assertEquals(checksumFileContent.size(), 1);
        assertTrue(checksumFileContent.get(0).endsWith(BOX));
        assertTrue(checksumFileContent.get(0).startsWith("f34835fa588de624b2782bd5307c344c"));
    }

    @Test
    public void givenMd5WithPersist_whenChecksumFileStored_thenChecksumReadFromFile() throws Exception {
        final String FAKE_CHECKSUM = "fake_checksum";
        File checksumFile = new File(vmDir + File.separator + CHECKSUM_FILE);
        assertFalse(checksumFile.exists());

        try (FileWriter fw = new FileWriter(checksumFile)) {
            fw.write(FAKE_CHECKSUM + "  " + BOX);
        }

        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(jsonPath("$.versions[0].providers[0].checksum", is(FAKE_CHECKSUM)));
    }
}
