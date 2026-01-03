package com.tim_rayner.restaurant.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController {

    @GetMapping("/helloworld")
    public String helloWorld() {
        return "Hello Worlds!";
    }
}