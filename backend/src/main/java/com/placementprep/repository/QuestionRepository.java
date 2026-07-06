package com.placementprep.repository;

import com.placementprep.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategory(String category);
    List<Question> findByCategoryAndDifficulty(String category, Question.Difficulty difficulty);
    List<Question> findByCompany(String company);
    List<Question> findByCategoryContainingIgnoreCaseOrTopicContainingIgnoreCase(String category, String topic);
}
