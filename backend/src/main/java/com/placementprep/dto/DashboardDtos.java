package com.placementprep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStats {
        private String userName;
        private int totalTests;
        private int highestScore;
        private double averageScore;
        private double accuracy;
        private int currentStreak;
        private int questionsAttempted;
        private List<RecentActivity> recentActivities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String category;
        private int score;
        private int totalQuestions;
        private double percentage;
        private LocalDateTime date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUpdateRequest {
        private String name;
        private String college;
        private String department;
        private String year;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
}
