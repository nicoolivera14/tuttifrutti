import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getAllGames, startRound, endRound } from "../api/gameApi.js";
import { useNavigate } from "react-router-dom";
import axios from "axios";


export default function GameRoom() {
  const { gameId } = useParams();
  const [game, setGame] = useState(null);
  const [timePerRound, setTimePerRound] = useState(60);
  const [rounds, setRounds] = useState(3);
  const [categoriesInput, setCategoriesInput] = useState("");
  const navigate = useNavigate();
  
  useEffect(() => {
    loadGame();
  }, []);

  const loadGame = async () => {
    try {
      const games = await getAllGames();
      console.log("Todas las salas:", games);
      console.log("ID buscado:", gameId);

      const found = games.find((g) => g.id === parseInt(gameId));
      console.log("Juego encontrado:", found)

      if (found) {
        setGame(found);
        setTimePerRound(found.timePerRoundSeconds || 60);
        setRounds(found.rounds || 3);
        setCategoriesInput(found.categories?.join(", ") || "");
      }
    } catch (err) {
      console.error("Error al cargar juego:", err);
    }
  };

  if (!game) return <p>Cargando...</p>;

  const handleSaveConfig = async () => {
    try {
      const updated = await axios.put(`http://localhost:8080/games/${game.id}/config`, {
          timePerRoundSeconds: parseInt(timePerRound),
          rounds: parseInt(rounds),
          categories: categoriesInput.split(",").map(c => c.trim()).filter(Boolean)
        });

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
          <li key={p.id}>{p.name}</li>
        ))}
      </ul>
      
      {game.status === "WAITING" && (
        <div style={{ marginTop: "2rem", border: "1px solid #ccc", padding: "1rem", borderRadius: "10px"}}>
          <h3>Configuración de la sala</h3>

          <div>
            <label>Tiempo por ronda (segundos): </label>
            <input type="number" min="10" max="180" value={timePerRound} onChange={(e) => setTimePerRound(e.target.value)} />
          </div>

          <div>
            <label>Cantidad de rondas: </label>
            <input type="number" min="1" max="10" value={rounds} onChange={(e) => setRounds(e.target.value)}/>
          </div>

          <div>
            <label>Categorías (separadas por comas): </label>
            <input type="text" value={categoriesInput} onChange={(e => setCategoriesInput(e.target.value))}/>
          </div>

          <button onClick={handleSaveConfig}>Guardar configuración</button>
        </div>
      )}

      <button onClick={() => navigate(`/home`)}>Ver salas</button>
      <button onClick={() => navigate(`/game/${game.id}/play`)}>Ir a la partida</button>
    </div>
  );
}