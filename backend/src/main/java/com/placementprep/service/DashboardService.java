package com.placementprep.service;

import com.placementprep.dto.DashboardDtos.*;
import com.placementprep.entity.Result;
import com.placementprep.entity.User;
import com.placementprep.exception.ApiException;
import com.placementprep.repository.ResultRepository;
import com.placementprep.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ResultRepository resultRepository;

    public DashboardService(UserRepository userRepository, ResultRepository resultRepository) {
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
    }

    public DashboardStats getStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        List<Result> results = resultRepository.findByUserOrderByDateDesc(user);

        int totalTests = results.size();
        int highestScore = results.stream().mapToInt(Result::getScore).max().orElse(0);
        double avgScore = results.stream().mapToInt(Result::getScore).average().orElse(0);
        double avgAccuracy = results.stream().mapToDouble(Result::getAccuracy).average().orElse(0);
        int questionsAttempted = results.stream().mapToInt(Result::getTotalQuestions).sum();

        List<RecentActivity> recent = results.stream()
                .limit(10)
                .map(r -> new RecentActivity(r.getCategory(), r.getScore(), r.getTotalQuestions(),
                        r.getPercentage(), r.getDate()))
                .toList();

        return new DashboardStats(user.getName(), totalTests, highestScore, avgScore, avgAccuracy,
                user.getCurrentStreak(), questionsAttempted, recent);
    }
}
