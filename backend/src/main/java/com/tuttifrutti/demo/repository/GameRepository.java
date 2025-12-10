package com.tuttifrutti.demo.repository;

import com.tuttifrutti.demo.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByCode(String code);
    List<Game> findByCodeContainingIgnoreCase(String code);
}



