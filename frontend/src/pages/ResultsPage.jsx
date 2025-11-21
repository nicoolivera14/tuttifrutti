import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getGameById } from "../api/gameApi";

export default function ResultsPage() {
    const { gameId } = useParams();
    const [game, setGame ] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        load();
    }, []);

    const load = async () => {
        try {
            const g = await getGameById(gameId);
            setGame(g);
        } catch (e) {
            console.error("Error cargando resultados", e);
        }
    };

    if (!game) return <p>Cargando...</p>;

    return (
        <div style={{ padding: "2rem" }}>
            <h2>Juego terminado</h2>
            <p>Código: {game.code}</p>

            <h3>Resultados</h3>
            <ul>
                {game.players.map((p) => (
                    <li key={p.id}>
                        {p.name} - {p.totalScore} puntos
                    </li>
                ))}
            </ul>

            <button onClick={() => navigate("/home")}>
                Volver a las salas
            </button>
        </div>
    );
}