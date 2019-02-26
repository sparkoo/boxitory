package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.conf.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.service.BoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class BoxRestController {
    private static final Logger LOG = LoggerFactory.getLogger(BoxRestController.class);

    private final BoxRepository boxRepository;

    public BoxRestController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @RequestMapping(value = "/{boxName}", method = RequestMethod.GET)
    public Box box(@PathVariable String boxName) {
        LOG.info("providing box [{}] ...", boxName);
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName));
    }

    @RequestMapping(value = "/{boxName}/latestVersion", method = RequestMethod.GET)
    public String latestBoxVersion(@PathVariable String boxName) {
        LOG.info("providing latest version of [{}] ...", boxName);
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName))
                .getVersions().stream().max(BoxVersion.VERSION_COMPARATOR)
                .map(BoxVersion::getVersion)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName));
    }
}
