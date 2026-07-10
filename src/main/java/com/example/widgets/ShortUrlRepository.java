package com.example.widgets;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ShortUrlRepository {

    private final ConcurrentHashMap<String, ShortUrlMapping> byCode = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ShortUrlMapping> byOriginalUrl = new ConcurrentHashMap<>();

    public Optional<ShortUrlMapping> findByCode(String code) {
        return Optional.ofNullable(byCode.get(code));
    }

    public Optional<ShortUrlMapping> findByOriginalUrl(String originalUrl) {
        return Optional.ofNullable(byOriginalUrl.get(originalUrl));
    }

    public boolean save(ShortUrlMapping mapping) {
        ShortUrlMapping existingByCode = byCode.putIfAbsent(mapping.code(), mapping);
        if (existingByCode != null) {
            return false;
        }
        ShortUrlMapping existingByOriginalUrl = byOriginalUrl.putIfAbsent(mapping.originalUrl(), mapping);
        if (existingByOriginalUrl != null) {
            byCode.remove(mapping.code());
            return false;
        }
        return true;
    }
}
