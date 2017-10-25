package cz.sparko.boxitory.test.integration;

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
        "box.checksum_persist=false"
})
public class DontPersistChecksumTest extends AbstractIntegrationTest {
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

        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(jsonPath("$.versions[0].providers[0].checksum", is("f34835fa588de624b2782bd5307c344c")));

        assertFalse(checksumFile.exists());
    }
}
