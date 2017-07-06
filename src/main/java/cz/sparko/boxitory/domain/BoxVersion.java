package cz.sparko.boxitory.domain;

import java.util.List;
import java.util.Objects;

public class BoxVersion {
    private final String version;
    private final List<BoxProvider> providers;

    public BoxVersion(String version, List<BoxProvider> providers) {
        this.version = version;
        this.providers = providers;
    }

    @Override
    public String toString() {
        return "BoxVersion{" +
                "version='" + version + '\'' +
                ", providers=" + providers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BoxVersion thatVersion = (BoxVersion) o;
        return Objects.equals(this.version, thatVersion.version) &&
                providers.containsAll(thatVersion.getProviders()) && thatVersion.getProviders().containsAll(providers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, providers);
    }

    public String getVersion() {
        return version;
    }

    public List<BoxProvider> getProviders() {
        return providers;
    }
}
