package com.placementprep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String category;

    private int score;

    private int totalQuestions;

    private int correctCount;

    private int wrongCount;

    private int skippedCount;

    private double percentage;

    private double accuracy;

    // Time taken in seconds
    private long timeTakenSeconds;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();
}
