package cz.sparko.boxitory.test.e2e.versionastimestamp;

import cz.sparko.boxitory.test.e2e.AbstractIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"box.version_as_timestamp=true"})
public class VersionAsTimestampTest extends AbstractIntegrationTest {

    private final String TEST_DATE = "2017-10-10 12:00:00";
    private final Timestamp TEST_TIMESTAMP = Timestamp.valueOf(TEST_DATE);

    private final String VM = "vm";
    private final String VM_1_BOX = VM + "_"  + TEST_TIMESTAMP.getTime() + "_virtualbox.box";

    private final String EXPECTED_BOX_DESCRIPTION = "2017-10-10T10:00:00Z";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vmDir = createDirInRepository(VM);
        createFile(vmDir.getPath() + File.separator + VM_1_BOX);
    }

    @Test
    public void givenValidRepo_whenBoxWithTimestampAsVersion_thenDescriptionContainsDateInISO8601() throws Exception {
        mockMvc.perform(get("/" + VM ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.versions[0].description", is(EXPECTED_BOX_DESCRIPTION)));
    }
}
