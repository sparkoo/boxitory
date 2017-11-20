package cz.sparko.boxitory.conf;

import cz.sparko.boxitory.service.HashService.HashAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "box")
public class AppProperties {
    private String home = ".";
    private String host_prefix = "";
    private boolean sort_desc = false;

    private HashAlgorithm checksum = HashAlgorithm.DISABLED;
    private boolean checksum_persist = true;
    private int checksum_buffer_size = 1024;
    private int checksum_ensure = 1;

    public String getHome() {
        return home;
    }

    public String getHost_prefix() {
        return host_prefix;
    }

    public boolean isSort_desc() {
        return sort_desc;
    }

    public HashAlgorithm getChecksum() {
        return checksum;
    }

    public int getChecksum_buffer_size() {
        return checksum_buffer_size;
    }

    public boolean isChecksum_persist() { return checksum_persist; }

    public int getChecksum_ensure() { return checksum_ensure; }

    public void setSort_desc(boolean sort_desc) {
        this.sort_desc = sort_desc;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setHost_prefix(String host_prefix) {
        this.host_prefix = host_prefix;
    }

    public void setChecksum(HashAlgorithm checksum) {
        this.checksum = checksum;
    }

    public void setChecksum_buffer_size(int checksum_buffer_size) {
        this.checksum_buffer_size = checksum_buffer_size;
    }

    public void setChecksum_persist(boolean checksum_persist) {
        this.checksum_persist = checksum_persist;
    }

    public void setChecksum_ensure(int checksum_ensure) { this.checksum_ensure = checksum_ensure; }
}
