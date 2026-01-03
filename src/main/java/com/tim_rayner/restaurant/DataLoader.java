package com.tim_rayner.restaurant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tim_rayner.restaurant.entities.DiningReview;
import com.tim_rayner.restaurant.entities.Restaurant;
import com.tim_rayner.restaurant.entities.ReviewStatus;
import com.tim_rayner.restaurant.entities.User;
import com.tim_rayner.restaurant.repositories.DiningReviewRepository;
import com.tim_rayner.restaurant.repositories.RestaurantRepository;
import com.tim_rayner.restaurant.repositories.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final DiningReviewRepository diningReviewRepository;

    public DataLoader(
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            DiningReviewRepository diningReviewRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.diningReviewRepository = diningReviewRepository;
    }

    @Override
    public void run(String... args) {
        // Seed Users
        User user1 = new User();
        user1.setUsername("johndoe");
        user1.setCity("London");
        user1.setCounty("Greater London");
        user1.setPostCode("SW1A 1AA");
        user1.setActivePeanutAllergy(true);
        user1.setActiveEggAllergy(false);
        user1.setActiveDairyAllergy(false);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("janedoe");
        user2.setCity("Manchester");
        user2.setCounty("Greater Manchester");
        user2.setPostCode("M1 1AA");
        user2.setActivePeanutAllergy(false);
        user2.setActiveEggAllergy(true);
        user2.setActiveDairyAllergy(true);
        userRepository.save(user2);

        // Seed Restaurants
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("The Allergy-Friendly Bistro");
        restaurant1.setPostCode("SW1A 1AA");
        restaurant1.setBio("A cozy bistro specializing in allergy-conscious dining.");
        restaurant1.setPeanutRating(4.5);
        restaurant1.setEggRating(4.0);
        restaurant1.setDairyRating(3.75);
        restaurant1.setOverallRating(4.08);
        restaurant1 = restaurantRepository.save(restaurant1);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Safe Eats Cafe");
        restaurant2.setPostCode("SW1A 1AA");
        restaurant2.setBio("100% nut-free kitchen with great options for all allergies.");
        restaurant2.setPeanutRating(5.0);
        restaurant2.setEggRating(4.25);
        restaurant2.setOverallRating(4.63);
        restaurant2 = restaurantRepository.save(restaurant2);

        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Manchester Munch");
        restaurant3.setPostCode("M1 1AA");
        restaurant3.setBio("Family-friendly dining in the heart of Manchester.");
        restaurant3.setDairyRating(4.0);
        restaurant3.setOverallRating(4.0);
        restaurant3 = restaurantRepository.save(restaurant3);

        // Seed Dining Reviews
        DiningReview review1 = new DiningReview();
        review1.setAuthor("johndoe");
        review1.setRestaurantId(restaurant1.getId());
        review1.setPeanutScore(5L);
        review1.setEggScore(4L);
        review1.setComment("Great experience! Very accommodating for my peanut allergy.");
        review1.setReviewStatus(ReviewStatus.APPROVED);
        diningReviewRepository.save(review1);

        DiningReview review2 = new DiningReview();
        review2.setAuthor("janedoe");
        review2.setRestaurantId(restaurant1.getId());
        review2.setPeanutScore(4L);
        review2.setEggScore(4L);
        review2.setDairyScore(4L);
        review2.setComment("Lovely atmosphere and safe food options.");
        review2.setReviewStatus(ReviewStatus.APPROVED);
        diningReviewRepository.save(review2);

        DiningReview review3 = new DiningReview();
        review3.setAuthor("johndoe");
        review3.setRestaurantId(restaurant2.getId());
        review3.setPeanutScore(5L);
        review3.setComment("Best nut-free place I've ever been to!");
        review3.setReviewStatus(ReviewStatus.PENDING);
        diningReviewRepository.save(review3);

        System.out.println("Database seeded 🌱");
    }
}