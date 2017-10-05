package cz.sparko.boxitory.conf;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException boxNotFound(String boxName) {
        return new NotFoundException("box [" + boxName + "] does not exist");
    }
}
