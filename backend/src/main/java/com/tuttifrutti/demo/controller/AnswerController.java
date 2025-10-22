package com.tuttifrutti.demo.controller;

import com.tuttifrutti.demo.domain.model.Answer;
import com.tuttifrutti.demo.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.tuttifrutti.demo.domain.dto.AnswerRequestDTO;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/submit")
    public Answer submitAnswer(@RequestBody AnswerRequestDTO request) {
        Answer answer = answerService.submitAnswer(
                request.getGameId(),
                request.getPlayerId(),
                request.getCategory(),
                request.getValue()
        );
        return answer;
    }
}
