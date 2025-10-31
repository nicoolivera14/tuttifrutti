import React, { useEffect, useState } from "react";
import { getAllGames, createGame, joinGame } from "../api/gameApi";
import { useNavigate } from "react-router-dom";

export default function Home() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(false);
  const [code, setCode] = useState("");
  const [playerName, setPlayerName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    loadGames();
  }, []);

  const loadGames = async () => {
    setLoading(true);
    try {
      const data = await getAllGames();
      setGames(data);
    } catch (error) {
      console.error("Error cargando juegos:", error);
    }
    setLoading(false);
  };

  const handleJoin = async (gameId) => {
    const name = playerName || prompt("Nombre jugador:");
    if (!name) return;
    try {
      await joinGame(gameId, name);
      navigate(`/game/${gameId}`);
    } catch (error) {
      console.error("Error uniendose a sala:", error);
    }
  };

  const handleJoinByCode = async () => {
    const game = games.find((g) => g.id === parseInt(code));
    if (game) {
      handleJoin(game.id);
    } else {
      alert("No se encontró una sala con ese código.");
    }
  };

  const handleCreate = async () => {
    const name = prompt("Nombre de la sala:");
    if (!name) return;
    try {
      const newGame = await createGame({ code: name, timePerRoundSeconds: 60, rounds: 3, categories: [] });
      alert(`Sala creada correctamente. Código de la sala: ${newGame.code}`);
      loadGames();
      navigate(`/game/${newGame.id}`);
    } catch (err) {
      alert("Error a crear la sala.");
    }
  };

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h1>Tutti Frutti</h1>

      <div style={{ marginBottom: "1rem" }}>
        <input type="text" placeholder="Código de Sala" value={code} onChange={(e) => setCode(e.target.value)} />
        <input type="text" placeholder="Nombre" value={playerName} onChange={(e) => setPlayerName(e.target.value)} />
        <button onClick={handleJoinByCode}>Unirse</button>
      </div>

      <h2>Salas Disponibles</h2>

      {loading ? (
        <p>Cargando...</p>
      ) : games.length === 0 ? (
        <p>No hay salas disponibles.</p>
      ) : (

        <table border="1" cellPadding="8" style={{ margin: "0 auto"}}>
        <thead>
          <tr>
          <th>Código</th>
          <th>Estado</th>
          <th>Jugadores</th>
          <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          {games.map((game) => (
            <tr key={game.id}>
              <td>{game.code}</td>
              <td>{game.status}</td>
              <td>{game.players?.length || 0}</td>
              <td>
                {game.status === "WAITING" ? (
                  <button onClick={() => handleJoin(game.id)}>Unirse</button>
                ) : (
                  <button disabled>Ver</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      )}

      <br />
      <button onClick={handleCreate}>Crear nueva Sala</button>
    </div>
  );
}