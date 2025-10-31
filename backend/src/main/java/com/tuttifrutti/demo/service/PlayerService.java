package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player registerPlayer(Player player) {
        if (player.getEmail() == null || player.getPassword() == null) {
            throw new RuntimeException("Email y contraseña son obligatorios.");
        }
        if (playerRepository.findByEmail(player.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado.");
        }
        return playerRepository.save(player);
    }

    public Optional<Player> login(String email, String password) {
        return playerRepository.findByEmail(email)
                .filter(player -> player.getPassword().equals(password));
    }

    public Optional<Player> getById(Long id) {
        return playerRepository.findById(id);
    }
}
