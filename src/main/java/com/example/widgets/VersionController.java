package com.example.widgets;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes service build version for diagnostics. */
@RestController
public class VersionController {

    private static final String DEFAULT_VERSION = "unknown";

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public VersionController(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    @GetMapping("/version")
    public VersionResponse version() {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        String version = buildProperties != null ? buildProperties.getVersion() : DEFAULT_VERSION;
        return new VersionResponse(version);
    }

    public record VersionResponse(String version) {
    }
}
