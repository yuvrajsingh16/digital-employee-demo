package com.example.widgets;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "short_url_mappings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_short_url_mappings_code", columnNames = "code"),
        @UniqueConstraint(name = "uk_short_url_mappings_original_url", columnNames = "original_url")
})
public class ShortUrlMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 7)
    private String code;

    @Column(name = "original_url", nullable = false, unique = true, length = 2048)
    private String originalUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ShortUrlMappingEntity() {
    }

    public ShortUrlMappingEntity(String code, String originalUrl, Instant createdAt) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
    }

    public String getCode() {
        return code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
