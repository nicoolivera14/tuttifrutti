import axios from "axios";

export async function submitAnswer(gameId, playerId, category, value) {
    const res = await axios.post("http://localhost:8080/api/answers/submit", {
        gameId,
        playerId,
        category,
        value,
    });

    return res.data;
}

export async function finishTurn(gameId, playerId) {
    const res = await axios.post(
        `http://localhost:8080/games/${gameId}/finish-turn?playerId=${playerId}`
    );
    return res.data;
}