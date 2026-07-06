package com.placementprep.controller;

import com.placementprep.entity.Result;
import com.placementprep.entity.User;
import com.placementprep.exception.ApiException;
import com.placementprep.repository.ResultRepository;
import com.placementprep.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ResultController {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    public ResultController(ResultRepository resultRepository, UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/results/my")
    public ResponseEntity<List<Result>> myResults(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(resultRepository.findByUserOrderByDateDesc(user));
    }

    // Simple global leaderboard: best score per user, ranked descending.
    @GetMapping("/api/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> leaderboard() {
        List<Result> topResults = resultRepository.findTop50ByOrderByScoreDesc();

        Map<String, Integer> bestPerUser = topResults.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUser().getName(),
                        Collectors.collectingAndThen(
                                Collectors.reducing((r1, r2) -> r1.getScore() >= r2.getScore() ? r1 : r2),
                                r -> r.map(Result::getScore).orElse(0)
                        )
                ));

        List<Map<String, Object>> leaderboard = bestPerUser.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> Map.<String, Object>of("name", e.getKey(), "highestScore", e.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderboard);
    }
}
