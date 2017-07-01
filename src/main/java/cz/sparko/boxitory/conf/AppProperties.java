package cz.sparko.boxitory.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "box")
public class AppProperties {
    private String home = ".";
    private String host_prefix = "";
    private String checksum = "disabled";

    public String getHome() {
        return home;
    }

    public String getHost_prefix() {
        return host_prefix;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setHost_prefix(String host_prefix) {
        this.host_prefix = host_prefix;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
