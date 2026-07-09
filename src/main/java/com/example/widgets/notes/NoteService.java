package com.example.widgets.notes;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NoteService {

    private final ConcurrentHashMap<UUID, Note> store = new ConcurrentHashMap<>();

    public Note create(String title, String content) {
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();
        Note note = new Note(id, title, content, now, now);
        store.put(id, note);
        return note;
    }

    public Note getById(UUID id) {
        Note note = store.get(id);
        if (note == null) {
            throw new NoteNotFoundException(id);
        }
        return note;
    }

    public List<Note> listAll() {
        return new ArrayList<>(store.values());
    }

    public Note update(UUID id, String title, String content) {
        Note existing = getById(id);
        Note updated = new Note(id, title, content, existing.createdAt(), Instant.now());
        store.put(id, updated);
        return updated;
    }

    public DeleteResult delete(UUID id) {
        return new DeleteResult(store.remove(id) != null, id);
    }
}
