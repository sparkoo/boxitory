package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.conf.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.service.BoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BoxController {
    private static final Logger LOG = LoggerFactory.getLogger(BoxController.class);

    private final BoxRepository boxRepository;

    public BoxController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @RequestMapping(value = "/{boxName}", method = RequestMethod.GET)
    @ResponseBody
    public Box box(@PathVariable String boxName) {
        LOG.info("providing box [{}] ...", boxName);
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName));
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        LOG.info("providing box list page ...");
        model.addAttribute("boxes", boxRepository.getBoxes());
        return "index";
    }

    @RequestMapping(value = "/{boxName}/latestVersion", method = RequestMethod.GET)
    @ResponseBody
    public String latestBoxVersion(@PathVariable String boxName) {
        LOG.info("providing latest version of [{}] ...", boxName);
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName))
                .getVersions().stream().max(BoxVersion.VERSION_COMPARATOR)
                .map(BoxVersion::getVersion)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName));
    }
}
