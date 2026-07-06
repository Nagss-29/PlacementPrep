package com.placementprep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class QuizDtos {

    // Sent by the frontend when the user submits a quiz.
    // answers maps questionId -> selected option ("A"/"B"/"C"/"D"), or null if skipped.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmitQuizRequest {
        private String category;
        private Map<Long, String> answers;
        private long timeTakenSeconds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionReview {
        private Long questionId;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;
        private String selectedAnswer;
        private boolean correct;
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultResponse {
        private Long resultId;
        private int score;
        private int totalQuestions;
        private int correctCount;
        private int wrongCount;
        private int skippedCount;
        private double percentage;
        private double accuracy;
        private long timeTakenSeconds;
        private List<QuestionReview> review;
    }
}
