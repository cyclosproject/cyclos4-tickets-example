package org.socialtrade.cyclostickets;

import java.io.File;
import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Contains the configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "cyclos")
public class CyclosConfig {

    private URI serverUri;
    private URI appUri;
    private File dataDir;

    public URI getServerUri() {
        return serverUri;
    }

    public URI getAppUri() {
        return appUri;
    }

    public File getDataDir() {
        return dataDir;
    }

    public void setServerUri(URI serverUri) {
        this.serverUri = serverUri;
    }

    public void setAppUri(URI appUri) {
        this.appUri = appUri;
    }

    public void setDataDir(File dataDir) {
        if (dataDir != null && dataDir.getPath().contains("%t")) {
            this.dataDir = new File(dataDir.getPath().replace("%t", System.getProperty("java.io.tmpdir")));
        } else {
            this.dataDir = dataDir;
        }
        this.dataDir.mkdirs();
    }

    public URI appUri(String relativePath) {
        return appUriBuilder().path(relativePath).build().toUri();
    }

    public UriComponentsBuilder appUriBuilder() {
        return UriComponentsBuilder.fromUri(getAppUri());
    }

    public URI serverUri(String relativePath) {
        return serverUriBuilder().path(relativePath).build().toUri();
    }

    public UriComponentsBuilder serverUriBuilder() {
        return UriComponentsBuilder.fromUri(getServerUri());
    }
}
