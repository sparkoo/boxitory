package cz.sparko.boxitory.test.integration;

import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"box.sort_desc=true"})
public class VersionSortDescTest extends VersionSortTest {

    @Test
    public void givenSortAsc_whenGetBox_thenVersionsSortedAsc() throws Exception {
        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.versions[0].version", is("12")))
                .andExpect(jsonPath("$.versions[1].version", is("5")))
                .andExpect(jsonPath("$.versions[2].version", is("3")))
                .andExpect(jsonPath("$.versions[3].version", is("2")))
                .andExpect(jsonPath("$.versions[4].version", is("1")));
    }
}
