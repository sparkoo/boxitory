package cz.sparko.boxitory.service.filesystem;

import cz.sparko.boxitory.service.DescriptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * This implementation of {@link DescriptionProvider} uses filesystem as storage. Descriptions must be stored in file
 * {@link FilesystemDescriptionProvider#DESCRIPTIONS_FILE} in CSV format with
 * {@link FilesystemDescriptionProvider#SEPARATOR} as separator, in box folder beside {@code .box} files.
 */
public class FilesystemDescriptionProvider implements DescriptionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemDescriptionProvider.class);

    public static final String DESCRIPTIONS_FILE = "descriptions.csv";
    private static final String SEPARATOR = ";;;";

    private final File boxHome;
    private final boolean versionAsTimestamp;

    public FilesystemDescriptionProvider(File boxHome, boolean versionAsTimestamp) {
        this.boxHome = boxHome;
        this.versionAsTimestamp = versionAsTimestamp;
    }

    public FilesystemDescriptionProvider(String boxHome, boolean versionAsTimestamp) {
        this(new File(boxHome), versionAsTimestamp);
    }

    /**
     * {@link FilesystemDescriptionProvider} makes best effort to get description for given box's version from file
     * {@link FilesystemDescriptionProvider#DESCRIPTIONS_FILE}. That means that when it finds just one valid line in
     * {@link FilesystemDescriptionProvider#DESCRIPTIONS_FILE} that matches given parameters, it return it. It does
     * not do any validation of the rest of the file.
     * <p>
     * When multiple descriptions for one version found, it returns the latest one. With this behavior, we can simply
     * append to the {@link FilesystemDescriptionProvider#DESCRIPTIONS_FILE} and don't care about past descriptions.
     */
    @Override
    public Optional<String> getDescription(String boxName, String version) {
        validateArgs(boxName, version);

        File descriptionFile = new File(boxHome, File.separator + boxName + File.separator + DESCRIPTIONS_FILE);
        Optional<String> descriptionNotFoundResult = Optional.empty();

        if (versionAsTimestamp) {
            descriptionNotFoundResult = Optional.of(getDateFromTimestamp(version));
        }

        if (!descriptionFile.exists()) {
            LOG.trace("Descriptions file [{}] does not exist.", DESCRIPTIONS_FILE);
            return descriptionNotFoundResult;
        }

        try {
            Optional<DescriptionLine> foundDescription = Files.readAllLines(descriptionFile.toPath()).stream()
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .filter(parsedLine -> version.equals(parsedLine.version))
                    .reduce((a, b) -> b);   // get last object

            if (foundDescription.isPresent()) {
                String description = foundDescription.get().description;
                LOG.debug("Description [{}] found for box [{}] version [{}]", description, boxName, version);
                return Optional.of(description);
            }
        } catch (IOException e) {
            LOG.error("Error when parsing description file. Please check whether [{}] is in valid format.",
                    DESCRIPTIONS_FILE, e);
        }

        LOG.debug("No description found for box [{}] version [{}]", boxName, version);
        return descriptionNotFoundResult;
    }

    private void validateArgs(String boxName, String version) {
        if (boxName == null || boxName.isEmpty()) {
            throw new IllegalArgumentException("[boxName] must not be null nor empty");
        }
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("[boxName] must not be null nor empty");
        }
    }

    private DescriptionLine parseLine(String line) {
        String[] splittedLine = line.split(SEPARATOR);
        if (splittedLine.length != 2) {
            return null;
        }

        if (versionAsTimestamp) {
            splittedLine[1] = Stream.of(
                    getDateFromTimestamp(splittedLine[0]),
                    splittedLine[1]
            ).filter(s -> s != null && !s.isEmpty()).collect(joining("-"));
        }

        return new DescriptionLine(splittedLine[0], splittedLine[1]);
    }

    private String getDateFromTimestamp(String timestamp) {
        return Instant.ofEpochSecond(Long.valueOf(timestamp)).toString();
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
