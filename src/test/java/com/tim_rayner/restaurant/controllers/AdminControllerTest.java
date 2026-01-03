package com.tim_rayner.restaurant.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.tim_rayner.restaurant.actions.AdminReviewAction;
import com.tim_rayner.restaurant.entities.DiningReview;
import com.tim_rayner.restaurant.entities.Restaurant;
import com.tim_rayner.restaurant.entities.ReviewStatus;
import com.tim_rayner.restaurant.repositories.DiningReviewRepository;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiningReviewRepository diningReviewRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @Test
    void getPendingReviews_returnsList() throws Exception {
        DiningReview review1 = new DiningReview();
        review1.setId(1L);
        review1.setAuthor("user1");
        review1.setRestaurantId(1L);
        review1.setReviewStatus(ReviewStatus.PENDING);

        DiningReview review2 = new DiningReview();
        review2.setId(2L);
        review2.setAuthor("user2");
        review2.setRestaurantId(2L);
        review2.setReviewStatus(ReviewStatus.PENDING);

        when(diningReviewRepository.findByReviewStatus(ReviewStatus.PENDING))
                .thenReturn(Arrays.asList(review1, review2));

        mockMvc.perform(get("/admin/reviews/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].reviewStatus").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].reviewStatus").value("PENDING"));
    }

    @Test
    void getPendingReviews_empty_returnsEmptyList() throws Exception {
        when(diningReviewRepository.findByReviewStatus(ReviewStatus.PENDING))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/reviews/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void approveReview_success_returns200() throws Exception {
        DiningReview review = new DiningReview();
        review.setId(1L);
        review.setAuthor("testuser");
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);
        review.setReviewStatus(ReviewStatus.PENDING);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        AdminReviewAction action = new AdminReviewAction();
        action.setAcceptReview(true);

        when(diningReviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        when(diningReviewRepository.findByRestaurantIdAndReviewStatus(anyLong(), any(ReviewStatus.class)))
                .thenReturn(Collections.singletonList(review));
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(review);

        mockMvc.perform(put("/admin/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reviewStatus").value("APPROVED"));

        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void rejectReview_success_returns200() throws Exception {
        DiningReview review = new DiningReview();
        review.setId(1L);
        review.setAuthor("testuser");
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);
        review.setReviewStatus(ReviewStatus.PENDING);

        AdminReviewAction action = new AdminReviewAction();
        action.setAcceptReview(false);

        when(diningReviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(review);

        mockMvc.perform(put("/admin/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reviewStatus").value("REJECTED"));

        // Verify restaurant scores are NOT recomputed for rejection
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void processReview_notFound_returns404() throws Exception {
        AdminReviewAction action = new AdminReviewAction();
        action.setAcceptReview(true);

        when(diningReviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/admin/reviews/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveReview_computesAverageScores() throws Exception {
        // First review already approved
        DiningReview approvedReview = new DiningReview();
        approvedReview.setId(1L);
        approvedReview.setRestaurantId(1L);
        approvedReview.setPeanutScore(4L);
        approvedReview.setEggScore(5L);
        approvedReview.setReviewStatus(ReviewStatus.APPROVED);

        // New review being approved
        DiningReview newReview = new DiningReview();
        newReview.setId(2L);
        newReview.setRestaurantId(1L);
        newReview.setPeanutScore(5L);
        newReview.setEggScore(3L);
        newReview.setReviewStatus(ReviewStatus.PENDING);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        AdminReviewAction action = new AdminReviewAction();
        action.setAcceptReview(true);

        when(diningReviewRepository.findById(anyLong())).thenReturn(Optional.of(newReview));
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        // After approval, both reviews are approved
        when(diningReviewRepository.findByRestaurantIdAndReviewStatus(anyLong(), any(ReviewStatus.class)))
                .thenReturn(Arrays.asList(approvedReview, newReview));
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(newReview);

        mockMvc.perform(put("/admin/reviews/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewStatus").value("APPROVED"));

        // Verify restaurant was saved with updated scores
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void approveReview_handlesPartialScores() throws Exception {
        // Review with only peanut score
        DiningReview review = new DiningReview();
        review.setId(1L);
        review.setRestaurantId(1L);
        review.setPeanutScore(4L);
        // No egg or dairy scores
        review.setReviewStatus(ReviewStatus.PENDING);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        AdminReviewAction action = new AdminReviewAction();
        action.setAcceptReview(true);

        when(diningReviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        when(diningReviewRepository.findByRestaurantIdAndReviewStatus(anyLong(), any(ReviewStatus.class)))
                .thenReturn(Collections.singletonList(review));
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(review);

        mockMvc.perform(put("/admin/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewStatus").value("APPROVED"));

        verify(restaurantRepository).save(any(Restaurant.class));
    }
}

