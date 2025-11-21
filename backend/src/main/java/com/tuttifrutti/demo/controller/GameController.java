package com.tuttifrutti.demo.controller;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.dto.GameConfigDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.GameStatus;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.repository.PlayerRepository;
import com.tuttifrutti.demo.service.GameService;
import com.tuttifrutti.demo.service.RoundService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final RoundService roundService;

    public GameController(GameService gameService,
                          PlayerRepository playerRepository, RoundService roundService) {
        this.gameService = gameService;
        this.playerRepository = playerRepository;
        this.roundService = roundService;
    }

    //CREAR JUEGO
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody CreateGameRequestDTO req) {
        Game game = gameService.createGame(req);
        return ResponseEntity.ok(game);
    }

    @PutMapping("/{id}/config")
    public ResponseEntity<Game> updateGameConfig(@PathVariable("id") Long id, @RequestBody GameConfigDTO data) {

        Game game = gameService.findById(id);
        game.setTimePerRoundSeconds(data.getTimePerRoundSeconds());
        game.setRounds(data.getRounds());
        game.setCategories(data.getCategories());
        return ResponseEntity.ok(gameService.save(game));
    }

    //JUEGO POR ID(Funcionamiento del GamePlay)
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(gameService.findById(id));
    }

    //UNIRSE A JUEGO
    @PostMapping("/join-by-code")
    public ResponseEntity<Game> joinGameByCode(
            @RequestParam("gameCode") String gameCode,
            @RequestParam("playerName") String playerName) {
        Game updatedGame = gameService.joinGameByCode(gameCode, playerName);
        return ResponseEntity.ok(updatedGame);
    }


    //LISTAR JUEGOS
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        System.out.println("🔎 Cantidad de juegos en DB: " + games.size());
        return ResponseEntity.ok(games);
    }

    //SIGUIENTE RONDA
    @PostMapping("/{gameId}/next-round")
    public ResponseEntity<Game> nextRound(@PathVariable("gameId") Long gameId) {
        Game updatedGame = gameService.nextRound(gameId);
        System.out.println("FINISH TURN → AFTER nextRound: index=" + updatedGame.getCurrentRoundIndex());

        return ResponseEntity.ok(updatedGame);
    }

    //TERMINAR TURNO
    @PostMapping("/{gameId}/finish-turn")
    public ResponseEntity<Game> finishTurn(
            @PathVariable("gameId") Long gameId,
            @RequestParam("playerId") Long playerId,
            @RequestParam(name= "surrender", required = false, defaultValue = "false") boolean surrender) {

         Game game = gameService.findById(gameId);
         Player player = playerRepository.findById(playerId)
                 .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

         player.setFinishedTurn(true);
         playerRepository.save(player);

         boolean allFinished = game.getPlayers().stream().allMatch(Player::isFinishedTurn);

         if (allFinished && game.getStatus() == GameStatus.IN_ROUND) {

             game.getPlayers().forEach(p -> p.setFinishedTurn(false));

             game = gameService.nextRound(gameId);

         }
        return ResponseEntity.ok(gameService.save(game));
    }

    //Validación a futuro sobre nextRound para que solo se llame cuando todos los jugadores terminen su turno
}
