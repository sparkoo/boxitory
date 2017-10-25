package cz.sparko.boxitory.test.integration;

import cz.sparko.boxitory.conf.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@TestPropertySource(properties = {
        "box.sort_desc=true",
        "box.host_prefix=test_prefix"
})
public class MultiVmTest extends AbstractIntegrationTest {

    private final String VM1 = "vm1";
    private final String VM2 = "vm2";
    private final String VM1_1_VBOX = VM1 + "_1_virtualbox.box";
    private final String VM1_2_VBOX = VM1 + "_2_virtualbox.box";
    private final String VM2_17_VBOX = VM2 + "_17_virtualbox.box";

    @Override
    void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vm1Dir = createDirInRepository(VM1);
        File vm2Dir = createDirInRepository(VM2);
        createFile(vm1Dir.getPath() + File.separator + VM1_1_VBOX);
        createFile(vm1Dir.getPath() + File.separator + VM1_2_VBOX);
        createFile(vm2Dir.getPath() + File.separator + VM2_17_VBOX);
    }

    @Autowired
    AppProperties appProperties;

    @Test
    public void givenMultiProviders_whenIndex_thenReturnListWithVm() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML + UTF8_CHARSET))
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString(VM1)))
                .andExpect(content().string(containsString(VM2)));
    }

    @Test
    public void givenMultiProviders_whenVm1Box_thenReturnListOfVmVersions() throws Exception {
        mockMvc.perform(get("/" + VM1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", is(VM1)))
                .andExpect(jsonPath("$.description", is(VM1)))
                .andExpect(jsonPath("$.versions", hasSize(2)))
                .andExpect(jsonPath("$.versions[0].version", is("2")))
                .andExpect(jsonPath("$.versions[0].providers", hasSize(1)))
                .andExpect(jsonPath("$.versions[0].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(VM1_2_VBOX)))
                .andExpect(jsonPath("$.versions[1].version", is("1")))
                .andExpect(jsonPath("$.versions[1].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(VM1_1_VBOX)));
    }

    @Test
    public void givenMultiProviders_whenVm2Box_thenReturnListOfVmVersions() throws Exception {
        mockMvc.perform(get("/" + VM2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", is(VM2)))
                .andExpect(jsonPath("$.description", is(VM2)))
                .andExpect(jsonPath("$.versions", hasSize(1)))
                .andExpect(jsonPath("$.versions[0].version", is("17")))
                .andExpect(jsonPath("$.versions[0].providers", hasSize(1)))
                .andExpect(jsonPath("$.versions[0].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(VM2_17_VBOX)));
    }

    @Test
    public void givenMultiProviders_whenLatestVersionVm1_thenReturnLatestVersionNumber() throws Exception {
        mockMvc.perform(get("/" + VM1 + "/latestVersion"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN + UTF8_CHARSET))
                .andExpect(content().string("2"));
    }

    @Test
    public void givenMultiProviders_whenLatestVersionVm2_thenReturnLatestVersionNumber() throws Exception {
        mockMvc.perform(get("/" + VM2 + "/latestVersion"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN + UTF8_CHARSET))
                .andExpect(content().string("17"));
    }
}
