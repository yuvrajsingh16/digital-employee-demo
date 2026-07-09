package com.example.widgets.notes;

import java.util.UUID;

public record DeleteResult(boolean deleted, UUID id) {
}
