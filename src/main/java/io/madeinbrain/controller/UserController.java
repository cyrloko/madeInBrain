package io.madeinbrain.controller;

import io.madeinbrain.entity.User;
import io.madeinbrain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");

            if (username == null || email == null || password == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required fields");
                return ResponseEntity.badRequest().body(error);
            }

            User user = userService.createUser(username, email, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("username", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Missing username or password");
            return ResponseEntity.badRequest().body(error);
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null || !userService.validatePassword(user, password)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "reputation", user.getReputation(),
                "role", user.getRole(),
                "isExpert", user.getIsExpert()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("reputation", user.getReputation());
        profile.put("role", user.getRole());
        profile.put("isExpert", user.getIsExpert());
        profile.put("expertDomains", user.getExpertDomains());
        profile.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/experts")
    public ResponseEntity<List<User>> getExperts() {
        List<User> experts = userService.getExperts();
        return ResponseEntity.ok(experts);
    }

    @GetMapping("/top")
    public ResponseEntity<List<User>> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        List<User> topUsers = userService.getTopUsersByReputation(limit);
        return ResponseEntity.ok(topUsers);
    }

    @PostMapping("/{userId}/promote-expert")
    public ResponseEntity<Map<String, String>> promoteToExpert(@PathVariable Long userId,
                                                               @RequestBody Map<String, String> request,
                                                               Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String expertDomains = request.get("expertDomains");
            User promotedUser = userService.promoteToExpert(userId, expertDomains);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User promoted to expert successfully");
            response.put("username", promotedUser.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}