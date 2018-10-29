package cz.sparko.boxitory.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoxProvider {
    private final String url;
    private final String localUrl;
    private final String name;
    @JsonProperty("checksum_type")
    private final String checksumType;
    private final String checksum;

    public BoxProvider(String url, String localUrl, String name, String checksumType, String checksum) {
        this.url = url;
        this.localUrl = localUrl;
        this.name = name;
        this.checksumType = checksumType;
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "BoxProvider{" +
                "url='" + url + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", name='" + name + '\'' +
                ", checksumType='" + checksumType + '\'' +
                ", checksum='" + checksum + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoxProvider that = (BoxProvider) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(localUrl, that.localUrl) &&
                Objects.equals(name, that.name) &&
                Objects.equals(checksumType, that.checksumType) &&
                Objects.equals(checksum, that.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, localUrl, name, checksumType, checksum);
    }

    public String getUrl() {
        return url;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public String getName() {
        return name;
    }

    public String getChecksumType() { return checksumType; }

    public String getChecksum() { return checksum; }
}
