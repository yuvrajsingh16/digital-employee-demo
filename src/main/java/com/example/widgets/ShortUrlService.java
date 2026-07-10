package com.example.widgets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ShortUrlService {

    private static final int CODE_LENGTH = 7;
    private static final int MAX_URL_LENGTH = 2048;
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final ShortUrlRepository repository;
    private final String publicBaseUrl;

    public ShortUrlService(ShortUrlRepository repository, @Value("${app.public-base-url:}") String publicBaseUrl) {
        this.repository = repository;
        this.publicBaseUrl = publicBaseUrl;
    }

    public ShortUrlResponse shorten(String originalUrl, String requestBaseUrl) {
        validateOriginalUrl(originalUrl);
        return repository.findByOriginalUrl(originalUrl)
                .map(mapping -> toResponse(mapping, requestBaseUrl))
                .orElseGet(() -> createShortUrl(originalUrl, requestBaseUrl));
    }

    public Optional<String> resolveOriginalUrl(String code) {
        return repository.findByCode(code).map(ShortUrlMapping::originalUrl);
    }

    private ShortUrlResponse createShortUrl(String originalUrl, String requestBaseUrl) {
        while (true) {
            ShortUrlMapping mapping = new ShortUrlMapping(generateCode(), originalUrl, Instant.now());
            try {
                ShortUrlMapping savedMapping = repository.save(mapping);
                return toResponse(savedMapping, requestBaseUrl);
            } catch (DataIntegrityViolationException exception) {
                if (isDuplicateOriginalUrl(exception)) {
                    return repository.findByOriginalUrl(originalUrl)
                            .map(existing -> toResponse(existing, requestBaseUrl))
                            .orElseThrow(() -> exception);
                }
            }
        }
    }

    private boolean isDuplicateOriginalUrl(DataIntegrityViolationException exception) {
        return exception.getMostSpecificCause() != null
                && exception.getMostSpecificCause().getMessage() != null
                && exception.getMostSpecificCause().getMessage().contains("original_url");
    }

    private void validateOriginalUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank() || originalUrl.length() > MAX_URL_LENGTH) {
            throw new InvalidShortUrlException("URL must be a non-empty absolute URL no longer than 2048 characters");
        }
        try {
            URI uri = URI.create(originalUrl);
            if (!uri.isAbsolute() || uri.getScheme() == null || uri.getHost() == null) {
                throw new InvalidShortUrlException("URL must be a valid absolute URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new InvalidShortUrlException("URL must be a valid absolute URL");
        }
    }

    private ShortUrlResponse toResponse(ShortUrlMapping mapping, String requestBaseUrl) {
        return new ShortUrlResponse(mapping.code(), buildShortUrl(mapping.code(), requestBaseUrl), mapping.originalUrl());
    }

    private String buildShortUrl(String code, String requestBaseUrl) {
        String baseUrl = publicBaseUrl.isBlank() ? requestBaseUrl : publicBaseUrl;
        return baseUrl.endsWith("/") ? baseUrl + code : baseUrl + "/" + code;
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            builder.append(BASE62.charAt(ThreadLocalRandom.current().nextInt(BASE62.length())));
        }
        return builder.toString();
    }
}
