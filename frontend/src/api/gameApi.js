const BASE_URL = "http://localhost:8080/games";

export async function getAllGames() {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw new Error("Error al obtener las salas.");
    return res.json();
}

export async function createGame(data) {
    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res.ok)  throw new Error("Error al crear la sala.");
    return res.json();
}

export async function joinGame(gameId, playerName) {
  const res = await fetch(`${BASE_URL}/${gameId}/join?playerName=${playerName}`, {
    method: "POST",
  });
  if (!res.ok)  throw new Error("Error al unirse a la sala.");
  return res.json();
}

export async function joinGameByCode(gameCode, playerName) {
  const res = await fetch(`${BASE_URL}/join-by-code?gameCode=${gameCode}&playerName=${playerName}`, {
    method: "POST",
    });
    if (!res.ok) throw new Error("Error al unirse a la sala con código.");
    return res.json();
}

export async function startRound(gameId) {
  const res = await fetch(`${BASE_URL}/${gameId}/start-round`, { method: "POST" });
  if (!res.ok)  throw new Error("Error al iniciar la ronda.");
  return res.json();
}

export async function endRound(gameId) {
  const res = await fetch(`${BASE_URL}/${gameId}/end-round`, { method: "POST" });
  if (!res.ok)  throw new Error("Error al finalizar la ronda.");
  return res.json();
}

export async function getGameById(id) {
  const res = await fetch(`http://localhost:8080/games/${id}`);
  return res.json();
}

export async function resetGame(gameId) {
    const res = await fetch(`http://localhost:8080/games/${gameId}/reset`, {
        method: "POST",
    });
    return res.json();
}