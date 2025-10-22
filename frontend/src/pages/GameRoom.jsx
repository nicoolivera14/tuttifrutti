// src/pages/GameRoom.jsx
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getAllGames, startRound, endRound } from "../api/gameApi.js";

export default function GameRoom() {
  const { gameId } = useParams();
  const [game, setGame] = useState(null);

  useEffect(() => {
    loadGame();
  }, []);

  const loadGame = async () => {
    const res = await getAllGames();
    const currentGame = res.find((g) => g.id === parseInt(gameId));
    setGame(currentGame);
  };

  const handleStart = async () => {
    await startRound(gameId);
    loadGame();
  };

  const handleEnd = async () => {
    await endRound(gameId);
    loadGame();
  };

  if (!game) return <div>Cargando...</div>;

  return (
    <div>
      <h1>Game {game.code}</h1>
      <p>Status: {game.status}</p>
      <p>Current Letter: {game.currentLetter || "-"}</p>
      <p>
        Round: {game.currentRoundIndex + 1} / {game.rounds}
      </p>

      <h3>Players:</h3>
      <ul>
        {game.players.map((p) => (
          <li key={p.id}>{p.name}</li>
        ))}
      </ul>

      <button onClick={handleStart} disabled={game.status !== "WAITING"}>
        Start Round
      </button>
      <button onClick={handleEnd} disabled={game.status !== "IN_ROUND"}>
        End Round
      </button>
    </div>
  );
}
