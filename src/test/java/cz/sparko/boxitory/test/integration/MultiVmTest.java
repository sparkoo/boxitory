package cz.sparko.boxitory.test.integration;

import cz.sparko.boxitory.conf.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.containsString;
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
        "box.home=target/test-classes/test_repository/multivm",
        "box.sort_desc=true",
        "box.host_prefix=test_prefix"
})
public class MultiVmTest extends AbstractIntegrationTest {

    @Autowired
    AppProperties appProperties;

    @Test
    public void givenMultiProviders_whenIndex_thenReturnListWithVm() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML + UTF8_CHARSET))
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("vm")));
    }

    @Test
    public void givenMultiProviders_whenVm1Box_thenReturnListOfVmVersions() throws Exception {
        final String VM = "vm1";
        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", is(VM)))
                .andExpect(jsonPath("$.description", is(VM)))
                .andExpect(jsonPath("$.versions[0].version", is("2")))
                .andExpect(jsonPath("$.versions[0].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(VM + "_2_virtualbox.box")))
                .andExpect(jsonPath("$.versions[1].version", is("1")))
                .andExpect(jsonPath("$.versions[1].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[1].providers[0].url", containsString(VM + "_1_virtualbox.box")));
    }

    @Test
    public void givenMultiProviders_whenVm2Box_thenReturnListOfVmVersions() throws Exception {
        final String VM = "vm2";
        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", is(VM)))
                .andExpect(jsonPath("$.description", is(VM)))
                .andExpect(jsonPath("$.versions[0].version", is("17")))
                .andExpect(jsonPath("$.versions[0].providers[0].name", is("virtualbox")))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(appProperties.getHost_prefix())))
                .andExpect(jsonPath("$.versions[0].providers[0].url", containsString(VM + "_17_virtualbox.box")));
    }

    @Test
    public void givenMultiProviders_whenLatestVersionVm1_thenReturnLatestVersionNumber() throws Exception {
        mockMvc.perform(get("/vm1/latestVersion"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN + UTF8_CHARSET))
                .andExpect(content().string("2"));
    }

    @Test
    public void givenMultiProviders_whenLatestVersionVm2_thenReturnLatestVersionNumber() throws Exception {
        mockMvc.perform(get("/vm2/latestVersion"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN + UTF8_CHARSET))
                .andExpect(content().string("17"));
    }
}
