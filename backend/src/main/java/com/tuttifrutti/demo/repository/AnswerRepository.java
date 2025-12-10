package com.tuttifrutti.demo.repository;

import com.tuttifrutti.demo.domain.model.Answer;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByPlayerAndCategoryAndGameAndRoundNumber(Player player, String category, Game game, Integer roundNumber);
}

