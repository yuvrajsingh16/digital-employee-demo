package com.example.widgets;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlJpaRepository extends JpaRepository<ShortUrlMappingEntity, Long> {

    Optional<ShortUrlMappingEntity> findByCode(String code);

    Optional<ShortUrlMappingEntity> findByOriginalUrl(String originalUrl);
}
