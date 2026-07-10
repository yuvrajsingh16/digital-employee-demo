package com.example.widgets;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ShortUrlRepository {

    private final ShortUrlJpaRepository repository;

    public ShortUrlRepository(ShortUrlJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<ShortUrlMapping> findByCode(String code) {
        return repository.findByCode(code).map(this::toMapping);
    }

    public Optional<ShortUrlMapping> findByOriginalUrl(String originalUrl) {
        return repository.findByOriginalUrl(originalUrl).map(this::toMapping);
    }

    public ShortUrlMapping save(ShortUrlMapping mapping) {
        ShortUrlMappingEntity saved = repository.save(toEntity(mapping));
        return toMapping(saved);
    }

    private ShortUrlMappingEntity toEntity(ShortUrlMapping mapping) {
        return new ShortUrlMappingEntity(mapping.code(), mapping.originalUrl(), mapping.createdAt());
    }

    private ShortUrlMapping toMapping(ShortUrlMappingEntity entity) {
        return new ShortUrlMapping(entity.getCode(), entity.getOriginalUrl(), entity.getCreatedAt());
    }
}
