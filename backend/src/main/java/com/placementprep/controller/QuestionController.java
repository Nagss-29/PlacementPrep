package com.placementprep.controller;

import com.placementprep.entity.Question;
import com.placementprep.exception.ApiException;
import com.placementprep.repository.QuestionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // ===========================
    // PUBLIC ENDPOINTS
    // ===========================

    // Get all questions (Public)
    @GetMapping("/api/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    // Get all categories (Public)
    @GetMapping("/api/questions/public/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = questionRepository.findAll().stream()
                .map(Question::getCategory)
                .distinct()
                .toList();

        return ResponseEntity.ok(categories);
    }

    // Search questions (Public)
    @GetMapping("/api/questions/search")
    public ResponseEntity<List<Question>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(
                questionRepository.findByCategoryContainingIgnoreCaseOrTopicContainingIgnoreCase(keyword, keyword)
        );
    }

    // ===========================
    // ADMIN ENDPOINTS
    // ===========================

    @GetMapping("/api/admin/questions")
    public ResponseEntity<List<Question>> getAll() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @PostMapping("/api/admin/questions")
    public ResponseEntity<Question> create(@Valid @RequestBody Question question) {
        return ResponseEntity.ok(questionRepository.save(question));
    }

    @PutMapping("/api/admin/questions/{id}")
    public ResponseEntity<Question> update(@PathVariable Long id,
                                           @Valid @RequestBody Question updated) {

        Question existing = questionRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException("Question not found", HttpStatus.NOT_FOUND));

        existing.setCategory(updated.getCategory());
        existing.setTopic(updated.getTopic());
        existing.setQuestionText(updated.getQuestionText());
        existing.setOptionA(updated.getOptionA());
        existing.setOptionB(updated.getOptionB());
        existing.setOptionC(updated.getOptionC());
        existing.setOptionD(updated.getOptionD());
        existing.setCorrectAnswer(updated.getCorrectAnswer());
        existing.setExplanation(updated.getExplanation());
        existing.setDifficulty(updated.getDifficulty());
        existing.setCompany(updated.getCompany());

        return ResponseEntity.ok(questionRepository.save(existing));
    }

    @DeleteMapping("/api/admin/questions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}