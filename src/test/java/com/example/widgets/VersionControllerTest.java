package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Properties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VersionControllerTest.TestBuildPropertiesConfiguration.class)
@AutoConfigureMockMvc
class VersionControllerTest {

    private static final String STUBBED_VERSION = "test-version";

    @Autowired
    MockMvc mvc;

    @Test
    void returnsBuildInfoVersion() throws Exception {
        mvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(STUBBED_VERSION));
    }

    @TestConfiguration
    static class TestBuildPropertiesConfiguration {
        @Bean
        BuildProperties buildProperties() {
            Properties properties = new Properties();
            properties.put("version", STUBBED_VERSION);
            return new BuildProperties(properties);
        }
    }
}
