package com.tuttifrutti.demo.controller;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.service.GameService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    public GameController(GameService gameService) { this.gameService = gameService; }

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
}
