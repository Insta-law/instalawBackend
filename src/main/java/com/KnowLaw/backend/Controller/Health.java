package com.KnowLaw.backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        return new ResponseEntity<>("All okay boys !",HttpStatus.OK);

    }
}
