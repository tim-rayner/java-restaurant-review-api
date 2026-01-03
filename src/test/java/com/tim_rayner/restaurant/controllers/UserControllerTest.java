package com.tim_rayner.restaurant.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.tim_rayner.restaurant.entities.User;
import com.tim_rayner.restaurant.repositories.UserRepository;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void createUser_success_returns201() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setCity("London");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.city").value("London"));
    }

    @Test
    void createUser_usernameExists_returns409() throws Exception {
        User user = new User();
        user.setUsername("existinguser");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUser_exists_returns200() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setCity("London");
        user.setCounty("Greater London");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.city").value("London"))
                .andExpect(jsonPath("$.county").value("Greater London"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_success_returns200() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setCity("London");

        User updatedUser = new User();
        updatedUser.setCity("Manchester");
        updatedUser.setActivePeanutAllergy(true);

        User savedUser = new User();
        savedUser.setUsername("testuser");
        savedUser.setCity("Manchester");
        savedUser.setActivePeanutAllergy(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(put("/users/testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.city").value("Manchester"))
                .andExpect(jsonPath("$.activePeanutAllergy").value(true));
    }

    @Test
    void updateUser_notFound_returns404() throws Exception {
        User updatedUser = new User();
        updatedUser.setCity("Manchester");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }
}

