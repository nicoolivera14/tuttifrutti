package com.tuttifrutti.demo.repository;

import com.tuttifrutti.demo.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> { }
