package cz.sparko.boxitory.test.e2e.hashtype;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cz.sparko.boxitory.test.e2e.AbstractIntegrationTest;
import java.io.File;
import java.io.IOException;
import org.testng.annotations.Test;

abstract public class HashTypeTest extends AbstractIntegrationTest {
    private final String VM = "vm";
    private final String VM_1_VBOX = VM + "_1_virtualbox.box";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vmDir = createDirInRepository(VM);
        createFile(vmDir.getPath() + File.separator + VM_1_VBOX);
    }

    abstract String expectedAlg();

    @Test
    public void givenHashAlg_whenGetBox_thenHashMethodIsCorrect() throws Exception {
        mockMvc.perform(get("/" + VM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.versions[0].providers[0].checksum_type", is(expectedAlg())));
    }
}
