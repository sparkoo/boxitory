package cz.sparko.boxitory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class NotFoundException extends HttpClientErrorException {
    private NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static NotFoundException boxNotFound(String boxName) {
        return new NotFoundException("box [" + boxName + "] does not exist");
    }

    public static NotFoundException boxVersionNotFound(String boxName, String boxVersion) {
        return new NotFoundException("version [" + boxVersion + "] of box [" + boxName + "] does not exist");
    }

    public static NotFoundException boxVersionProviderNotFound(String boxName, String boxVersion, String boxProvider) {
        return new NotFoundException(
                "provider [" + boxProvider + "] of version [" + boxVersion + "] of box [" + boxName + "] does not exist");
    }
}
