package com.tuttifrutti.demo.controller;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.dto.GameConfigDTO;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.service.GameService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    //CREAR JUEGO
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody CreateGameRequestDTO req) {
        return ResponseEntity.ok(gameService.createGame(req));
    }

    //ACTUALIZAR CONFIG
    @PutMapping("/{id}/config")
    public ResponseEntity<Game> updateGameConfig(@PathVariable("id") Long id, @RequestParam("playerId") Long playerId,
                                                 @RequestBody GameConfigDTO data) {
        Game game = gameService.updateGameConfig(id, playerId, data);
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
        return ResponseEntity.ok(gameService.joinGameByCode(gameCode, playerName));
    }


    //LISTAR JUEGOS
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    //SIGUIENTE RONDA
    @PostMapping("/{gameId}/next-round")
    public ResponseEntity<Game> nextRound(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(gameService.nextRound(gameId));
    }

    //TERMINAR TURNO
    @PostMapping("/{gameId}/finish-turn")
    public ResponseEntity<Game> finishTurn(
            @PathVariable("gameId") Long gameId,
            @RequestParam("playerId") Long playerId,
            @RequestParam(name= "surrender", required = false, defaultValue = "false") boolean surrender) {

        return ResponseEntity.ok(gameService.finishTurn(gameId, playerId));
    }

    @PostMapping("/{gameId}/force-end-round")
    public ResponseEntity<Game> forceEndRound(
            @PathVariable("gameId") Long gameId,
            @RequestParam("playerId") Long playerId) {

        Game updated = gameService.forceEndRound(gameId, playerId);
        return ResponseEntity.ok(updated);
    }

    //Validación a futuro sobre nextRound para que solo se llame cuando todos los jugadores terminen su turno
}
