package cz.sparko.boxitory.test.e2e.versionastimestamp;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cz.sparko.boxitory.service.filesystem.FilesystemDescriptionProvider;
import cz.sparko.boxitory.test.e2e.AbstractIntegrationTest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

@TestPropertySource(properties = {"box.version_as_timestamp=true"})
public class VersionAsTimestampTest extends AbstractIntegrationTest {

    private final String VM1_TEST_DATE = "2017-10-10T12:00:00.00Z";
    private final Instant VM1_TEST_TIMESTAMP = Instant.parse(VM1_TEST_DATE);
    private final String VM2_TEST_DATE = "2017-10-10T10:00:00.00Z";
    private final Instant VM2_TEST_TIMESTAMP = Instant.parse(VM2_TEST_DATE);

    private final String VM1 = "vm1";
    private final String VM2 = "vm2";
    private final String VM_1_BOX = VM1 + "_"  + VM1_TEST_TIMESTAMP.getEpochSecond() + "_virtualbox.box";
    private final String VM_2_BOX = VM2 + "_"  + VM2_TEST_TIMESTAMP.getEpochSecond() + "_vmware.box";

    private final String EXPECTED_VM1BOX_DESCRIPTION = "2017-10-10T12:00:00Z";
    private final String EXPECTED_VM2BOX_DESCRIPTION = "2017-10-10T10:00:00Z - Some useful description";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vm1Dir = createDirInRepository(VM1);
        File vm2Dir = createDirInRepository(VM2);
        createFile(vm1Dir.getPath() + File.separator + VM_1_BOX);
        createFile(vm2Dir.getPath() + File.separator + VM_2_BOX);

        File descriptionFile = createFile(
                vm2Dir.getPath() + File.separator + FilesystemDescriptionProvider.DESCRIPTIONS_FILE
        );

        try (FileWriter descriptionFileWriter = new FileWriter(descriptionFile);) {
            descriptionFileWriter.write(VM2_TEST_TIMESTAMP.getEpochSecond() + ";;;" + "Some useful description");
        }
    }

    @Test
    public void givenValidRepo_whenBoxWithTimestampAsVersion_thenDescriptionContainsDateInISO8601() throws Exception {
        mockMvc.perform(get("/" + VM1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.versions[0].description", is(EXPECTED_VM1BOX_DESCRIPTION)));
    }

    @Test
    public void givenValidRepo_whenBoxWithTimestampAsVersionHasRecordInDescriptionFile_thenDescriptionContainsDateInISO8601AndDescriptionFromFile() throws Exception {
        mockMvc.perform(get("/" + VM2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.versions[0].description", is(EXPECTED_VM2BOX_DESCRIPTION)));
    }
}
