package com.phegondev.usersmanagementsystem.controller;

import com.phegondev.usersmanagementsystem.dto.*;
import com.phegondev.usersmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest reg) {
        // Check if the username is available
        if (userService.isUsernameAvailable(reg.getUsername())) {
            // Proceed with registration if the username is available
            return ResponseEntity.ok(userService.register(reg));
        } else {
            // Return a response indicating that the username is already taken
            RegistrationResponse registrationResponse = new RegistrationResponse();
            registrationResponse.setMessage("Username is already taken");
            registrationResponse.setStatusCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(registrationResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<UsernameAvailabilityResponse> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        UsernameAvailabilityResponse response = new UsernameAvailabilityResponse();
        response.setAvailable(isAvailable);
        return ResponseEntity.ok(response);
    }
}

