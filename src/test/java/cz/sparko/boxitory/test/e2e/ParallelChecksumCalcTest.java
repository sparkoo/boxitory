package cz.sparko.boxitory.test.e2e;

import org.springframework.test.context.TestPropertySource;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestPropertySource(properties = {"box.checksum=md5", "box.checksum_persist=true"})
public class ParallelChecksumCalcTest extends AbstractIntegrationTest {
    private final String VM1 = "vm1";
    private final String VM2 = "vm2";
    private final String VM1_1_VBOX = VM1 + "_1_virtualbox.box";
    private final String VM2_1_VBOX = VM2 + "_1_virtualbox.box";

    private final String EXPECTED_VM1BOX_CHECKSUM = "6dd69c522eac6e1689f4896d96db7c95";
    private final String EXPECTED_VM2BOX_CHECKSUM = "6b21c4a111ac178feacf9ec9d0c71f17";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vm1Dir = createDirInRepository(VM1);
        File vm2Dir = createDirInRepository(VM2);
        File vm1box = createFile(vm1Dir.getPath() + File.separator + VM1_1_VBOX);
        File vm2box = createFile(vm2Dir.getPath() + File.separator + VM2_1_VBOX);

        try (FileWriter vm1boxWriter = new FileWriter(vm1box);
             FileWriter vm2boxWriter = new FileWriter(vm2box)) {
            for (int i = 0; i < 1_000_000; i++) {
                vm1boxWriter.append(String.valueOf(i));
            }
            for (int i = 0; i < 1_000; i++) {
                vm2boxWriter.append(String.valueOf(i));
            }
        }
    }

    @Test
    public void givenTwoBoxes_whenCalculateChecksumParallel_thenChecksumIsCorrectlyCalculated() throws
            Exception {
        Thread requestVm1 = new Thread(() -> {
            try {
                mockMvc.perform(get("/" + VM1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread requestVm2 = new Thread(() -> {
            try {
                mockMvc.perform(get("/" + VM2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        requestVm1.start();
        requestVm2.start();

        requestVm1.join();
        requestVm2.join();

        mockMvc.perform(get("/" + VM1))
                .andDo(print())
                .andExpect(jsonPath("$.versions[0].providers[0].checksum", is(EXPECTED_VM1BOX_CHECKSUM)));
        mockMvc.perform(get("/" + VM2))
                .andDo(print())
                .andExpect(jsonPath("$.versions[0].providers[0].checksum", is(EXPECTED_VM2BOX_CHECKSUM)));
    }
}
