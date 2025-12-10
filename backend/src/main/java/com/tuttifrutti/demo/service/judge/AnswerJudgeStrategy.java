package com.tuttifrutti.demo.service.judge;

import com.tuttifrutti.demo.domain.model.JudgeResult;

public interface AnswerJudgeStrategy {
    JudgeResult evaluate(String word, char expectedLetter, String category);
}

