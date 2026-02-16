package com.xod.bdsb.ml.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class HealthController {

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
    }
}
