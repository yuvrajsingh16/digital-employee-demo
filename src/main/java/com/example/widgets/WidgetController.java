package com.example.widgets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST surface for widgets. The backlog adds endpoints (e.g. DELETE) and validation. */
@RestController
@RequestMapping("/widgets")
public class WidgetController {

    private final WidgetService service;

    public WidgetController(WidgetService service) {
        this.service = service;
    }

    @GetMapping
    public List<Widget> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Widget> get(@PathVariable String id) {
        return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Widget create(@RequestBody CreateWidgetRequest request) {
        return service.create(request.name(), request.quantity());
    }

    public record CreateWidgetRequest(String name, int quantity) {
    }
}
