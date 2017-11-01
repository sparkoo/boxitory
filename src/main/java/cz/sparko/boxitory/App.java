package cz.sparko.boxitory;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.factory.HashServiceFactory;
import cz.sparko.boxitory.service.BoxRepository;
import cz.sparko.boxitory.service.DescriptionProvider;
import cz.sparko.boxitory.service.filesystem.FilesystemBoxRepository;
import cz.sparko.boxitory.service.filesystem.FilesystemDescriptionProvider;
import cz.sparko.boxitory.service.filesystem.FilesystemHashStore;
import cz.sparko.boxitory.service.HashService;
import cz.sparko.boxitory.service.HashStore;
import cz.sparko.boxitory.service.noop.NoopHashStore;
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
    public HashService hashService(AppProperties appProperties, HashStore hashStore) {
        return HashServiceFactory.createHashService(appProperties, hashStore);
    }

    @Bean
    @Autowired
    public HashStore hashStore(AppProperties appProperties) {
        if (appProperties.isChecksum_persist()) {
            return new FilesystemHashStore();
        } else {
            return new NoopHashStore();
        }
    }
}
