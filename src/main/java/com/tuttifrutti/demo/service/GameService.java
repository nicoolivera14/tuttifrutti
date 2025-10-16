package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.domain.dto.CreateGameRequestDTO;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.domain.model.Game;

import java.util.List;

public interface GameService {
    Game createGame(CreateGameRequestDTO req);
    Player joinGame(Long gameId, String playerName);
    List<Game> getAllGames();
}
