package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.conf.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.service.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
