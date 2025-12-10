package com.tuttifrutti.demo.service;

import com.tuttifrutti.demo.domain.model.Answer;
import com.tuttifrutti.demo.domain.model.JudgeResult;
import com.tuttifrutti.demo.domain.model.Player;
import com.tuttifrutti.demo.domain.model.Game;
import com.tuttifrutti.demo.repository.AnswerRepository;
import com.tuttifrutti.demo.repository.GameRepository;
import com.tuttifrutti.demo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final JudgeService judgeService;

    public Answer submitAnswer(Long gameId, Long playerId, String category, String value) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada."));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado."));

        long now = System.currentTimeMillis();
        if (now > game.getRoundEndTimestamp()) {
            throw new RuntimeException("El tiempo de la ronda ya terminó");
        }

        if (player.isFinishedTurn()) {
            throw new RuntimeException("Ya terminaste tu turno esta ronda");
        }

        String required = String.valueOf(game.getCurrentLetter()).toUpperCase();

        if (value == null) value = "";
        value = value.trim();

        String provided = value.toUpperCase();

        if(!provided.startsWith(required)) {
            throw new RuntimeException("La palabra no comienza con la letra correcta");
        }

        Answer answer = answerRepository
                .findByPlayerAndCategoryAndGameAndRoundNumber(player, category, game, game.getCurrentRoundIndex())
                .orElse(new Answer());

        answer.setCategory(category);
        answer.setPlayer(player);
        answer.setGame(game);
        answer.setRoundNumber(game.getCurrentRoundIndex());

        boolean previusValid = Boolean.TRUE.equals(answer.getValid());

        JudgeResult result = judgeService.judge(value, game.getCurrentLetter(), category);
        boolean isValid = result.isValid();

        answer.setValue(value);
        answer.setValid(isValid);

        answerRepository.save(answer);

        if (!previusValid && isValid) {
            player.setTotalScore(player.getTotalScore() + 10);
            playerRepository.save(player);
        }

        return answer;
    }
}
