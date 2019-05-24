package cz.sparko.boxitory.service.filesystem;

import cz.sparko.boxitory.conf.AppProperties;
import cz.sparko.boxitory.exception.NotFoundException;
import cz.sparko.boxitory.domain.Box;
import cz.sparko.boxitory.domain.BoxProvider;
import cz.sparko.boxitory.domain.BoxVersion;
import cz.sparko.boxitory.model.BoxStream;
import cz.sparko.boxitory.model.CalculatedChecksumCounter;
import cz.sparko.boxitory.model.FileBoxStream;
import cz.sparko.boxitory.service.BoxRepository;
import cz.sparko.boxitory.service.DescriptionProvider;
import cz.sparko.boxitory.service.HashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.sparko.boxitory.domain.BoxVersion.VERSION_COMPARATOR;

public class FilesystemBoxRepository implements BoxRepository {
    private static final Logger LOG = LoggerFactory.getLogger(FilesystemBoxRepository.class);

    private static final String BOX_FILENAME_REGEX = "_(\\d+)_(\\w+)\\.box";

    private final String hostPrefix;
    private final File boxHome;
    private final HashService hashService;
    private final boolean sortDesc;
    private final int ensuredChecksum;
    private final DescriptionProvider descriptionProvider;
    private final BoxPathType pathType;

    public FilesystemBoxRepository(AppProperties appProperties,
                                   HashService hashService,
                                   DescriptionProvider descriptionProvider) {
        this.boxHome = new File(appProperties.getHome());
        this.hostPrefix = appProperties.getHost_prefix();
        this.sortDesc = appProperties.isSort_desc();
        this.hashService = hashService;
        this.descriptionProvider = descriptionProvider;
        this.ensuredChecksum = appProperties.getChecksum_ensure();
        this.pathType = appProperties.getPath_type();
        LOG.info("setting BOX_HOME as [{}] and HOST_PREFIX as [{}]", boxHome.getAbsolutePath(), hostPrefix);
    }

    @Override
    public List<String> getBoxNames() {
        return listPotentialBoxDirs()
                .filter(this::containsValidBoxFile)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Box> getBox(String boxName) {
        Map<String, List<File>> groupedBoxFiles = new TreeMap<>(Collections.reverseOrder());
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

    @Override
    public List<Box> getBoxes() {
        return getBoxNames().stream()
                .map(this::getBox)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BoxStream> getBoxStream(String boxName, String boxProvider, String boxVersion) {
        Box box = getBox(boxName).orElseThrow(() -> NotFoundException.boxNotFound(boxName));
        BoxVersion version = getBoxVersion(box, boxVersion);
        BoxProvider provider = getBoxVersionProvider(version, boxProvider);

        File boxFile = new File(provider.getLocalUrl());
        if (boxFile.exists() && boxFile.isFile()) {
            return Optional.of(new FileBoxStream(new File(provider.getLocalUrl())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String latestVersionOfBox(String boxName, String boxProvider) {
        return getBox(boxName)
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName))
                .getVersions().stream().sorted(BoxVersion.VERSION_COMPARATOR.reversed())
                .filter(v -> v.getProviders().stream().anyMatch(p -> p.getName().equals(boxProvider)))
                .findFirst()
                .orElseThrow(() -> NotFoundException.boxNotFound(boxName))
                .getVersion();
    }

    private BoxVersion getBoxVersion(Box box, String boxVersion) {
        return box.getVersions().stream()
                .filter(v -> v.getVersion().equals(boxVersion))
                .findFirst()
                .orElseThrow(() -> NotFoundException.boxVersionNotFound(box.getName(), boxVersion));
    }

    private BoxProvider getBoxVersionProvider(BoxVersion boxVersion, String boxProvider) {
        return boxVersion.getProviders().stream()
                .filter(p -> p.getName().equals(boxProvider))
                .findFirst()
                .orElseThrow(() -> NotFoundException.boxVersionProviderNotFound(
                        boxVersion.getDescription(), boxVersion.getVersion(), boxProvider));
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
                .filter(this::validateFilename)
                .collect(Collectors.groupingBy(
                        this::getBoxVersionFromFileName
                ));
    }

    private boolean validateFilename(File boxFile) {
        String boxFilename = boxFile.getName();
        String boxParentDirname = boxFile.getParentFile().getName();

        if (!boxFilename.matches(boxParentDirname + BOX_FILENAME_REGEX)) {
            if (!isValidOtherFile(boxFilename, boxParentDirname)) {
                LOG.warn("box file [{}] has wrong name. must be in format ${name}_${version}_${provider}.box",
                         boxFilename);
            }
            return false;
        }
        return true;
    }

    private boolean isValidOtherFile(String filename, String parentDirname) {
        for (HashService.HashAlgorithm hashAlgorithm : HashService.HashAlgorithm.CHECKSUMS) {
            if (filename.matches(parentDirname + BOX_FILENAME_REGEX + hashAlgorithm.getFileExtension())) {
                return true;
            }
        }
        return FilesystemDescriptionProvider.DESCRIPTIONS_FILE.equals(filename);
    }

    private String getBoxVersionFromFileName(File file) {
        String filename = file.getName();
        List<String> parsedFilename = Arrays.asList(filename.split("_"));
        return parsedFilename.get(1);
    }

    private List<BoxVersion> createBoxVersionsFromGroupedFiles(Map<String, List<File>> groupedFiles, String boxName) {
        List<BoxVersion> boxVersions = new ArrayList<>();
        CalculatedChecksumCounter checksumCounter = new CalculatedChecksumCounter(ensuredChecksum);
        groupedFiles.forEach(
                (key, value) -> boxVersions.add(createBoxVersion(key, value, boxName, checksumCounter))
        );
        if (sortDesc) {
            boxVersions.sort(VERSION_COMPARATOR.reversed());
        } else {
            boxVersions.sort(VERSION_COMPARATOR);
        }
        return boxVersions;
    }

    private BoxVersion createBoxVersion(String version, List<File> fileList,
                                        String boxName, CalculatedChecksumCounter checksumCounter) {
        checksumCounter.increment();
        return new BoxVersion(
                version,
                descriptionProvider.getDescription(boxName, version).orElse(null),
                fileList.stream()
                        .map((file) -> createBoxProviderFromFile(file, boxName, version, checksumCounter))
                        .collect(Collectors.toList())
        );
    }

    private BoxProvider createBoxProviderFromFile(File file, String boxName, String version,
                                                  CalculatedChecksumCounter checksumCounter) {
        String filename = file.getName();
        List<String> parsedFilename = Arrays.asList(filename.split("_"));

        String provider = parsedFilename.get(2);
        if (provider.endsWith(".box")) {
            provider = provider.substring(0, provider.length() - 4);
        }

        return new BoxProvider(
                hostPrefix + createBoxUrl(file.getAbsolutePath(), boxName, version, provider),
                file.getAbsolutePath(),
                provider,
                hashService.getHashType(),
                hashService.getChecksum(
                        file.getAbsolutePath(),
                        !checksumCounter.isLimitOfCalculatedChecksumExceeded()
                )
        );
    }

    private String createBoxUrl(String boxAbsolutePath, String boxName, String version, String provider) {
        switch (this.pathType) {
            case RAW:
                return boxAbsolutePath;
            case BOXITORY:
                return String.format("/download/%s/%s/%s", boxName, provider, version);
            default:
                throw new RuntimeException("Unknown pathType " + this.pathType.name());
        }
    }

    private boolean containsValidBoxFile(File file) {
        for (File potentialBoxFile : Objects.requireNonNull(file.listFiles())) {
            if (this.validateFilename(potentialBoxFile)) {
                return true;
            }
        }
        return false;
    }
}
