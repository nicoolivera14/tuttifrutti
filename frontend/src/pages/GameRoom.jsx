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
    try {
      const games = await getAllGames();
      const found = games.find((g) => g.id === parseInt(gameId));
      setGame(found);
    } catch (err) {
      console.error("Error al cargar juego:", err);
    }
  };

  if (!game) return <p>Cargando...</p>;

  return (
    <div style={{ padding: "2rem"}}>
      <h2>Game Room</h2>
      <p>ID: {game.id}</p>
      <p>Código: {game.code}</p>
      <p>Estado: {game.status}</p>

      <h3>Jugadores</h3>
      <ul>
        {game.players.map((p) => (
          <li key={p.id}>{p.name}</li>
        ))}
      </ul>

      <button onClick={() => startRound(game.id)}>Iniciar ronda</button>
      <button onClick={() => endRound(game.id)}>Finalizar ronda</button>
    </div>
  );
}