package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.domain.model.JudgeResult;
import com.tuttifrutti.demo.service.judge.AnswerJudgeStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JudgeService {
    private final AnswerJudgeStrategy strategy;

    public JudgeService(@Qualifier("aiJudge") AnswerJudgeStrategy strategy) {
        this.strategy = strategy;
    }
    public JudgeResult judge(String word, char letter, String category) {
        return strategy.evaluate(word, letter, category);
    }
}

