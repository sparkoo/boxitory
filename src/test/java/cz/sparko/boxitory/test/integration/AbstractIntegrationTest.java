package cz.sparko.boxitory.test.integration;

import cz.sparko.boxitory.App;
import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.controller.BoxController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = App.class)
@WebMvcTest(controllers = BoxController.class)
@AutoConfigureMockMvc(secure = false)
@TestPropertySource(locations = "classpath:test.properties")
public abstract class AbstractIntegrationTest extends AbstractTestNGSpringContextTests {
    public static final String UTF8_CHARSET = ";charset=UTF-8";

    @Autowired
    MockMvc mockMvc;

    @Configuration
    static class TestConfig {
        @Bean
        public AppProperties appProperties() {
            return new AppProperties();
        }
    }
}
