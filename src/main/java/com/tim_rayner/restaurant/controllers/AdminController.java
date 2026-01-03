package com.tim_rayner.restaurant.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tim_rayner.restaurant.actions.AdminReviewAction;
import com.tim_rayner.restaurant.entities.DiningReview;
import com.tim_rayner.restaurant.entities.Restaurant;
import com.tim_rayner.restaurant.entities.ReviewStatus;
import com.tim_rayner.restaurant.repositories.DiningReviewRepository;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;

    public AdminController(
            DiningReviewRepository diningReviewRepository,
            RestaurantRepository restaurantRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/reviews/pending")
    public ResponseEntity<List<DiningReview>> getPendingReviews() {
        List<DiningReview> pendingReviews = diningReviewRepository.findByReviewStatus(ReviewStatus.PENDING);
        return ResponseEntity.ok(pendingReviews);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<DiningReview> processReview(
            @PathVariable Long id,
            @RequestBody AdminReviewAction action) {
        
        Optional<DiningReview> reviewOptional = diningReviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        DiningReview review = reviewOptional.get();
        
        if (Boolean.TRUE.equals(action.getAcceptReview())) {
            review.setReviewStatus(ReviewStatus.APPROVED);
            diningReviewRepository.save(review);
            
            // Recompute restaurant scores
            recomputeRestaurantScores(review.getRestaurantId());
        } else {
            review.setReviewStatus(ReviewStatus.REJECTED);
            diningReviewRepository.save(review);
        }
        
        return ResponseEntity.ok(review);
    }

    private void recomputeRestaurantScores(Long restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            return;
        }
        
        Restaurant restaurant = restaurantOptional.get();
        List<DiningReview> approvedReviews = diningReviewRepository
                .findByRestaurantIdAndReviewStatus(restaurantId, ReviewStatus.APPROVED);
        
        if (approvedReviews.isEmpty()) {
            restaurant.setPeanutRating(null);
            restaurant.setEggRating(null);
            restaurant.setDairyRating(null);
            restaurant.setOverallRating(null);
            restaurantRepository.save(restaurant);
            return;
        }
        
        // Calculate averages for each score type
        Double peanutAvg = calculateAverage(approvedReviews, "peanut");
        Double eggAvg = calculateAverage(approvedReviews, "egg");
        Double dairyAvg = calculateAverage(approvedReviews, "dairy");
        
        restaurant.setPeanutRating(peanutAvg);
        restaurant.setEggRating(eggAvg);
        restaurant.setDairyRating(dairyAvg);
        
        // Calculate overall rating as average of available scores
        Double overallAvg = calculateOverallAverage(peanutAvg, eggAvg, dairyAvg);
        restaurant.setOverallRating(overallAvg);
        
        restaurantRepository.save(restaurant);
    }

    private Double calculateAverage(List<DiningReview> reviews, String scoreType) {
        List<Long> scores = reviews.stream()
                .map(review -> {
                    switch (scoreType) {
                        case "peanut": return review.getPeanutScore();
                        case "egg": return review.getEggScore();
                        case "dairy": return review.getDairyScore();
                        default: return null;
                    }
                })
                .filter(score -> score != null)
                .toList();
        
        if (scores.isEmpty()) {
            return null;
        }
        
        double average = scores.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        return roundToTwoDecimals(average);
    }

    private Double calculateOverallAverage(Double peanut, Double egg, Double dairy) {
        double sum = 0.0;
        int count = 0;
        
        if (peanut != null) {
            sum += peanut;
            count++;
        }
        if (egg != null) {
            sum += egg;
            count++;
        }
        if (dairy != null) {
            sum += dairy;
            count++;
        }
        
        if (count == 0) {
            return null;
        }
        
        return roundToTwoDecimals(sum / count);
    }

    private Double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

