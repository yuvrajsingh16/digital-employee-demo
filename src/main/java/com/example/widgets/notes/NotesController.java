package com.example.widgets.notes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
public class NotesController {

    private final NoteService service;

    public NotesController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@RequestBody NoteRequest request) {
        return ResponseEntity.ok(NoteResponse.from(service.create(request.title(), request.content())));
    }

    @GetMapping
    public List<NoteResponse> list() {
        return service.listAll().stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(NoteResponse.from(service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable UUID id, @RequestBody NoteRequest request) {
        return ResponseEntity.ok(NoteResponse.from(service.update(id, request.title(), request.content())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record NoteRequest(String title, String content) {
    }

    public record NoteResponse(UUID id, String title, String content, Instant createdAt, Instant updatedAt) {
        public static NoteResponse from(Note note) {
            return new NoteResponse(note.id(), note.title(), note.content(), note.createdAt(), note.updatedAt());
        }
    }
}
