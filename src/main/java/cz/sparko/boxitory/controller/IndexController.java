package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.service.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
    private BoxRepository boxRepository;

    @Autowired
    public IndexController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getBoxes() {
        return "index";
    }
}
