package com.delivery.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/db")
    public String testDb() {
        return "User service je povezan na bazu i Eureka, jupiii";
    }
}