package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.domain.model.Answer;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.repository.AnswerRepository;
import com.tuttifrutti.demo.repository.GameRepository;
import com.tuttifrutti.demo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    public Answer submitAnswer(Long gameId, Long playerId, String category, String value) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada."));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado."));

        Answer answer = Answer.builder()
                .category(category)
                .value(value)
                .player(playerRepository.findById(playerId).get())
                .game(gameRepository.findById(gameId).get())
                .valid(false)
                .build();

        return answerRepository.save(answer);
    }
}
