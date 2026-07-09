package com.example.widgets.notes;

import java.time.Instant;
import java.util.UUID;

public record Note(UUID id, String title, String content, Instant createdAt, Instant updatedAt) {
}
