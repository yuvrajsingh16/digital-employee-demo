package com.example.widgets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
public class ShortUrlController {

    private static final String CODE_PATTERN = "^[0-9A-Za-z]{7}$";

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortUrlResponse> shorten(@Valid @RequestBody ShortenRequest request, HttpServletRequest servletRequest) {
        String requestBaseUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort();
        ShortUrlResponse response = service.shorten(request.originalUrl(), requestBaseUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        if (!code.matches(CODE_PATTERN)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "short_url_not_found"));
        }
        return service.resolveOriginalUrl(code)
                .map(url -> ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "short_url_not_found")));
    }

    @ExceptionHandler(InvalidShortUrlException.class)
    public ResponseEntity<Map<String, String>> handleInvalidShortUrl(InvalidShortUrlException exception) {
        return ResponseEntity.unprocessableEntity().body(Map.of("error", "invalid_url", "message", exception.getMessage()));
    }

    public record ShortenRequest(@NotBlank String originalUrl) {
    }
}
