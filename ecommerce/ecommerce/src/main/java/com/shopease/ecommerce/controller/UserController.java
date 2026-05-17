package com.shopease.ecommerce.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.repository.UserRepository;
import com.shopease.ecommerce.security.JwtUtil;
import com.shopease.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // User Login — FIX #1 & #2: return email, role and name so frontend can store properly
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email");
        }

        User user = optionalUser.get();

        if (userService.checkPassword(loginRequest.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid password");
        }
    }
}
