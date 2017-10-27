package cz.sparko.boxitory.test.integration;

import cz.sparko.boxitory.App;
import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.controller.BoxController;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;

@ContextConfiguration(classes = App.class)
@WebMvcTest(controllers = BoxController.class)
@AutoConfigureMockMvc(secure = false)
@TestPropertySource(locations = "classpath:test.properties")
public abstract class AbstractIntegrationTest extends AbstractTestNGSpringContextTests {
    public static final String UTF8_CHARSET = ";charset=UTF-8";

    @Autowired
    public AppProperties appProperties;

    @Autowired
    public MockMvc mockMvc;

    @BeforeMethod
    public void setUp() throws IOException {
        createFolderStructure();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        destroyFolderStructure();
    }

    public void createRepositoryDir() {
        new File(appProperties.getHome()).mkdir();
    }

    public void createFolderStructure() throws IOException {
        createRepositoryDir();
    }

    public void destroyFolderStructure() throws IOException {
        FileUtils.deleteDirectory(new File(appProperties.getHome()));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public AppProperties appProperties() {
            return new AppProperties();
        }
    }

    public File createDirInRepository(String vmName) {
        File vmDir = new File(appProperties.getHome() + File.separator + vmName);
        vmDir.mkdir();
        return vmDir;
    }

    public File createFile(String filePath) throws IOException {
        File testFile = new File(filePath);
        testFile.createNewFile();
        return testFile;
    }
}
