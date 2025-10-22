package com.tuttifrutti.demo.service.impl;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.GameStatus;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.repository.GameRepository;
import com.tuttifrutti.demo.repository.PlayerRepository;
import com.tuttifrutti.demo.service.GameService;
import com.tuttifrutti.demo.service.JudgeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameServiceImpl(GameRepository gameRepository, JudgeService judgeService,
                           PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public Game createGame(CreateGameRequestDTO req) {
        Game game = new Game();
        game.setCategories(req.getCategories());
        game.setRounds(req.getRounds());
        game.setTimePerRoundSeconds(req.getTimePerRoundSeconds());
        game.setStatus(GameStatus.WAITING);

        game.setCode(generateGameCode());

        gameRepository.save(game);
        return game;
    }

    @Override
    public Player joinGame(Long gameId, String playerName) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Player player = new Player();
        player.setName(playerName);
        player.setGame(game);

        // Guardamos directamente el jugador
        Player savedPlayer = playerRepository.save(player);

        // Actualizamos lista en memoria (opcional)
        game.getPlayers().add(savedPlayer);

        return savedPlayer;
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    private String generateGameCode() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(letters.charAt(rand.nextInt(letters.length())));
        }
        return sb.toString();
    }
}
