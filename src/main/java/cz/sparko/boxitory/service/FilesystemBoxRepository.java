package cz.sparko.boxitory.service;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxProvider;
import cz.sparko.boxitory.domain.BoxVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FilesystemBoxRepository implements BoxRepository {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemBoxRepository.class);

    private final String hostPrefix;
    private final File boxHome;

    @Autowired
    public FilesystemBoxRepository(AppProperties appProperties) {
        this.boxHome = new File(appProperties.getHome());
        this.hostPrefix = appProperties.getHost_prefix();
        LOG.info("setting BOX_HOME as [{}] and HOST_PREFIX as [{}]", boxHome.getAbsolutePath(), hostPrefix);
    }

    @Override
    public Optional<Box> getBox(String boxName) {
        Map<String, List<File>> groupedBoxFiles = new HashMap<>();
        getBoxDir(boxName)
                .ifPresent(d -> groupedBoxFiles.putAll(groupBoxFilesByVersion(d)));

        List<BoxVersion> boxVersions = createBoxVersionsFromGroupedFiles(groupedBoxFiles);
        if (boxVersions.isEmpty()) {
            LOG.debug("no box versions found for [{}]", boxName);
            return Optional.empty();
        } else {
            LOG.debug("[{}] box versions found for [{}]", boxVersions.size(), boxName);
            return Optional.of(new Box(boxName, boxName, boxVersions));
        }
    }

    private Optional<File> getBoxDir(String boxName) {
        File[] boxesHomeFiles = boxHome.listFiles();
        if (boxesHomeFiles == null) {
            throw new IllegalStateException("[" + boxHome.getAbsolutePath() + "] is not a valid folder");
        }
        return Arrays.stream(boxesHomeFiles)
                .filter(File::isDirectory)
                .filter(f -> f.getName().equals(boxName))
                .findFirst();
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
        List<String> parsedFilename = Arrays.asList(filename.split("_"));
        if (parsedFilename.size() != 3) {
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

    private List<BoxVersion> createBoxVersionsFromGroupedFiles(Map<String, List<File>> groupedFiles) {
        List<BoxVersion> boxVersions = new ArrayList<>();
        groupedFiles
                .entrySet()
                .forEach(
                    (entry) -> boxVersions.add(createBoxVersion(entry.getKey(), entry.getValue()))
                );
        return boxVersions;
    }

    private BoxVersion createBoxVersion(String version, List<File> fileList) {
        List<BoxProvider> boxProviders = new ArrayList<>();

        if (!fileList.isEmpty()) {
            fileList.forEach(
                    (file) -> boxProviders.add(createBoxProviderFromFile(file))
            );
        }

        return new BoxVersion(version, boxProviders);
    }

    private BoxProvider createBoxProviderFromFile(File file) {
        String filename = file.getName();
        List<String> parsedFilename = Arrays.asList(filename.split("_"));
        String provider = parsedFilename.get(2);
        if (provider.endsWith(".box")) {
            provider = provider.substring(0, provider.length() - 4);
        }
        return new BoxProvider(hostPrefix + file.getAbsolutePath(), provider);
    }
}
