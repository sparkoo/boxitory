package cz.sparko.boxitory.test.e2e;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestPropertySource(properties = {"box.checksum=md5", "box.checksum_persist=false", "box.checksum_ensure=2"})
public class EnsureChecksumParallelTest extends AbstractIntegrationTest {
    private static final String VM1 = "vm1";
    private static final String VM2 = "vm2";
    private static final String VM1_1_VBOX = VM1 + "_1_virtualbox.box";
    private static final String VM1_2_VBOX = VM1 + "_2_virtualbox.box";
    private static final String VM1_3_VBOX = VM1 + "_3_virtualbox.box";
    private static final String VM2_1_VBOX = VM2 + "_1_virtualbox.box";
    private static final String VM2_2_VBOX = VM2 + "_2_virtualbox.box";
    private static final String VM2_3_VBOX = VM2 + "_3_virtualbox.box";
    private static final String EXPECTED_BOX_CHECKSUM = "6dd69c522eac6e1689f4896d96db7c95";

    @BeforeClass
    @Override
    public void setUp() throws IOException {
        super.setUp();
    }

    @AfterClass
    @Override
    public void tearDown() throws IOException {
        // sometimes throwing IOException. Probably some process don't release some file fast enough. Wait some time.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.tearDown();
    }

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vm1Dir = createDirInRepository(VM1);
        File vm1box = createFile(vm1Dir.getPath() + File.separator + VM1_1_VBOX);
        File vm2box = createFile(vm1Dir.getPath() + File.separator + VM1_2_VBOX);
        File vm3box = createFile(vm1Dir.getPath() + File.separator + VM1_3_VBOX);

        File vm2Dir = createDirInRepository(VM2);
        File vm2_1box = createFile(vm2Dir.getPath() + File.separator + VM2_1_VBOX);
        File vm2_2box = createFile(vm2Dir.getPath() + File.separator + VM2_2_VBOX);
        File vm2_3box = createFile(vm2Dir.getPath() + File.separator + VM2_3_VBOX);

        try (FileWriter vm3boxWriter = new FileWriter(vm3box);
             FileWriter vm2boxWriter = new FileWriter(vm2box);
             FileWriter vm2_3boxWriter = new FileWriter(vm2_3box);
             FileWriter vm2_2boxWriter = new FileWriter(vm2_2box)) {
            for (int i = 0; i < 1_000_000; i++) {
                vm3boxWriter.append(String.valueOf(i));
                vm2boxWriter.append(String.valueOf(i));
                vm2_3boxWriter.append(String.valueOf(i));
                vm2_2boxWriter.append(String.valueOf(i));
            }
        }
    }

    @Test
    public void givenTwoBoxes_whenRequestWithEnsureChecksumInParallel_thenChecksumIsProcessedAsEnsured() {
        List<ConcurrentTester> requests = new ArrayList<>();
        requests.add(new ConcurrentTester(new VmRequest(mockMvc, singletonList(VM1))));
        requests.add(new ConcurrentTester(new VmRequest(mockMvc, singletonList(VM2))));

        requests.forEach(ConcurrentTester::start);

        requests.forEach(ConcurrentTester::test);
    }

    @Test
    public void givenOneBox_whenRequestWithEnsureChecksumInParallel_thenChecksumIsProcessedAsEnsured() {
        List<ConcurrentTester> requests = new ArrayList<>();
        IntStream.range(0, 5)
                .forEach(i -> requests.add(new ConcurrentTester(new VmRequest(mockMvc, singletonList(VM1)))));

        requests.forEach(ConcurrentTester::start);

        requests.forEach(ConcurrentTester::test);
    }

    private static final class VmRequest implements Runnable {
        private final MockMvc mockMvc;
        private final List<String> vmsRequests;

        VmRequest(MockMvc mockMvc, List<String> vmsRequests) {
            this.mockMvc = mockMvc;
            this.vmsRequests = vmsRequests;
        }

        @Override
        public void run() {
            try {
                for (String vm : vmsRequests) {
                    mockMvc.perform(get("/" + vm))
                            .andDo(print())
                            .andExpect(jsonPath("$.versions[0].providers[0].checksum", is(EXPECTED_BOX_CHECKSUM)))
                            .andExpect(jsonPath("$.versions[1].providers[0].checksum", is(EXPECTED_BOX_CHECKSUM)));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
