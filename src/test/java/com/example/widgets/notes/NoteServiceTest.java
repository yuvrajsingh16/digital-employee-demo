package com.example.widgets.notes;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NoteServiceTest {

    @Test
    void createsListsGetsUpdatesAndDeletesNotes() {
        NoteService service = new NoteService();

        Note created = service.create("first note", "hello");
        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("first note");
        assertThat(created.content()).isEqualTo("hello");
        assertThat(created.createdAt()).isEqualTo(created.updatedAt());

        assertThat(service.listAll()).containsExactly(created);
        assertThat(service.getById(created.id())).isEqualTo(created);

        Note updated = service.update(created.id(), "updated note", "world");
        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.title()).isEqualTo("updated note");
        assertThat(updated.content()).isEqualTo("world");
        assertThat(updated.createdAt()).isEqualTo(created.createdAt());
        assertThat(updated.updatedAt()).isAfterOrEqualTo(created.updatedAt());
        assertThat(service.getById(created.id())).isEqualTo(updated);

        DeleteResult deleteResult = service.delete(created.id());
        assertThat(deleteResult.deleted()).isTrue();
        assertThat(deleteResult.id()).isEqualTo(created.id());
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void getByIdThrowsWhenNoteDoesNotExist() {
        NoteService service = new NoteService();

        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(NoteNotFoundException.class)
                .hasMessage("Note not found: " + id);
    }
}
