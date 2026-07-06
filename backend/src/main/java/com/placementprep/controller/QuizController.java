package com.placementprep.controller;

import com.placementprep.dto.QuestionDto;
import com.placementprep.dto.QuizDtos.*;
import com.placementprep.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // GET /api/quiz/questions?category=Java&difficulty=MEDIUM&limit=10
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDto>> getQuestions(
            @RequestParam String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getQuizQuestions(category, difficulty, limit));
    }

    @PostMapping("/submit")
    public ResponseEntity<ResultResponse> submitQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SubmitQuizRequest request) {
        return ResponseEntity.ok(quizService.submitQuiz(userDetails.getUsername(), request));
    }
}
