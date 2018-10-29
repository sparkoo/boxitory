package cz.sparko.boxitory.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Full description of Vagrant's box as needed in http API.
 */
@SuppressWarnings("unused")
public class Box {
    private final String name;
    private final String description;
    private final List<BoxVersion> versions;

    public Box(String name, String description, List<BoxVersion> versions) {
        this.name = name;
        this.description = description;
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "Box{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", versions=" + versions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Box box = (Box) o;
        return Objects.equals(name, box.name) &&
                Objects.equals(description, box.description) &&
                versions.containsAll(box.versions) && box.versions.containsAll(versions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, versions);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<BoxVersion> getVersions() {
        return versions;
    }

    public List<String> getProviders() {
        return getVersions().stream()
                .flatMap(v -> v.getProviders().stream().map(BoxProvider::getName))
                .distinct()
                .collect(Collectors.toList());
    }
}
