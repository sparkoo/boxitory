package cz.sparko.boxitory.conf;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class NotFoundException extends HttpClientErrorException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static NotFoundException boxNotFound(String boxName) {
        return new NotFoundException("box [" + boxName + "] does not exist");
    }
}
