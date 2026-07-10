package com.example.widgets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@Service
public class ShortUrlService {

    private static final int MAX_URL_LENGTH = 2048;
    private static final int MAX_SAVE_ATTEMPTS = 10;

    private final ShortUrlRepository repository;
    private final ShortUrlCodeGenerator codeGenerator;
    private final String publicBaseUrl;

    public ShortUrlService(ShortUrlRepository repository,
            ShortUrlCodeGenerator codeGenerator,
            @Value("${app.public-base-url:}") String publicBaseUrl) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.publicBaseUrl = publicBaseUrl;
    }

    public ShortUrlResponse shorten(String originalUrl) {
        validateOriginalUrl(originalUrl);
        Optional<ShortUrlMappingEntity> existingMapping = repository.findByOriginalUrl(originalUrl);
        if (existingMapping.isPresent()) {
            return toResponse(existingMapping.get());
        }
        return createShortUrl(originalUrl);
    }

    public Optional<String> resolveOriginalUrl(String code) {
        return repository.findByCode(code).map(ShortUrlMappingEntity::getOriginalUrl);
    }

    private ShortUrlResponse createShortUrl(String originalUrl) {
        for (int attempt = 0; attempt < MAX_SAVE_ATTEMPTS; attempt++) {
            try {
                return toResponse(repository.save(new ShortUrlMappingEntity(codeGenerator.generateCode(), originalUrl, Instant.now())));
            } catch (DataIntegrityViolationException exception) {
                Optional<ShortUrlMappingEntity> existingMapping = repository.findByOriginalUrl(originalUrl);
                if (existingMapping.isPresent()) {
                    return toResponse(existingMapping.get());
                }
            }
        }
        throw new ShortUrlCreationException("Unable to create a unique short URL after " + MAX_SAVE_ATTEMPTS + " attempts");
    }

    private void validateOriginalUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new InvalidShortUrlException("invalid_url: URL must not be blank");
        }
        if (originalUrl.length() > MAX_URL_LENGTH) {
            throw new InvalidShortUrlException("invalid_url: URL must not exceed 2048 characters");
        }
        try {
            URI uri = URI.create(originalUrl);
            if (!uri.isAbsolute() || uri.getScheme() == null || uri.getHost() == null) {
                throw new InvalidShortUrlException("invalid_url: URL must be an absolute URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new InvalidShortUrlException("invalid_url: URL must be an absolute URL", exception);
        }
    }

    private ShortUrlResponse toResponse(ShortUrlMappingEntity mapping) {
        return new ShortUrlResponse(mapping.getCode(), buildShortUrl(mapping.getCode()), mapping.getOriginalUrl());
    }

    private String buildShortUrl(String code) {
        String baseUrl = publicBaseUrl.isBlank() ? "" : publicBaseUrl;
        return baseUrl.endsWith("/") ? baseUrl + code : baseUrl + "/" + code;
    }
}
