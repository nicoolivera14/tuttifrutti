package com.tuttifrutti.demo.controller;

import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "http://localhost:5174") // puerto frontend
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody Player player) {
        try {
            Player saved = playerService.registerPlayer(player);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        return playerService.login(email, password)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas")));
    }
}
