package cz.sparko.boxitory.service.filesystem;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxProvider;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.service.BoxRepository;
import cz.sparko.boxitory.service.DescriptionProvider;
import cz.sparko.boxitory.service.HashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.sparko.boxitory.domain.BoxVersion.VERSION_COMPARATOR;

public class FilesystemBoxRepository implements BoxRepository {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemBoxRepository.class);

    private final String hostPrefix;
    private final File boxHome;
    private final HashService hashService;
    private final boolean sortDesc;

    private final DescriptionProvider descriptionProvider;

    public FilesystemBoxRepository(AppProperties appProperties,
                                   HashService hashService,
                                   DescriptionProvider descriptionProvider) {
        this.boxHome = new File(appProperties.getHome());
        this.hostPrefix = appProperties.getHost_prefix();
        this.sortDesc = appProperties.isSort_desc();
        this.hashService = hashService;
        this.descriptionProvider = descriptionProvider;
        LOG.info("setting BOX_HOME as [{}] and HOST_PREFIX as [{}]", boxHome.getAbsolutePath(), hostPrefix);
    }

    @Override
    public List<String> getBoxes() {
        return listPotentialBoxDirs()
                .filter(this::containsValidBoxFile)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Box> getBox(String boxName) {
        Map<String, List<File>> groupedBoxFiles = new HashMap<>();
        getBoxDir(boxName)
                .ifPresent(d -> groupedBoxFiles.putAll(groupBoxFilesByVersion(d)));

        List<BoxVersion> boxVersions = createBoxVersionsFromGroupedFiles(groupedBoxFiles, boxName);
        if (boxVersions.isEmpty()) {
            LOG.debug("no box versions found for [{}]", boxName);
            return Optional.empty();
        } else {
            LOG.debug("[{}] box versions found for [{}]", boxVersions.size(), boxName);
            return Optional.of(new Box(boxName, boxName, boxVersions));
        }
    }

    private Optional<File> getBoxDir(String boxName) {
        return listPotentialBoxDirs()
                .filter(File::isDirectory)
                .filter(f -> f.getName().equals(boxName))
                .findFirst();
    }

    private Stream<File> listPotentialBoxDirs() {
        File[] potentialBoxDirs = Optional.ofNullable(boxHome.listFiles(File::isDirectory))
                .orElseThrow(() -> new IllegalStateException(
                        "Repository directory [" + boxHome.getAbsolutePath() + "] is not a valid directory."));
        return Arrays.stream(potentialBoxDirs);
    }

    private Map<String, List<File>> groupBoxFilesByVersion(File boxDir) {
        File[] boxFiles = Optional.ofNullable(boxDir.listFiles())
                .orElse(new File[0]);
        return Arrays.stream(boxFiles)
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith(".box"))
                .filter(this::validateFilename)
                .collect(Collectors.groupingBy(
                        this::getBoxVersionFromFileName
                ));
    }

    private boolean validateFilename(File boxFile) {
        String filename = boxFile.getName();
        File parentDir = boxFile.getParentFile();

        if (!filename.matches(parentDir.getName() + "_(\\d+)_(\\w+)\\.box")) {
            LOG.warn("box file [{}] has wrong name. must be in format ${name}_${version}_${provider}.box", filename);
            return false;
        }
        return true;
    }

    private String getBoxVersionFromFileName(File file) {
        String filename = file.getName();
        List<String> parsedFilename = Arrays.asList(filename.split("_"));
        return parsedFilename.get(1);
    }

    private List<BoxVersion> createBoxVersionsFromGroupedFiles(Map<String, List<File>> groupedFiles, String boxName) {
        List<BoxVersion> boxVersions = new ArrayList<>();
        groupedFiles.forEach(
                (key, value) -> boxVersions.add(createBoxVersion(key, value, boxName))
        );
        if (sortDesc) {
            boxVersions.sort(VERSION_COMPARATOR.reversed());
        } else {
            boxVersions.sort(VERSION_COMPARATOR);
        }
        return boxVersions;
    }

    private BoxVersion createBoxVersion(String version, List<File> fileList, String boxName) {
        return new BoxVersion(
                version,
                descriptionProvider.getDescription(boxName, version).orElse(null),
                fileList.stream().map(this::createBoxProviderFromFile).collect(Collectors.toList())
        );
    }

    private BoxProvider createBoxProviderFromFile(File file) {
        String filename = file.getName();
        List<String> parsedFilename = Arrays.asList(filename.split("_"));

        String provider = parsedFilename.get(2);
        if (provider.endsWith(".box")) {
            provider = provider.substring(0, provider.length() - 4);
        }
        return new BoxProvider(
                hostPrefix + file.getAbsolutePath(),
                provider,
                hashService.getHashType(),
                hashService.getChecksum(file.getAbsolutePath())
        );
    }

    private boolean containsValidBoxFile(File file) {
        File[] files = file.listFiles(this::validateFilename);
        return files != null && files.length > 0;
    }
}
