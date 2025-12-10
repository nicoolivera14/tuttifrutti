package com.tuttifrutti.demo.service.judge;

import com.tuttifrutti.demo.domain.model.JudgeResult;
import org.springframework.stereotype.Component;

@Component("basicJudge")
public class BasicValidationStrategy implements AnswerJudgeStrategy {

    @Override
    public JudgeResult evaluate(String word, char expectedLetter, String category) {
        if (word == null || word.isBlank()) {
            return new JudgeResult(false, 0, "Vacío");
        }

        word = word.trim();

        if (!word.toUpperCase().startsWith(String.valueOf(expectedLetter).toUpperCase())) {
            return new JudgeResult(false, 0, "No empieza con la letra correcta");
        }

        if (word.length() < 2) {
            return new JudgeResult(false, 0, "Muy corto");
        }

        return new JudgeResult(true, 100, "OK (validación mínima)");
    }
}

