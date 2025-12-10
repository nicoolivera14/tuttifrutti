package com.tuttifrutti.demo.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JudgeResult {
    private boolean valid;
    private int score;
    private String reason;
}
