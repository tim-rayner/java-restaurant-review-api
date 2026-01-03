package com.tim_rayner.restaurant.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="RESTAURANTS")
public class Restaurant {
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(name="NAME")
    @Getter @Setter
    private String name;

    @Column(name="BIO")
    @Getter @Setter
    private String bio;

    @Column(name="OVERALL_RATING")
    @Getter @Setter
    private double overallRating;

    @Column(name="PEANUT_RATING")
    @Getter @Setter
    private double peanutRating;

    @Column(name="EGG_RATING")
    @Getter @Setter
    private double eggRating;

    @Column(name="DAIRY_RATING")
    @Getter @Setter
    private double dairyRating;

}
