package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // e.g. Quantitative Aptitude, Java, DBMS...

    private String topic;

    @Column(nullable = false, length = 1000)
    private String questionText;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;

    // Stores "A", "B", "C" or "D"
    @Column(nullable = false, length = 1)
    private String correctAnswer;

    @Column(length = 2000)
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    private String company; // Company tag e.g. Zoho, Freshworks, TCS

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
