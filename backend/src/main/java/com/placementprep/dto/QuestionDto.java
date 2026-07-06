package com.placementprep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Question shown to the user during a quiz - correctAnswer and explanation are hidden
// until the quiz is submitted, to prevent cheating via the network tab.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long id;
    private String category;
    private String topic;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String difficulty;
    private String company;
}
