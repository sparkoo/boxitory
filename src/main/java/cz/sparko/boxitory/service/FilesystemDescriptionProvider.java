package cz.sparko.boxitory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class FilesystemDescriptionProvider implements DescriptionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemDescriptionProvider.class);

    public static final String DESCRIPTIONS_FILE = "descriptions.csv";
    private static final String SEPARATOR = ";;;";

    private final File boxHome;

    public FilesystemDescriptionProvider(File boxHome) {
        this.boxHome = boxHome;
    }

    @Override
    public String getDescription(String boxName, String version) {
        File descriptionFile = new File(boxHome, File.separator + boxName + File.separator + DESCRIPTIONS_FILE);
        try {
            Optional<DescriptionLine> foundDescription = Files.readAllLines(descriptionFile.toPath()).stream()
                    .map(this::parseLine)
                    .filter(parsedLine -> parsedLine.version.equals(version))
                    .findFirst();

            if (foundDescription.isPresent()) {
                String description = foundDescription.get().description;
                LOG.debug("Description [{}] found for box [{}] version [{}]", description, boxName, version);
                return description;
            }
        } catch (IOException e) {
            LOG.error("Error when parsing description file. Please check whether [{}] is in valid format.",
                    DESCRIPTIONS_FILE, e);
        }
        LOG.debug("No description found for box [{}] version [{}]", boxName, version);
        return null;
    }

    private DescriptionLine parseLine(String line) {
        String[] splittedLine = line.split(SEPARATOR);
        return new DescriptionLine(splittedLine[0], splittedLine[1]);
    }

    private static class DescriptionLine {
        private final String version;
        private final String description;

        DescriptionLine(String version, String description) {
            this.version = version;
            this.description = description;
        }
    }
}
