package cz.sparko.boxitory.test.e2e.ensurechecksum;

import cz.sparko.boxitory.test.e2e.AbstractIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"box.checksum=md5", "box.checksum_persist=false", "box.checksum_ensure=2"})
public class ChecksumEnsureTest extends AbstractIntegrationTest {
    private static final String VM1 = "vm1";
    private static final String VM1_1_VBOX = VM1 + "_1_virtualbox.box";
    private static final String VM1_2_VBOX = VM1 + "_2_virtualbox.box";
    private static final String VM1_3_VBOX = VM1 + "_3_virtualbox.box";

    private static final String EXPECTED_BOX_CHECKSUM = "6dd69c522eac6e1689f4896d96db7c95";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vm1Dir = createDirInRepository(VM1);
        File vm1box = createFile(vm1Dir.getPath() + File.separator + VM1_1_VBOX);
        File vm2box = createFile(vm1Dir.getPath() + File.separator + VM1_2_VBOX);
        File vm3box = createFile(vm1Dir.getPath() + File.separator + VM1_3_VBOX);


        try (FileWriter vm3boxWriter = new FileWriter(vm3box);
             FileWriter vm2boxWriter = new FileWriter(vm2box);
             FileWriter vm1boxWriter = new FileWriter(vm1box)) {
            for (int i = 0; i < 1_000_000; i++) {
                vm3boxWriter.append(String.valueOf(i));
                vm2boxWriter.append(String.valueOf(i));
                vm1boxWriter.append(String.valueOf(i));
            }
        }
    }

    @Test
    public void givenOneBox_whenRequestWithEnsureChecksumSetToTwo_thenChecksumIsProcessedForMaxTwoProviders() throws Exception {
        mockMvc.perform(get("/" + VM1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.versions[0].providers[0].checksum", is(EXPECTED_BOX_CHECKSUM)))
                .andExpect(jsonPath("$.versions[1].providers[0].checksum", is(EXPECTED_BOX_CHECKSUM)))
                .andExpect(jsonPath("$.versions[2].providers[0].checksum").doesNotExist());
    }
}
