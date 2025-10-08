package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for `ApiExceptionHandler` covering IllegalArgumentException -> 400 and IllegalStateException -> 409.
 */
@WebMvcTest(controllers = SimpleTestController.class)
@Import(ApiExceptionHandler.class)
public class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returns400ForIllegalArgument() throws Exception {
        mockMvc.perform(get("/test/illegal-arg").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").value("invalid param"));
    }

    @Test
    void returns409ForIllegalState() throws Exception {
        mockMvc.perform(get("/test/illegal-state").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("conflict"))
                .andExpect(jsonPath("$.message").value("already exists"));
    }
}
