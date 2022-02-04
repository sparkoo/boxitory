package cz.sparko.boxitory.test.e2e;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.IOException;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

@TestPropertySource(properties = {"box.sort_desc=true"})
public class MultiProviderTest extends AbstractIntegrationTest {

    private final String VM = "vm";
    private final String VM_1_VBOX = VM + "_1_virtualbox.box";
    private final String VM_2_VBOX = VM + "_2_virtualbox.box";
    private final String VM_2_LVIRT = VM + "_2_libvirt.box";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vmDir = createDirInRepository(VM);
        createFile(vmDir.getPath() + File.separator + VM_1_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_2_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_2_LVIRT);
    }

    @Test
    public void givenMultiProviders_whenIndex_thenReturnListWithVm() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML + UTF8_CHARSET))
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString(VM)));
    }

    @Test
    public void givenMultiProviders_whenBox_thenReturnListOfVmVersions() throws Exception {
        mockMvc.perform(get("/vm"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(VM)))
                .andExpect(jsonPath("$.description", is(VM)))
                .andExpect(jsonPath("$.versions", hasSize(2)))
                .andExpect(jsonPath("$.versions[0].version", is("2")))
                .andExpect(jsonPath("$.versions[0].providers", hasSize(2)))
                .andExpect(jsonPath("$.versions[0].providers[?(@.name == \'libvirt\' && " +
                                            "@.url =~ /.*" + appProperties.getHost_prefix() + ".*/i && " +
                                            "@.url =~ /.*" + VM_2_LVIRT + ".*/i)]").exists())
                .andExpect(jsonPath("$.versions[0].providers[?(@.name == \'virtualbox\' && " +
                                            "@.url =~ /.*" + appProperties.getHost_prefix() + ".*/i && " +
                                            "@.url =~ /.*" + VM_2_VBOX + ".*/i)]").exists())
                .andExpect(jsonPath("$.versions[1].version", is("1")))
                .andExpect(jsonPath("$.versions[1].providers", hasSize(1)))
                .andExpect(jsonPath("$.versions[1].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(VM_1_VBOX)));
    }

    @Test
    public void givenMultiProviders_whenLatestVersion_thenReturnLatestVersionNumber() throws Exception {
        mockMvc.perform(get("/vm/latestVersion"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN + UTF8_CHARSET))
                .andExpect(content().string("2"));
    }
}
