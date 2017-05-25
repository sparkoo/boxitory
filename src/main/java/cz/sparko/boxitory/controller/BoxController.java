package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.conf.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.service.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/", method = RequestMethod.GET)
public class BoxController {
    private BoxRepository boxRepository;

    @Autowired
    public BoxController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @RequestMapping("{boxName}")
    @ResponseBody
    public Box getBoxes(@PathVariable String boxName) {
        return boxRepository.getBox(boxName)
                .orElseThrow(() -> new NotFoundException("[" + boxName + "] does not exist"));
    }
}
