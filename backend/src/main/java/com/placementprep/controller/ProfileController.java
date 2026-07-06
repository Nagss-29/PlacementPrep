package com.placementprep.controller;

import com.placementprep.dto.DashboardDtos.ChangePasswordRequest;
import com.placementprep.dto.DashboardDtos.ProfileUpdateRequest;
import com.placementprep.entity.User;
import com.placementprep.exception.ApiException;
import com.placementprep.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User currentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        user.setPassword(null); // never return the hash
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody ProfileUpdateRequest request) {
        User user = currentUser(userDetails);
        if (request.getName() != null) user.setName(request.getName());
        if (request.getCollege() != null) user.setCollege(request.getCollege());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getYear() != null) user.setYear(request.getYear());

        User saved = userRepository.save(user);
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody ChangePasswordRequest request) {
        User user = currentUser(userDetails);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ApiException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
