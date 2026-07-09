package com.example.widgets;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VersionControllerTest {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class WhenBuildPropertiesPresent {

        @Autowired
        MockMvc mvc;

        @Test
        void returnsVersionAsJson() throws Exception {
            mvc.perform(get("/version"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.version").isNotEmpty());
        }
    }

    @Nested
    @SpringBootTest(classes = {WidgetsApplication.class, NoBuildPropertiesTestConfig.class})
    @AutoConfigureMockMvc
    class WhenBuildPropertiesMissing {

        @Autowired
        MockMvc mvc;

        @Test
        void returnsUnknownVersionWithJsonContentType() throws Exception {
            mvc.perform(get("/version"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.version").value("unknown"));
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class NoBuildPropertiesTestConfig {

        @Bean
        static org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor removeBuildPropertiesBeanDefinition() {
            return new org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor() {
                @Override
                public void postProcessBeanDefinitionRegistry(
                        org.springframework.beans.factory.support.BeanDefinitionRegistry registry) {
                    if (registry.containsBeanDefinition("buildProperties")) {
                        registry.removeBeanDefinition("buildProperties");
                    }
                }

                @Override
                public void postProcessBeanFactory(
                        org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
                }
            };
        }
    }
}
