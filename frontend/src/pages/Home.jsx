import React, { useEffect, useState } from "react";
import { getAllGames, createGame, joinGameByCode } from "../api/gameApi";
import { useNavigate } from "react-router-dom";

function getCurrentPlayer() {
  const stored = sessionStorage.getItem("player");
  return stored ? JSON.parse(stored) : null;
}

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

  const handleJoinByCode = async () => {
    const currentPlayer = getCurrentPlayer();
    if (!currentPlayer) return;

    if (!code.trim()) {
      alert("Ingresá un código de sala.");
      return;
    }

    try {
      const updatedGame = await joinGameByCode(code, currentPlayer.name);
      navigate(`/game/${updatedGame.id}`);
    } catch (error) {
      console.error("Error uniendose a sala:", error);
    }
  };

  const handleCreate = async () => {
    const currentPlayer = getCurrentPlayer();
    if (!currentPlayer) {
      alert("Tenés que iniciar sesión primero.");
      return;
    }
    const gameName = `Sala de ${currentPlayer.name}`;
   
    try {
      const newGame = await createGame({ 
        name: gameName,
        playerName: currentPlayer.name, 
        timePerRoundSeconds: 60, 
        rounds: 3, 
        categories: [] });
      alert(`Sala creada correctamente. Código de la sala: ${newGame.code}`);
      loadGames();
      navigate(`/game/${newGame.id}`);
    } catch (err) {
      alert("Error a crear la sala.");
    }
  };

  const handleJoinFromList = async (game) => {
    const currentPlayer = getCurrentPlayer();
    if (!currentPlayer) {
      alert("Tenés que iniciar sesión primero.");
      return;
    }

    try {
      await joinGameByCode(game.code, currentPlayer.name);
      navigate(`/game/${game.code}`);
    } catch (error) {
      alert("No se pudo unir a la sala.")
    }
  };

  return (
    <div style={{ textAlign: "center", padding: "2rem" }}>
      <h1>Tutti Frutti</h1>

      <div style={{ marginBottom: "1rem" }}>
        <input type="text" placeholder="Código de Sala" value={code} onChange={(e) => setCode(e.target.value.toUpperCase())} />
        <button onClick={handleJoinByCode}>Unirse por código</button>
      </div>

      <h2>Salas Disponibles</h2>
      <button onClick={loadGames} style={{ marginBottom: "1rem" }}>Actualizar lista</button>
      {loading ? (
        <p>Cargando...</p>
      ) : games.length === 0 ? (
        <p>No hay salas disponibles.</p>
      ) : (

        <table border="1" cellPadding="8" style={{ margin: "0 auto"}}>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Código</th>
            <th>Estado</th>
            <th>Jugadores</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          {games.map((game) => (
            <tr key={game.id}>
              <th>{game.name}</th>
              <td>{game.code}</td>
              <td>{game.status}</td>
              <td>{game.players?.length || 0}</td>
              <td>
                {game.status === "WAITING" ? (
                  <>
                  <button onClick={() => navigate(`/game/${game.id}`)}>Ver</button>
                  </>
                ) : (
                  <button onClick={() => navigate(`/game/${game.id}`)}>Ver</button>
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