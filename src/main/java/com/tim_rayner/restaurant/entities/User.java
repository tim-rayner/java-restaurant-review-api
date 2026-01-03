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
@Table(name="USERS")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="USERNAME", unique = true)
    @Getter @Setter
    private String username;

    @Column(name="CITY")
    @Getter @Setter
    private String city;

    @Column(name="COUNTY")
    @Getter @Setter
    private String county;

    @Column(name="POST_CODE")
    @Getter @Setter
    private String postCode;

    @Column(name="ACTIVE_PEANUT_ALLERGY")
    @Getter @Setter
    private Boolean activePeanutAllergy;

    @Column(name="ACTIVE_EGG_ALLERGY")
    @Getter @Setter
    private Boolean activeEggAllergy;

    @Column(name="ACTIVE_DAIRY_ALLERGY")
    @Getter @Setter
    private Boolean activeDairyAllergy;

}
