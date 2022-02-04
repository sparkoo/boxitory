package cz.sparko.boxitory.test.e2e.versionsort;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

@TestPropertySource(properties = {"box.sort_desc=false"})
public class VersionSortAscTest extends VersionSortTest {

    @Test
    public void givenSortAsc_whenGetBox_thenVersionsSortedAsc() throws Exception {
        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.versions[0].version", is("1")))
                .andExpect(jsonPath("$.versions[1].version", is("2")))
                .andExpect(jsonPath("$.versions[2].version", is("3")))
                .andExpect(jsonPath("$.versions[3].version", is("5")))
                .andExpect(jsonPath("$.versions[4].version", is("12")));
    }
}
