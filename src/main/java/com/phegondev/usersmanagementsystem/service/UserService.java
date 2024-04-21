package com.phegondev.usersmanagementsystem.service;

import com.phegondev.usersmanagementsystem.dto.LoginRequest;
import com.phegondev.usersmanagementsystem.dto.LoginResponse;
import com.phegondev.usersmanagementsystem.dto.RegistrationResponse;
import com.phegondev.usersmanagementsystem.dto.RegistrationRequest;
import com.phegondev.usersmanagementsystem.entity.User;
import com.phegondev.usersmanagementsystem.repository.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;

@Service
public class UserService {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public RegistrationResponse register(RegistrationRequest registrationDetails) {
        RegistrationResponse resp = new RegistrationResponse();

        try {
            // Check if the username is already taken
            if (usersRepo.findByUsername(registrationDetails.getUsername()).isPresent()) {
                resp.setStatusCode(400);
                resp.setMessage("Username is already taken");
                return resp;
            }

            // Check if the email is already registered
            if (usersRepo.findByEmail(registrationDetails.getEmail()).isPresent()) {
                resp.setStatusCode(400);
                resp.setMessage("Email is already registered");
                return resp;
            }

            User ourUser = new User();
            ourUser.setEmail(registrationDetails.getEmail());
            ourUser.setUsername(registrationDetails.getUsername());
            ourUser.setPassword(passwordEncoder.encode(registrationDetails.getPassword()));
            User userResult = usersRepo.save(ourUser);
            if (userResult.getId() > 0) {
                resp.setMessage("User Registered Successfully!!");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(500);
                resp.setError("Failed to register user");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }



    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("LoginResponse BEGINS");
        LoginResponse response = new LoginResponse();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Fetch user details from the repository
            User userDetails = usersRepo.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Reset failed login attempts
            usersRepo.updateFailedLoginAttempts(loginRequest.getUsername(), 0);

            // Generate JWT token
            String jwtToken = jwtUtils.generateToken(userDetails);
            // Set response properties
            response.setStatusCode(200);
            response.setToken(jwtToken);
            response.setExpirationTime("12Hrs");
            response.setMessage("Logged In Successfully!!");
        } catch (AuthenticationException e) {
            //if user is trying to login without registering
            if (e.getMessage().equals("No value present")) {
                response.setStatusCode(404);
                response.setMessage("User is not registered. Please register first.");
                return  response;
            }
            // Increment failed login attempts
            User user = usersRepo.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Integer failedLoginAttempts = user.getFailedLoginAttempts() !=null ? user.getFailedLoginAttempts() + 1: 0;
            usersRepo.updateFailedLoginAttempts(loginRequest.getUsername(), failedLoginAttempts);

            // Check if account should be locked
            if (failedLoginAttempts >= 4) {
                user.setLocked(true);
                usersRepo.save(user);
                response.setStatusCode(400);
                response.setMessage("Account locked due to too many failed login attempts.");
            } else {
                response.setStatusCode(400);
                response.setLoginAttempts(failedLoginAttempts);
                response.setMessage("Invalid credentials. Attempts remaining: " + (4 - failedLoginAttempts));
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public boolean isUsernameAvailable(String username) {
        //if optional contains no value it returns true meaning new user.
        return usersRepo.findByUsername(username).isEmpty();
    }
}
