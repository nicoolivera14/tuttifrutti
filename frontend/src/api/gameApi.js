const BASE_URL = "http://localhost:8080/games";

export async function getAllGames() {
    const res = await fetch(BASE_URL);
    return res.json();
}

export async function createGame(data) {
    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    return res.json();
}

export async function joinGame(gameId, playerName) {
  const res = await fetch(`${BASE_URL}/${gameId}/join?playerName=${playerName}`, {
    method: "POST",
  });
  return res.json();
}

export async function startRound(gameId) {
  const res = await fetch(`${BASE_URL}/${gameId}/start-round`, { method: "POST" });
  return res.json();
}

export async function endRound(gameId) {
  const res = await fetch(`${BASE_URL}/${gameId}/end-round`, { method: "POST" });
  return res.json();
}