package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "app.public-base-url=http://short.test")
class ShortUrlJpaPersistenceTest {

    @MockBean
    ShortUrlCodeGenerator codeGenerator;

    @Autowired
    ShortUrlService service;

    @Test
    void returnsSameCodeForSameUrlAcrossServiceInvocations() {
        when(codeGenerator.generateCode()).thenReturn("abc1234", "zzzzzzz");

        ShortUrlResponse firstResponse = service.shorten("https://example.com/persisted");
        ShortUrlResponse secondResponse = service.shorten("https://example.com/persisted");

        assertThat(secondResponse.code()).isEqualTo(firstResponse.code());
        assertThat(secondResponse.shortUrl()).isEqualTo("http://short.test/" + firstResponse.code());
        assertThat(service.resolveOriginalUrl(firstResponse.code())).contains("https://example.com/persisted");
    }
}
