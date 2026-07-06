package com.placementprep.service;

import com.placementprep.dto.QuestionDto;
import com.placementprep.dto.QuizDtos.*;
import com.placementprep.entity.Question;
import com.placementprep.entity.Result;
import com.placementprep.entity.User;
import com.placementprep.exception.ApiException;
import com.placementprep.repository.QuestionRepository;
import com.placementprep.repository.ResultRepository;
import com.placementprep.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    public QuizService(QuestionRepository questionRepository,
                        ResultRepository resultRepository,
                        UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
    }

    // Returns questions with answers stripped out, optionally filtered by difficulty, shuffled and capped.
    public List<QuestionDto> getQuizQuestions(String category, String difficulty, int limit) {
        List<Question> questions;

        if (difficulty != null && !difficulty.isBlank()) {
            Question.Difficulty diff = Question.Difficulty.valueOf(difficulty.toUpperCase());
            questions = questionRepository.findByCategoryAndDifficulty(category, diff);
        } else {
            questions = questionRepository.findByCategory(category);
        }

        if (questions.isEmpty()) {
            throw new ApiException("No questions found for category: " + category, HttpStatus.NOT_FOUND);
        }

        Collections.shuffle(questions);

        return questions.stream()
                .limit(limit > 0 ? limit : questions.size())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private QuestionDto toDto(Question q) {
        return new QuestionDto(q.getId(), q.getCategory(), q.getTopic(), q.getQuestionText(),
                q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                q.getDifficulty().name(), q.getCompany());
    }

    public ResultResponse submitQuiz(String userEmail, SubmitQuizRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Map<Long, String> answers = request.getAnswers() == null ? Map.of() : request.getAnswers();

        List<Question> questions = questionRepository.findAllById(answers.keySet());
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int correct = 0, wrong = 0, skipped = 0;
        List<QuestionReview> review = new ArrayList<>();

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Question q = questionMap.get(entry.getKey());
            if (q == null) continue;

            String selected = entry.getValue();
            boolean isCorrect = selected != null && selected.equalsIgnoreCase(q.getCorrectAnswer());

            if (selected == null || selected.isBlank()) {
                skipped++;
            } else if (isCorrect) {
                correct++;
            } else {
                wrong++;
            }

            review.add(new QuestionReview(
                    q.getId(), q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                    q.getCorrectAnswer(), selected, isCorrect, q.getExplanation()
            ));
        }

        int total = answers.size();
        double percentage = total == 0 ? 0 : (correct * 100.0) / total;
        double accuracy = (correct + wrong) == 0 ? 0 : (correct * 100.0) / (correct + wrong);

        Result result = new Result();
        result.setUser(user);
        result.setCategory(request.getCategory());
        result.setScore(correct);
        result.setTotalQuestions(total);
        result.setCorrectCount(correct);
        result.setWrongCount(wrong);
        result.setSkippedCount(skipped);
        result.setPercentage(percentage);
        result.setAccuracy(accuracy);
        result.setTimeTakenSeconds(request.getTimeTakenSeconds());

        Result saved = resultRepository.save(result);
        updateStreak(user);

        return new ResultResponse(saved.getId(), correct, total, correct, wrong, skipped,
                percentage, accuracy, request.getTimeTakenSeconds(), review);
    }

    private void updateStreak(User user) {
        var now = java.time.LocalDateTime.now();
        var last = user.getLastActivityDate();

        if (last == null || last.toLocalDate().isBefore(now.toLocalDate().minusDays(1))) {
            user.setCurrentStreak(1);
        } else if (last.toLocalDate().isEqual(now.toLocalDate().minusDays(1))) {
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        }
        // else: already practiced today, streak unchanged

        user.setLastActivityDate(now);
        userRepository.save(user);
    }
}
