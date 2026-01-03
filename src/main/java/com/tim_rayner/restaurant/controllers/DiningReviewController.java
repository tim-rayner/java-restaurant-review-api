package com.tim_rayner.restaurant.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tim_rayner.restaurant.entities.DiningReview;
import com.tim_rayner.restaurant.entities.ReviewStatus;
import com.tim_rayner.restaurant.repositories.DiningReviewRepository;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;
import com.tim_rayner.restaurant.repositories.UserRepository;

@RestController
@RequestMapping("/reviews")
public class DiningReviewController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public DiningReviewController(
            DiningReviewRepository diningReviewRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<DiningReview> submitReview(@RequestBody DiningReview review) {
        // Validate that the restaurant exists
        if (!restaurantRepository.existsById(review.getRestaurantId())) {
            return ResponseEntity.notFound().build();
        }
        
        // Validate that the user exists by their display name (author)
        if (!userRepository.existsByUsername(review.getAuthor())) {
            return ResponseEntity.notFound().build();
        }
        
        // Set initial status to PENDING
        review.setReviewStatus(ReviewStatus.PENDING);
        
        DiningReview savedReview = diningReviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiningReview> getReview(@PathVariable Long id) {
        Optional<DiningReview> review = diningReviewRepository.findById(id);
        return review.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
}
