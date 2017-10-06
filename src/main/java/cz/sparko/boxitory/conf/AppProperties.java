package cz.sparko.boxitory.conf;

import cz.sparko.boxitory.factory.HashServiceFactory.HashAlgoritm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "box")
public class AppProperties {
    private String home = ".";
    private String host_prefix = "";
    private HashAlgoritm checksum = HashAlgoritm.DISABLED;
    private boolean sort_desc = false;
    private int checksum_buffer_size = 1024;

    public String getHome() {
        return home;
    }

    public String getHost_prefix() {
        return host_prefix;
    }

    public boolean isSort_desc() {
        return sort_desc;
    }

    public HashAlgoritm getChecksum() {
        return checksum;
    }

    public int getChecksum_buffer_size() {
        return checksum_buffer_size;
    }

    public void setSort_desc(boolean sort_desc) {
        this.sort_desc = sort_desc;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setHost_prefix(String host_prefix) {
        this.host_prefix = host_prefix;
    }

    public void setChecksum(HashAlgoritm checksum) {
        this.checksum = checksum;
    }

    public void setChecksum_buffer_size(int checksum_buffer_size) {
        this.checksum_buffer_size = checksum_buffer_size;
    }
}
