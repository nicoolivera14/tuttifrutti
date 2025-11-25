import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getAllGames, startRound, endRound } from "../api/gameApi.js";
import axios from "axios";
import { createStompClient } from "../websocketClient.js";


export default function GameRoom() {
  const { gameId } = useParams();
  const navigate = useNavigate();

  const [game, setGame] = useState(null);
  const [timePerRound, setTimePerRound] = useState(60);
  const [rounds, setRounds] = useState(3);
  const [categoriesInput, setCategoriesInput] = useState("");
  const [isOwner, setIsOwner] = useState(false);
  const [playerId, setPlayerId] = useState(null);

  useEffect(() => {
    loadGame();
  }, []);

  useEffect(() => {
    const client = createStompClient(() => {
      client.subscribe(`/topic/games/${gameId}`, (message) => {
        const updated = JSON.parse(message.body);

        setGame(updated);

        if (updated.status === "IN_ROUND") {
          navigate(`/game/${updated.id}/play`);
        }
      });
    });

    return () => client.deactivate();
  }, []);

  const loadGame = async () => {
    try {
      const games = await getAllGames();

      const found = games.find((g) => g.id === parseInt(gameId));

      if (!found) return;
     
      setGame(found);

      const sessionPlayer = JSON.parse(sessionStorage.getItem("player"));
      const playerName = sessionPlayer?.name;

      const matchedPlayer = found.players.find(p => p.name === playerName);

      if (matchedPlayer) {
        sessionStorage.setItem("player", JSON.stringify({
          id: matchedPlayer.id,
          name: matchedPlayer.name,
          owner: matchedPlayer.owner
        }));
      }

      const updatedPlayer = JSON.parse(sessionStorage.getItem("player"));
      setPlayerId(updatedPlayer?.id);

      setTimePerRound(found.timePerRoundSeconds || 60);
      setRounds(found.rounds || 3);
      setCategoriesInput(found.categories?.join(", ") || "");
      
      //VERIFICAR SI ES OWNER
      const owner = found.players.find((p) => p.owner === true);
      setIsOwner(owner?.id === updatedPlayer?.id);


    } catch (err) {
      console.error("Error al cargar juego:", err);
    }
  };

  if (!game) return <p>Cargando...</p>;

  const handleStartGame = async () => {
    try {
      const res = await axios.post(`http://localhost:8080/games/${game.id}/next-round`);
      console.log("Partida iniciada:", res.data);
    } catch (err) {
      console.error("Error al iniciar partida:", err);
    }
  };

  const handleSaveConfig = async () => {
    try {
      const updated = await axios.put(`http://localhost:8080/games/${game.id}/config?playerId=${playerId}`, 
        {
          timePerRoundSeconds: parseInt(timePerRound),
          rounds: parseInt(rounds),
          categories: categoriesInput
            .split(",")
            .map(c => c.trim())
            .filter(Boolean),
        }
      );

      setGame(updated.data);
      alert("Configuración guardada correctamente.");
      await loadGame();
    } catch (error) {
      console.error("Error al guardar configuración:", error);
      alert("Error al guardar la configuración.");
    }
  }

  return (
    <div style={{ padding: "2rem"}}>
      <h2>Game Room</h2>
      <p>ID: {game.id}</p>
      <p>Código: {game.code}</p>
      <p>Estado: {game.status}</p>

      <h3>Jugadores</h3>
      <ul>
        {game.players.map((p) => (
          <li key={p.id}>{p.name} {p.owner && "(Dueño)"}</li>
        ))}
      </ul>
      
      {game.status === "WAITING" && (
        <div style={{ marginTop: "2rem", border: "1px solid #ccc", padding: "1rem", borderRadius: "10px"}}>
          <h3>Configuración de la sala</h3>

          <div>
            <label>Tiempo por ronda (segundos): </label>
            <input 
              type="number" 
              min="10" 
              max="180" 
              value={timePerRound} 
              onChange={(e) => setTimePerRound(e.target.value)}
              disabled={!isOwner} />
          </div>

          <div>
            <label>Cantidad de rondas: </label>
            <input 
              type="number" 
              min="1" 
              max="10" 
              value={rounds} 
              onChange={(e) => setRounds(e.target.value)}
              disabled={!isOwner} />
          </div>

          <div>
            <label>Categorías (separadas por comas): </label>
            <input 
              type="text" 
              value={categoriesInput} 
              onChange={(e) => setCategoriesInput(e.target.value)} 
              disabled={!isOwner} />
          </div>

          {isOwner && (<button onClick={handleSaveConfig}>Guardar configuración</button> )}
        </div>
      )}

      <button onClick={() => navigate(`/home`)}>Ver salas</button>
      {isOwner && game.status === "WAITING" && ( <button onClick={handleStartGame}>Iniciar partida</button>)}
    </div>
  );
}