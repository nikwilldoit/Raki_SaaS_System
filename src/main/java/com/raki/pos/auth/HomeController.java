package com.raki.pos.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/") //Root path - http://localhost:8080/
    public String home() {
        return "POS Backend is running! Ready for React frontend on port 3000";
    }

    @GetMapping("/api/test") //Test path - http://localhost:8080/api/test
    public String test() {
        return "Backend connection successful!";
    }
}