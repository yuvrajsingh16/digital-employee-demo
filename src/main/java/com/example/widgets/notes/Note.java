package com.example.widgets.notes;

import java.time.Instant;
import java.util.UUID;

public record Note(UUID id, String title, String content, Instant createdAt, Instant updatedAt) {

    public Note {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt must not be null");
        }
    }
}
