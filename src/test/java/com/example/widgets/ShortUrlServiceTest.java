package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "app.public-base-url=http://short.test")
@AutoConfigureMockMvc
class ShortUrlServiceTest {

    @MockBean
    ShortUrlCodeGenerator codeGenerator;

    @Autowired
    ShortUrlService service;

    @Test
    void rejectsMalformedUrlAsInvalidUrl() {
        assertThatThrownBy(() -> service.shorten("http://["))
                .isInstanceOf(InvalidShortUrlException.class)
                .hasMessage("invalid_url: URL must be an absolute URL");
    }

    @Test
    void failsAfterBoundedCollisionRetries() {
        when(codeGenerator.generateCode()).thenReturn("abc1234");
        service.shorten("https://example.com/collision");
        assertThatThrownBy(() -> service.shorten("https://example.com/collision-2"))
                .isInstanceOf(ShortUrlCreationException.class)
                .hasMessage("Unable to create a unique short URL after 10 attempts");
    }
}
