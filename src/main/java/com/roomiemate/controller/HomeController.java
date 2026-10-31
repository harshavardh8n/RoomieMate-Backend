package com.roomiemate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "RoomieMate Backend");
        response.put("version", "1.0.0");
        response.put("swaggerUI", "/swagger-ui.html");
        response.put("apiDocs", "/api-docs");
        response.put("h2Console", "/h2-console");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "HEALTHY");
        return ResponseEntity.ok(status);
    }
}

