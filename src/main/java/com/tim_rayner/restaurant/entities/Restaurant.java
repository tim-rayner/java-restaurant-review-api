package com.tim_rayner.restaurant.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="RESTAURANTS")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Restaurant {
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Column(name="POST_CODE")
    @Getter @Setter
    private String postCode;

    @Column(name="NAME")
    @Getter @Setter
    private String name;

    @Column(name="BIO")
    @Getter @Setter
    private String bio;

    @Column(name="OVERALL_RATING")
    @Getter @Setter
    private Double overallRating;

    @Column(name="PEANUT_RATING")
    @Getter @Setter
    private Double peanutRating;

    @Column(name="EGG_RATING")
    @Getter @Setter
    private Double eggRating;

    @Column(name="DAIRY_RATING")
    @Getter @Setter
    private Double dairyRating;

}
