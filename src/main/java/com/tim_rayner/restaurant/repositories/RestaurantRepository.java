package com.tim_rayner.restaurant.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tim_rayner.restaurant.entities.Restaurant;

/**
 * Repository interface for Restaurant entity operations.
 * Extends CrudRepository to provide standard CRUD operations,
 * plus custom query methods for allergy-based restaurant searches.
 */
public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    
    /**
     * Finds a restaurant by its unique identifier.
     * 
     * @param id the restaurant's unique ID
     * @return an Optional containing the restaurant if found, or empty if not found
     */
    Optional<Restaurant> findById(long id);
    
    /**
     * Finds all restaurants in a given post code that have a peanut allergy rating,
     * sorted by peanut rating in descending order (highest rated first).
     * 
     * @param postCode the post code to search within
     * @return list of restaurants with peanut ratings, sorted by rating descending
     */
    List<Restaurant> findByPostCodeAndPeanutRatingNotNullOrderByPeanutRatingDesc(String postCode);
    
    /**
     * Finds all restaurants in a given post code that have an egg allergy rating,
     * sorted by egg rating in descending order (highest rated first).
     * 
     * @param postCode the post code to search within
     * @return list of restaurants with egg ratings, sorted by rating descending
     */
    List<Restaurant> findByPostCodeAndEggRatingNotNullOrderByEggRatingDesc(String postCode);
    
    /**
     * Finds all restaurants in a given post code that have a dairy allergy rating,
     * sorted by dairy rating in descending order (highest rated first).
     * 
     * @param postCode the post code to search within
     * @return list of restaurants with dairy ratings, sorted by rating descending
     */
    List<Restaurant> findByPostCodeAndDairyRatingNotNullOrderByDairyRatingDesc(String postCode);
}