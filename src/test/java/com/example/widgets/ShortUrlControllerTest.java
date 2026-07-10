package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.public-base-url=http://short.test")
@AutoConfigureMockMvc
class ShortUrlControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shortensUrlAndReturnsCreated() throws Exception {
        String response = mvc.perform(post("/api/shorten").contentType("application/json").content("{\"originalUrl\":\"https://example.com/articles/1\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        String code = jsonNode.get("code").asText();
        org.assertj.core.api.Assertions.assertThat(code).matches("^[0-9A-Za-z]{7}$");
        org.assertj.core.api.Assertions.assertThat(jsonNode.get("shortUrl").asText()).isEqualTo("http://short.test/" + code);
        org.assertj.core.api.Assertions.assertThat(jsonNode.get("originalUrl").asText()).isEqualTo("https://example.com/articles/1");
    }

    @Test
    void returnsSameCodeForSameUrl() throws Exception {
        String first = mvc.perform(post("/api/shorten").contentType("application/json").content("{\"originalUrl\":\"https://example.com/same\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String second = mvc.perform(post("/api/shorten").contentType("application/json").content("{\"originalUrl\":\"https://example.com/same\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(second).isEqualTo(first);
    }

    @Test
    void rejectsInvalidUrlWith422() throws Exception {
        mvc.perform(post("/api/shorten").contentType("application/json").content("{\"originalUrl\":\"not-a-url\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("invalid_url"));
    }

    @Test
    void rejectsTooLongUrlWith422() throws Exception {
        StringBuilder builder = new StringBuilder("https://example.com/");
        while (builder.length() <= 2050) {
            builder.append('a');
        }
        mvc.perform(post("/api/shorten").contentType("application/json").content("{\"originalUrl\":\"" + builder + "\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void redirectsExistingCode() throws Exception {
        String response = mvc.perform(post("/api/shorten").contentType("application/json").content("{\"originalUrl\":\"https://example.com/redirect\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String code = objectMapper.readTree(response).get("code").asText();
        mvc.perform(get("/" + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/redirect"));
    }

    @Test
    void returns404JsonForUnknownCode() throws Exception {
        mvc.perform(get("/abcdefg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("short_url_not_found"));
    }

    @Test
    void returns404JsonForInvalidCodeFormat() throws Exception {
        mvc.perform(get("/bad-code"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("short_url_not_found"));
    }

}
