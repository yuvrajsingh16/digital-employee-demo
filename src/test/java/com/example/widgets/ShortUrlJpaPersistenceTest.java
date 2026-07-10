package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.public-base-url=http://short.test")
class ShortUrlJpaPersistenceTest {

    @Autowired
    ShortUrlService firstService;

    @Autowired
    ShortUrlJpaRepository repository;

    @Test
    void returnsSameCodeForSameUrlAcrossServiceInvocations() {
        ShortUrlResponse firstResponse = firstService.shorten("https://example.com/persisted");
        ShortUrlService secondService = new ShortUrlService(repository, () -> "zzzzzzz", "http://short.test");
        ShortUrlResponse secondResponse = secondService.shorten("https://example.com/persisted");

        assertThat(secondResponse.code()).isEqualTo(firstResponse.code());
        assertThat(repository.findByOriginalUrl("https://example.com/persisted")).isPresent();
    }
}
