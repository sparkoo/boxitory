package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.conf.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.service.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class BoxController {
    private BoxRepository boxRepository;

    @Autowired
    public BoxController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @RequestMapping(value = "/{boxName}", method = RequestMethod.GET)
    @ResponseBody
    public Box box(@PathVariable String boxName) {
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("boxes", boxRepository.getBoxes());
        return "index";
    }

    @RequestMapping(value = "/{boxName}/latestVersion", method = RequestMethod.GET)
    @ResponseBody
    public String latestBoxVersion(@PathVariable String boxName) {
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName))
                .getVersions().stream()
                .sorted(BoxVersion.VERSION_COMPARATOR.reversed())
                .findFirst()
                .map(BoxVersion::getVersion)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        final HttpStatus status;
        if (e instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(e.getMessage(), status);
    }
}
