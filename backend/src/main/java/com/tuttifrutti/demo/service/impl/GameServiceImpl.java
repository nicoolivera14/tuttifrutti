package com.tuttifrutti.demo.service.impl;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.GameStatus;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.repository.GameRepository;
import com.tuttifrutti.demo.repository.PlayerRepository;
import com.tuttifrutti.demo.service.GameService;
import com.tuttifrutti.demo.service.JudgeService;
import com.tuttifrutti.demo.service.RoundService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final RoundService roundService;

    public GameServiceImpl(GameRepository gameRepository, JudgeService judgeService,
                           PlayerRepository playerRepository, RoundService roundService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.roundService = roundService;
    }

    @Override
    public Game createGame(CreateGameRequestDTO req) {
        Game game = new Game();
        game.setName(req.getName());
        game.setRounds(req.getRounds());
        game.setTimePerRoundSeconds(req.getTimePerRoundSeconds());
        game.setCategories(req.getCategories());
        game.setStatus(GameStatus.WAITING);
        game.setCode(generateGameCode());
        game.setCurrentRoundIndex(-1);
        //game.setCurrentLetter(roundService.generateRandomLetter());

        gameRepository.save(game);

        Player creator = new Player();
        creator.setName(req.getPlayerName());
        creator.setGame(game);
        playerRepository.save(creator);

        game.getPlayers().add(creator);

        return gameRepository.save(game);
    }

    @Override
    public Game findById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
    }

    @Override
    public Game save(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Player joinGame(Long gameCode, String playerName) {
        Game game = gameRepository.findById(gameCode)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Player player = new Player();
        player.setName(playerName);
        player.setGame(game);
        playerRepository.save(player);

        game.getPlayers().add(player);

        return player;
    }

    @Override
    public Game joinGameByCode(String gameCode, String playerName) {
        Game game = gameRepository.findByCode(gameCode)
                    .orElseThrow(() -> new RuntimeException("Juego no encontrado con el código: " + gameCode));

        Player player = new Player();
        player.setName(playerName);
        player.setGame(game);
        playerRepository.save(player);

        game.getPlayers().add(player);

        return gameRepository.save(game);
    }

    public Game nextRound (Long gameId) {
        Game game = findById(gameId);

        System.out.println("➡️ nextRound() llamado | BEFORE | gameId=" + gameId +
                " | roundIndex=" + game.getCurrentRoundIndex() +
                " | status=" + game.getStatus());

        if (game.getStatus() == GameStatus.FINISHED) {
            throw new RuntimeException("El juego ya terminó");
        }


        if (game.getCurrentRoundIndex() == -1) {
            game.setCurrentRoundIndex(0);
            game.setCurrentLetter(roundService.generateRandomLetter());
            game.setStatus(GameStatus.IN_ROUND);

            System.out.println("✔️ nextRound() AFTER primera ronda | roundIndex=" + game.getCurrentRoundIndex() +
                    " | status=" + game.getStatus());

            return gameRepository.save(game);
        }

        int nextIndex = game.getCurrentRoundIndex() + 1;

        if (nextIndex >= game.getRounds()) {
            game.setStatus(GameStatus.FINISHED);
            System.out.println("🏁 Juego FINALIZADO | AFTER | roundIndex=" + game.getCurrentRoundIndex());

            return gameRepository.save(game);
        }

        game.setCurrentRoundIndex(nextIndex);
        game.setCurrentLetter(roundService.generateRandomLetter());
        game.setStatus(GameStatus.IN_ROUND);

        System.out.println("➡️ nextRound() AFTER avanzó ronda | roundIndex=" + game.getCurrentRoundIndex() +
                " | status=" + game.getStatus());

        return gameRepository.save(game);
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
