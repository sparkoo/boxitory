package cz.sparko.boxitory.factory;

import cz.sparko.boxitory.service.BlankHashService;
import cz.sparko.boxitory.service.HashService;

public class HashServiceFactory {
    public HashService createHashService(String type) {
        switch (type) {
            case "disabled":
                return new BlankHashService();
            default:
                throw new IllegalArgumentException(
                        "Configured checksum type (box.checksum=" + type + ") is not supported"
                );
        }
    }
}
