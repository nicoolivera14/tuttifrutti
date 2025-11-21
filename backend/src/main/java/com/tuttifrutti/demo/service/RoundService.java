package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RoundService {

    private final GameRepository gameRepository;
    private final Random random = new Random();

    public RoundService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public char generateRandomLetter() {
        return (char) ('A' + random.nextInt(26));
    }
}
