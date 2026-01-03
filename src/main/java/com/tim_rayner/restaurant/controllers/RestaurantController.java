package com.tim_rayner.restaurant.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tim_rayner.restaurant.entities.Restaurant;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRestaurant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        return restaurant.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @RequestParam String zipcode,
            @RequestParam String allergy) {
        
        List<Restaurant> restaurants;
        
        restaurants = switch (allergy.toLowerCase()) {
            case "peanut" -> restaurantRepository.findByPostCodeAndPeanutRatingNotNullOrderByPeanutRatingDesc(zipcode);
            case "egg" -> restaurantRepository.findByPostCodeAndEggRatingNotNullOrderByEggRatingDesc(zipcode);
            case "dairy" -> restaurantRepository.findByPostCodeAndDairyRatingNotNullOrderByDairyRatingDesc(zipcode);
            default -> null;
        };
        
        if (restaurants == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(restaurants);
    }
}
