import { data } from "react-router-dom";

const BASE_URL = "http://localhost:8080/players";

export async function signupPlayer(player) {
    const response = await fetch(`${BASE_URL}/signup`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify(player),
    });

    if (!response.ok) {
        throw new Error("Error al registrar el usuario,");
    }

    return await response.json();    
}

export async function loginPlayer(credentials) {
    const response = await fetch(`${BASE_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
    });
    if (!response.ok) throw new Error("Error al iniciar sesión.");
    return await response.json();
}

export function getCurrentPlayer() {
    const stored = sessionStorage.getItem("player");
    return stored ? JSON.parse(stored) : null;
}