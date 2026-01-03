package com.tim_rayner.restaurant.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.tim_rayner.restaurant.entities.DiningReview;
import com.tim_rayner.restaurant.entities.ReviewStatus;
import com.tim_rayner.restaurant.repositories.DiningReviewRepository;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;
import com.tim_rayner.restaurant.repositories.UserRepository;

@WebMvcTest(DiningReviewController.class)
class DiningReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiningReviewRepository diningReviewRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void submitReview_success_returns201() throws Exception {
        DiningReview review = new DiningReview();
        review.setAuthor("testuser");
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);
        review.setComment("Great food!");

        DiningReview savedReview = new DiningReview();
        savedReview.setId(1L);
        savedReview.setAuthor("testuser");
        savedReview.setRestaurantId(1L);
        savedReview.setPeanutScore(4L);
        savedReview.setComment("Great food!");
        savedReview.setReviewStatus(ReviewStatus.PENDING);

        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(savedReview);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("testuser"))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.peanutScore").value(4))
                .andExpect(jsonPath("$.reviewStatus").value("PENDING"));
    }

    @Test
    void submitReview_restaurantNotFound_returns404() throws Exception {
        DiningReview review = new DiningReview();
        review.setAuthor("testuser");
        review.setRestaurantId(999L);
        review.setPeanutScore(4L);

        when(restaurantRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitReview_userNotFound_returns404() throws Exception {
        DiningReview review = new DiningReview();
        review.setAuthor("nonexistentuser");
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);

        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsByUsername("nonexistentuser")).thenReturn(false);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReview_exists_returns200() throws Exception {
        DiningReview review = new DiningReview();
        review.setId(1L);
        review.setAuthor("testuser");
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);
        review.setEggScore(5L);
        review.setComment("Excellent!");
        review.setReviewStatus(ReviewStatus.APPROVED);

        when(diningReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("testuser"))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.peanutScore").value(4))
                .andExpect(jsonPath("$.eggScore").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent!"))
                .andExpect(jsonPath("$.reviewStatus").value("APPROVED"));
    }

    @Test
    void getReview_notFound_returns404() throws Exception {
        when(diningReviewRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reviews/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitReview_withAllScores_returns201() throws Exception {
        DiningReview review = new DiningReview();
        review.setAuthor("testuser");
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);
        review.setEggScore(5L);
        review.setDairyScore(3L);
        review.setComment("Good variety!");

        DiningReview savedReview = new DiningReview();
        savedReview.setId(1L);
        savedReview.setAuthor("testuser");
        savedReview.setRestaurantId(1L);
        savedReview.setPeanutScore(4L);
        savedReview.setEggScore(5L);
        savedReview.setDairyScore(3L);
        savedReview.setComment("Good variety!");
        savedReview.setReviewStatus(ReviewStatus.PENDING);

        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(savedReview);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.peanutScore").value(4))
                .andExpect(jsonPath("$.eggScore").value(5))
                .andExpect(jsonPath("$.dairyScore").value(3))
                .andExpect(jsonPath("$.reviewStatus").value("PENDING"));
    }
}

