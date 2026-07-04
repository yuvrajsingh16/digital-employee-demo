package com.example.widgets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WidgetServiceTest {

    @Test
    void createsAndListsWidgets() {
        WidgetService service = new WidgetService();
        Widget a = service.create("bolt", 10);
        Widget b = service.create("nut", 5);

        assertThat(a.id()).isNotBlank();
        assertThat(service.list()).extracting(Widget::name).containsExactlyInAnyOrder("bolt", "nut");
        assertThat(service.get(a.id())).contains(a);
        assertThat(service.get(b.id())).contains(b);
    }

    @Test
    void getUnknownReturnsEmpty() {
        assertThat(new WidgetService().get("nope")).isEmpty();
    }
}
