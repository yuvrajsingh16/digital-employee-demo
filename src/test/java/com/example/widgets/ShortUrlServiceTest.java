package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ShortUrlServiceTest {

    @TestConfiguration
    static class Configuration {

        @Bean
        @Primary
        ShortUrlCodeGenerator shortUrlCodeGenerator() {
            return Mockito.mock(ShortUrlCodeGenerator.class);
        }
    }

    @MockBean
    ShortUrlJpaRepository repository;

    @Autowired
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
        when(repository.findByOriginalUrl("https://example.com/collision"))
                .thenReturn(Optional.empty());
        when(repository.save(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("collision"));
        when(((ShortUrlCodeGenerator) codeGenerator).generateCode())
                .thenReturn("abc1234");

        assertThatThrownBy(() -> service.shorten("https://example.com/collision"))
                .isInstanceOf(ShortUrlCreationException.class)
                .hasMessage("Unable to create a unique short URL after 10 attempts");
    }
}
