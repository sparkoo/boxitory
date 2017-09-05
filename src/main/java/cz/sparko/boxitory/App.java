package cz.sparko.boxitory;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.factory.HashServiceFactory;
import cz.sparko.boxitory.service.BoxRepository;
import cz.sparko.boxitory.service.DescriptionProvider;
import cz.sparko.boxitory.service.FilesystemBoxRepository;
import cz.sparko.boxitory.service.FilesystemDescriptionProvider;
import cz.sparko.boxitory.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @Autowired
    public BoxRepository boxRepository(AppProperties appProperties,
                                       HashService hashService,
                                       DescriptionProvider descriptionProvider) {
        return new FilesystemBoxRepository(appProperties, hashService, descriptionProvider);
    }

    @Bean
    @Autowired
    public DescriptionProvider descriptionProvider(AppProperties appProperties) {
        return new FilesystemDescriptionProvider(appProperties.getHome());
    }

    @Bean
    @Autowired
    public HashService hashService(AppProperties appProperties) throws NoSuchAlgorithmException {
        return HashServiceFactory.createHashService(appProperties);
    }
}
