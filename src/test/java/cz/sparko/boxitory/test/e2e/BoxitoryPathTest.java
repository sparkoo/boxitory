package cz.sparko.boxitory.test.e2e;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

@TestPropertySource(properties = {"box.path_type=BOXITORY"})
public class BoxitoryPathTest extends AbstractIntegrationTest {
    private final String VM = "vm";
    private final String VM_1_VBOX = VM + "_1_virtualbox.box";
    private final String VM_1_VBOX_DLURL = "download/" + VM + "/virtualbox/1";

    private final String VM_2_LVIRT = VM + "_2_libvirt.box";
    private final String VM_2_LVIRT_DLURL = "download/" + VM + "/libvirt/2";


    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vmDir = createDirInRepository(VM);
        createFile(vmDir.getPath() + File.separator + VM_1_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_2_LVIRT);
    }

    @Test
    public void givenBoxitoryPathConfig_whenGetBox_thenReturnProperUrls() throws Exception {
        mockMvc.perform(get("/vm"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(VM)))
                .andExpect(jsonPath("$.description", is(VM)))
                .andExpect(jsonPath("$.versions", hasSize(2)))
                .andExpect(jsonPath("$.versions[0].version", is("2")))
                .andExpect(jsonPath("$.versions[0].providers", hasSize(1)))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(VM_2_LVIRT_DLURL)))
                .andExpect(jsonPath("$.versions[1].version", is("1")))
                .andExpect(jsonPath("$.versions[1].providers", hasSize(1)))
                .andExpect(jsonPath("$.versions[1].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(VM_1_VBOX_DLURL)));
    }
}
