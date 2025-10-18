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

    //UNIRSE A JUEGO
    @PostMapping("/{gameId}/join")
    public Player joinGame(
            @PathVariable("gameId") Long gameId,
            @RequestParam("playerName") String playerName) {
        return gameService.joinGame(gameId, playerName);
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
