package cz.sparko.boxitory.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoxProvider {
    private final String url;
    private final String name;
    @JsonProperty("checksum_type")
    private final String checksumType;
    private final String checksum;

    public BoxProvider(String url, String name, String checksumType, String checksum) {
        this.url = url;
        this.name = name;
        this.checksumType = checksumType;
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "BoxProvider{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", checksumType='" + checksumType + '\'' +
                ", checksum='" + checksum  + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BoxProvider that = (BoxProvider) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(name, that.name) &&
                Objects.equals(checksumType, that.checksumType) &&
                Objects.equals(checksum, that.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name, checksumType, checksum);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getChecksumType() { return checksumType; }

    public String getChecksum() { return checksum; }
}
