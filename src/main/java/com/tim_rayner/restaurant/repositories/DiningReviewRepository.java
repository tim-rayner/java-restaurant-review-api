package com.tim_rayner.restaurant.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tim_rayner.restaurant.entities.DiningReview;
import com.tim_rayner.restaurant.entities.ReviewStatus;

/**
 * Repository interface for DiningReview entity operations.
 * Extends CrudRepository to provide standard CRUD operations,
 * plus custom query methods for filtering reviews by status and restaurant.
 */
public interface DiningReviewRepository extends CrudRepository<DiningReview, Long> {

    /**
     * Finds all dining reviews with a given status.
     * Use case: "As an admin, I want to get the list of all dining reviews that are pending approval"
     * 
     * @param reviewStatus the status to filter by (e.g., PENDING, APPROVED, REJECTED)
     * @return list of dining reviews matching the specified status
     */
    List<DiningReview> findByReviewStatus(ReviewStatus reviewStatus);

    /**
     * Finds all dining reviews for a specific restaurant with a given status.
     * Use case: "I want to fetch the set of all approved dining reviews belonging to this restaurant"
     * 
     * @param restaurantId the unique ID of the restaurant to filter by
     * @param reviewStatus the status to filter by (e.g., PENDING, APPROVED, REJECTED)
     * @return list of dining reviews for the restaurant matching the specified status
     */
    List<DiningReview> findByRestaurantIdAndReviewStatus(Long restaurantId, ReviewStatus reviewStatus);

}
