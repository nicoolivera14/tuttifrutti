package com.tuttifrutti.demo.controller;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.service.GameService;
import com.tuttifrutti.demo.service.RoundService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final RoundService roundService;

    public GameController(GameService gameService, RoundService roundService) {
        this.gameService = gameService;
        this.roundService = roundService;
    }

    //CREAR JUEGO
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody CreateGameRequestDTO req) {
        Game game = gameService.createGame(req);
        return ResponseEntity.ok(game);
    }

    @PutMapping("/{id}/config")
    public ResponseEntity<Game> updateGameConfig(@PathVariable Long id, @RequestBody Game updatedData) {
        Game game = gameService.findById(id);
        game.setTimePerRoundSeconds(updatedData.getTimePerRoundSeconds());
        game.setRounds(updatedData.getRounds());
        game.setCategories(updatedData.getCategories());
        return ResponseEntity.ok(gameService.save(game));
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
        return ResponseEntity.ok(games);
    }

    //INICIAR RONDA
    @PostMapping("/{gameId}/start-round")
    public ResponseEntity<Game> startRound(@PathVariable("gameId") Long gameId) {
        Game game = roundService.startNewRound(gameId);
        return ResponseEntity.ok(game);
    }

    //FINALIZAR RONDA
    @PostMapping("/{gameId}/end-round")
    public ResponseEntity<Game> endRound(@PathVariable("gameId") Long gameId) {
        Game updatedGame = roundService.endRound(gameId);
        return ResponseEntity.ok(updatedGame);
    }
}
