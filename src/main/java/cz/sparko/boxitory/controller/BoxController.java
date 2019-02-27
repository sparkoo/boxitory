package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.service.BoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BoxController {
    private static final Logger LOG = LoggerFactory.getLogger(BoxController.class);

    private final BoxRepository boxRepository;

    public BoxController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        LOG.info("providing box list page ...");
        model.addAttribute("boxes", boxRepository.getBoxes());
        return "index";
    }
}
