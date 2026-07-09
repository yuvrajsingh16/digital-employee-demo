package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class VersionControllerTest {

    @Test
    void returnsUnknownWhenBuildInfoMissing() {
        VersionController controller = new VersionController(new MissingBuildPropertiesProvider());

        assertThat(controller.version().version()).isEqualTo("unknown");
    }

    private static final class MissingBuildPropertiesProvider implements ObjectProvider<BuildProperties> {
        @Override
        public BuildProperties getObject(Object... args) {
            throw new IllegalStateException("BuildProperties is not available");
        }

        @Override
        public BuildProperties getObject() {
            throw new IllegalStateException("BuildProperties is not available");
        }

        @Override
        public BuildProperties getIfAvailable() {
            return null;
        }

        @Override
        public BuildProperties getIfUnique() {
            return null;
        }

        @Override
        public Stream<BuildProperties> stream() {
            return Stream.empty();
        }

        @Override
        public Stream<BuildProperties> orderedStream() {
            return Stream.empty();
        }
    }
}
