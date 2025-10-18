package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.GameStatus;
import com.tuttifrutti.demo.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class RoundService {

    private final GameRepository gameRepository;
    private final Random random = new Random();

    public RoundService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game startNewRound(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new RuntimeException("Juego no encontrado con ID: " + gameId);
        }

        Game game = optionalGame.get();

        if ("FINISHED".equals(game.getStatus())) {
            throw new RuntimeException("El juego ya ha finalizado.");
        }

        //GENERACIÓN DE LETRA ALEATORIA
        char randomLetter = (char) ('A' + random.nextInt(26));
        game.setCurrentLetter(randomLetter);

        game.setStatus(GameStatus.IN_ROUND);

        return gameRepository.save(game);
    }

    //FINAL DE RONDA. JUEGO TERMINADO SI ES LA ÚLTIMA RONDA
    public Game endRound(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new RuntimeException("Juego no encontrado con ID: " + gameId);
        }

        Game game = optionalGame.get();

        if (!com.tuttifrutti.demo.domain.model.GameStatus.IN_ROUND.equals(game.getStatus())) {
            throw new RuntimeException("No se puede finalizar una ronda que no está en curso.");
        }

        int nextRound = game.getCurrentRoundIndex() + 1;
        game.setCurrentRoundIndex(nextRound);

        if (nextRound >= game.getRounds()) {
            game.setStatus(com.tuttifrutti.demo.domain.model.GameStatus.FINISHED);
        } else {
            game.setStatus(com.tuttifrutti.demo.domain.model.GameStatus.WAITING);
        }

        return gameRepository.save(game);
    }
}
