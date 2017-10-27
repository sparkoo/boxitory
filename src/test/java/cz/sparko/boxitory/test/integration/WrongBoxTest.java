package cz.sparko.boxitory.test.integration;

import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WrongBoxTest extends AbstractIntegrationTest{
    @Test
    public void givenValidRepository_whenRequestWrongBox_then404() throws Exception {
        mockMvc.perform(get("/box"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
