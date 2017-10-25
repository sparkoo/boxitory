package cz.sparko.boxitory.test.integration;

import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"box.home=this/is/not/valid/repository/dir"})
public class WrongRepositoryDirTest extends AbstractIntegrationTest {

    @Override
    void createFolderStructure() { }

    @Test
    public void givenWrongRepoDir_whenRequestRoot_then500() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void givenWrongRepoDir_whenRequestBox_then500() throws Exception {
        mockMvc.perform(get("/box"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void givenWrongRepoDir_whenRequestBoxLatestVersion_then500() throws Exception {
        mockMvc.perform(get("/box"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }
}
