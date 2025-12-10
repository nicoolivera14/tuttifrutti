package com.tuttifrutti.demo.service.judge;

import com.tuttifrutti.demo.domain.model.JudgeResult;
import org.springframework.stereotype.Component;

@Component("noJudge")
public class NoValidationStrategy implements AnswerJudgeStrategy {

    @Override
    public JudgeResult evaluate(String word, char letter, String category) {
        return new JudgeResult(true, 100, "Sin validación");
    }
}

