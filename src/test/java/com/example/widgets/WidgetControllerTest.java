package com.example.widgets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void listsOk() throws Exception {
        mvc.perform(get("/widgets")).andExpect(status().isOk());
    }

    @Test
    void createsThenReturns() throws Exception {
        mvc.perform(post("/widgets").contentType("application/json").content("{\"name\":\"bolt\",\"quantity\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bolt"))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void getUnknownIs404() throws Exception {
        mvc.perform(get("/widgets/nope")).andExpect(status().isNotFound());
    }

    @Test
    void createWithBlankNameIs400() throws Exception {
        mvc.perform(post("/widgets").contentType("application/json").content("{\"name\":\"\",\"quantity\":3}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithNegativeQuantityIs400() throws Exception {
        mvc.perform(post("/widgets").contentType("application/json").content("{\"name\":\"bolt\",\"quantity\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
