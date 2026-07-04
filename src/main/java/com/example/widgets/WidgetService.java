package com.example.widgets;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory widget store. Deliberately small and extendable — the backlog grows it. */
@Service
public class WidgetService {

    private final ConcurrentHashMap<String, Widget> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public List<Widget> list() {
        return new ArrayList<>(store.values());
    }

    public Optional<Widget> get(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Widget create(String name, int quantity) {
        String id = "w-" + seq.getAndIncrement();
        Widget w = new Widget(id, name, quantity);
        store.put(id, w);
        return w;
    }
}
