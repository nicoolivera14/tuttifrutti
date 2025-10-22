// pages/Home.jsx
import React from "react";

export default function Home() {
  return <div><h2>Home page cargada</h2></div>;
}


/*import React, { useEffect, useState } from "react";
import gameApi from "../api/gameApi.jsx";
import { useNavigate } from "react-router-dom";

function Home() {
  const [games, setGames] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadGames();
  }, []);

  const loadGames = async () => {
    const res = await gameApi.getAllGames();
    setGames(res);
  };

  const joinGame = async (gameId) => {
    const playerName = prompt("Tu nombre:");
    if (!playerName) return;
    await gameApi.joinGame(gameId, playerName);
    navigate(`/game/${gameId}`);
  };

  return (
    <div>
      <h1>Real Maní Games</h1>
      <ul>
        {games.map((g) => (
          <li key={g.id}>
            {g.code} - {g.status}
            <button onClick={() => joinGame(g.id)}>Unirse</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Home;*/