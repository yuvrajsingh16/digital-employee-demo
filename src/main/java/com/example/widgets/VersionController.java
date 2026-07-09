package com.example.widgets;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    private static final String FALLBACK_VERSION = "unknown";

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public VersionController(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    @GetMapping("/version")
    public VersionResponse version() {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        if (buildProperties == null) {
            return new VersionResponse(FALLBACK_VERSION);
        }
        return new VersionResponse(buildProperties.getVersion());
    }

    public record VersionResponse(String version) {
    }
}
