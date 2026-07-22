package com.cloudlab.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * local profile excludes DataSource/Redis — endpoints must degrade gracefully.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class DatabaseAndRedisControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void databaseStatusUnavailableOnLocal() throws Exception {
        mockMvc.perform(get("/api/database/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.available").value(false));
    }

    @Test
    void redisStatusUnavailableOnLocal() throws Exception {
        mockMvc.perform(get("/api/redis/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.available").value(false));
    }
}
