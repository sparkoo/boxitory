package cz.sparko.boxitory.test.integration;


import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"box.sort_desc=true"})
public class LatestVersionTest extends AbstractIntegrationTest {

    private final String VM = "vm";
    private final String VM_1_VBOX = VM + "_1_virtualbox.box";
    private final String VM_2_VBOX = VM + "_2_virtualbox.box";
    private final String VM_3_VBOX = VM + "_3_virtualbox.box";
    private final String VM_5_VBOX = VM + "_5_virtualbox.box";
    private final String VM_12_VBOX = VM + "_12_virtualbox.box";

    @Override
    void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vmDir = createDirInRepository(VM);
        createFile(vmDir.getPath() + File.separator + VM_5_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_3_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_1_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_12_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_2_VBOX);
    }

    @Test
    public void givenValidRepo_whenLatestVersion_thenGetsLatestVersion() throws Exception {
        mockMvc.perform(get("/" + VM + "/latestVersion"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN + UTF8_CHARSET))
                .andExpect(content().string("12"));
    }

    @Test
    public void givenValidRepo_whenLatestVersionOnNonExistingBox_then404() throws Exception {
        mockMvc.perform(get("/this_box_dont_exist/latestVersion"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
