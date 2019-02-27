package cz.sparko.boxitory.controller;

import cz.sparko.boxitory.exception.NotFoundException;
import cz.sparko.boxitory.model.BoxStream;
import cz.sparko.boxitory.service.BoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DownloadController {
    private static final Logger LOG = LoggerFactory.getLogger(DownloadController.class);

    private final BoxRepository boxRepository;

    public DownloadController(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    @RequestMapping(value = "/download/{boxName}/{boxProvider}/{boxVersion}", method = RequestMethod.GET)
    public void downloadBox(
            HttpServletResponse response,
            @PathVariable String boxName,
            @PathVariable String boxProvider,
            @PathVariable String boxVersion) throws IOException {
        LOG.info("Downloading box [{}], provider [{}], version [{}]", boxName, boxProvider, boxVersion);

        BoxStream boxFile = boxRepository.getBoxStream(boxName, boxProvider, boxVersion)
                .orElseThrow(() -> NotFoundException.boxVersionProviderNotFound(boxName, boxVersion, boxProvider));

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + boxFile.getFilename() + "\"");
        FileCopyUtils.copy(boxFile.getStream(), response.getOutputStream());
    }

    @RequestMapping(value = "/download/{boxName}/{boxProvider}/latest", method = RequestMethod.GET)
    public void downloadLatestBox(
            HttpServletResponse response,
            @PathVariable String boxName,
            @PathVariable String boxProvider) throws IOException {
        LOG.info("Downloading latest version of box [{}], provider [{}]", boxName, boxProvider);
        downloadBox(response, boxName, boxProvider, boxRepository.latestVersionOfBox(boxName, boxProvider));
    }
}
