package com.tim_rayner.restaurant.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tim_rayner.restaurant.entities.User;

/**
 * Repository interface for User entity operations.
 * Extends CrudRepository to provide standard CRUD operations,
 * plus custom query methods for user lookup and existence checks.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Fetches a user profile by their username (display name).
     * Use case: "I want to fetch the user profile belonging to a given display name"
     * 
     * @param username the unique display name of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user exists with the given username.
     * Use case: "I want to verify that the user exists, based on the user display name"
     * 
     * @param username the display name to check for existence
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

}
