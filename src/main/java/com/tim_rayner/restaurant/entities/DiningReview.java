package com.tim_rayner.restaurant.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="DINING_REVIEWS")
public class DiningReview {
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(name="AUTHOR")
    @Getter @Setter
    private String author;

    @Column(name="RESTAURANT_ID")
    @Getter @Setter
    private Long restaurantId;

    @Column(name="PEANUT_SCORE", nullable = true)
    @Getter @Setter
    private Long peanutScore;

    @Column(name="EGG_SCORE", nullable = true)
    @Getter @Setter
    private Long eggScore;

    @Column(name="DAIRY_SCORE", nullable = true)
    @Getter @Setter
    private Long dairyScore;

    @Column(name="COMMENT", nullable = true)
    @Getter @Setter
    private String comment;

    @Column(name="REVIEW_STATUS")
    @Getter @Setter
    private ReviewStatus reviewStatus;
}
