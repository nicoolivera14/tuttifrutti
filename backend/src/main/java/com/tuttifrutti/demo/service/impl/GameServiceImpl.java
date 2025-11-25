package com.tuttifrutti.demo.service.impl;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.dto.GameConfigDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.GameStatus;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.repository.GameRepository;
import com.tuttifrutti.demo.repository.PlayerRepository;
import com.tuttifrutti.demo.service.GameService;
import com.tuttifrutti.demo.service.JudgeService;
import com.tuttifrutti.demo.service.RoundService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final RoundService roundService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameServiceImpl(GameRepository gameRepository, JudgeService judgeService,
                           PlayerRepository playerRepository, RoundService roundService, SimpMessagingTemplate messagingTemplate) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.roundService = roundService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Game createGame(CreateGameRequestDTO req) {
        Game game = new Game();
        game.setName(req.getName());
        game.setName(req.getName());
        game.setRounds(req.getRounds());
        game.setTimePerRoundSeconds(req.getTimePerRoundSeconds());
        game.setCategories(req.getCategories());
        game.setStatus(GameStatus.WAITING);
        game.setCode(generateGameCode());
        game.setCurrentRoundIndex(-1);

        gameRepository.save(game);

        Player creator = new Player();
        creator.setName(req.getPlayerName());
        creator.setGame(game);
        creator.setOwner(true);
        playerRepository.save(creator);

        game.getPlayers().add(creator);

        return gameRepository.save(game);
    }

    @Override
    public Game updateGameConfig(Long gameId, Long playerId, GameConfigDTO data) {
        Game game = findById(gameId);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!player.isOwner()) {
            throw new RuntimeException("Solo el dueño puede editar la configuración");
        }

        game.setTimePerRoundSeconds(data.getTimePerRoundSeconds());
        game.setRounds(data.getRounds());
        game.setCategories(data.getCategories());

        Game saved = save(game);

        messagingTemplate.convertAndSend("/topic/games/" + gameId, saved);

        return saved;
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
    public Player joinGame(Long gameId, String playerName) {

        Game game = findById(gameId);

        Player player = new Player();
        player.setName(playerName);
        player.setGame(game);
        playerRepository.save(player);

        game.getPlayers().add(player);

        Game saved = save(game);

        messagingTemplate.convertAndSend("/topic/games/" + gameId, saved);

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

        Game saved = save(game);

        messagingTemplate.convertAndSend("/topic/games/" + saved.getId(), saved);

        return saved;
    }

    public Game nextRound (Long gameId) {
        Game game = findById(gameId);

        if (game.getStatus() == GameStatus.FINISHED) {
            throw new RuntimeException("El juego ya terminó");
        }

        long now = System.currentTimeMillis();
        long end = now + (game.getTimePerRoundSeconds() * 1000);
        game.setRoundEndTimestamp(end);

        if (game.getCurrentRoundIndex() == -1) {
            game.setCurrentRoundIndex(0);
            game.setCurrentLetter(roundService.generateRandomLetter());
            game.setStatus(GameStatus.IN_ROUND);
        } else {
            int nextIndex = game.getCurrentRoundIndex() + 1;

            if (nextIndex >= game.getRounds()) {
                game.setStatus(GameStatus.FINISHED);
            } else {
                game.setCurrentRoundIndex(nextIndex);
                game.setCurrentLetter(roundService.generateRandomLetter());
                game.setStatus(GameStatus.IN_ROUND);
            }
        }

        game.getPlayers().forEach(p -> {
            p.setFinishedTurn(false);
            playerRepository.save(p);
        });

        Game saved = save(game);

        //NOTIFICAR
        messagingTemplate.convertAndSend("/topic/games/" + gameId, saved);

        return saved;
    }

    @Override
    public Game finishTurn(Long gameId, Long playerId) {
        Game game = findById(gameId);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        player.setFinishedTurn(true);
        playerRepository.save(player);

        boolean allFinished = game.getPlayers().stream().allMatch(Player::isFinishedTurn);

        if (allFinished && game.getStatus() == GameStatus.IN_ROUND) {

            game.getPlayers().forEach(p -> {
                p.setFinishedTurn(false);
                playerRepository.save(p);
            });

            game = nextRound(gameId);
        }

        Game saved = save(game);

        messagingTemplate.convertAndSend("/topic/games/" + gameId, saved);

        return saved;
    }

    @Override
    public Game forceEndRound(Long gameId, Long playerId) {
        Game game = findById(gameId);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!player.getGame().getId().equals(gameId))
            throw new RuntimeException("El jugador no pertenece a este juego");

        game.getPlayers().forEach(p -> {
            p.setFinishedTurn(true);
            playerRepository.save(p);
        });

        Game updated = nextRound(gameId);

        messagingTemplate.convertAndSend("/topic/games/" + gameId, updated);

        return updated;
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
