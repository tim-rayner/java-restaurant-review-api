package com.tim_rayner.restaurant.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.tim_rayner.restaurant.entities.Restaurant;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @Test
    void createRestaurant_success_returns201() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setPostCode("SW1A 1AA");

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.postCode").value("SW1A 1AA"));
    }

    @Test
    void getRestaurant_exists_returns200() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setPostCode("SW1A 1AA");
        restaurant.setPeanutRating(4.75);
        restaurant.setEggRating(3.50);

        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.peanutRating").value(4.75))
                .andExpect(jsonPath("$.eggRating").value(3.50));
    }

    @Test
    void getRestaurant_notFound_returns404() throws Exception {
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/restaurants/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchRestaurants_byPeanutAllergy_returnsList() throws Exception {
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("Restaurant A");
        restaurant1.setPostCode("SW1A 1AA");
        restaurant1.setPeanutRating(4.75);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Restaurant B");
        restaurant2.setPostCode("SW1A 1AA");
        restaurant2.setPeanutRating(4.25);

        when(restaurantRepository.findByPostCodeAndPeanutRatingNotNullOrderByPeanutRatingDesc("SW1A 1AA"))
                .thenReturn(Arrays.asList(restaurant1, restaurant2));

        mockMvc.perform(get("/restaurants/search")
                .param("zipcode", "SW1A 1AA")
                .param("allergy", "peanut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Restaurant A"))
                .andExpect(jsonPath("$[0].peanutRating").value(4.75))
                .andExpect(jsonPath("$[1].name").value("Restaurant B"))
                .andExpect(jsonPath("$[1].peanutRating").value(4.25));
    }

    @Test
    void searchRestaurants_byEggAllergy_returnsList() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Restaurant A");
        restaurant.setPostCode("SW1A 1AA");
        restaurant.setEggRating(4.50);

        when(restaurantRepository.findByPostCodeAndEggRatingNotNullOrderByEggRatingDesc("SW1A 1AA"))
                .thenReturn(Collections.singletonList(restaurant));

        mockMvc.perform(get("/restaurants/search")
                .param("zipcode", "SW1A 1AA")
                .param("allergy", "egg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Restaurant A"))
                .andExpect(jsonPath("$[0].eggRating").value(4.50));
    }

    @Test
    void searchRestaurants_byDairyAllergy_returnsList() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Restaurant A");
        restaurant.setPostCode("SW1A 1AA");
        restaurant.setDairyRating(3.75);

        when(restaurantRepository.findByPostCodeAndDairyRatingNotNullOrderByDairyRatingDesc("SW1A 1AA"))
                .thenReturn(Collections.singletonList(restaurant));

        mockMvc.perform(get("/restaurants/search")
                .param("zipcode", "SW1A 1AA")
                .param("allergy", "dairy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Restaurant A"))
                .andExpect(jsonPath("$[0].dairyRating").value(3.75));
    }

    @Test
    void searchRestaurants_invalidAllergy_returns400() throws Exception {
        mockMvc.perform(get("/restaurants/search")
                .param("zipcode", "SW1A 1AA")
                .param("allergy", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchRestaurants_noResults_returnsEmptyList() throws Exception {
        when(restaurantRepository.findByPostCodeAndPeanutRatingNotNullOrderByPeanutRatingDesc("XX00 0XX"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/restaurants/search")
                .param("zipcode", "XX00 0XX")
                .param("allergy", "peanut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}

