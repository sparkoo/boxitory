package cz.sparko.boxitory.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoxVersion {
    public static final Comparator<BoxVersion> VERSION_COMPARATOR =
            Comparator.comparingInt(o -> Integer.parseInt(o.getVersion()));

    private final String version;
    private final String description;
    private final List<BoxProvider> providers;

    public BoxVersion(String version, String description, List<BoxProvider> providers) {
        this.version = version;
        this.description = description;
        this.providers = providers;
    }

    @Override
    public String toString() {
        return "BoxVersion{" +
                "version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", providers=" + providers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BoxVersion that = (BoxVersion) o;
        return Objects.equals(version, that.version) &&
                Objects.equals(description, that.description) &&
                providers.containsAll(that.getProviders()) && that.getProviders().containsAll(providers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, description, providers);
    }

    public String getVersion() {
        return version;
    }

    public List<BoxProvider> getProviders() {
        return providers;
    }

    public String getDescription() {
        return description;
    }
}
