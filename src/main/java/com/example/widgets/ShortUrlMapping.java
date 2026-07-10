package com.example.widgets;

import java.time.Instant;

public record ShortUrlMapping(String code, String originalUrl, Instant createdAt) {
}
