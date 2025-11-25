import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getGameById } from "../api/gameApi";
import { submitAnswer, finishTurn } from "../api/answerApi";
import { createStompClient } from "../websocketClient";
import axios from "axios";

export default function GamePlay() {
    const { gameId } = useParams();
    const [game, setGame] = useState(null);
    const [answers, setAnswers] = useState({});
    const [playerId, setPlayerId] = useState(null);
    const navigate = useNavigate();

    const [timeLeft, setTimeLeft] = useState(0);
    const [currentRound, setCurrentRound] = useState(null);
    const [timerStarted, setTimerStarted] = useState(false);

    const [roundStarted, setRoundStarted] = useState(false);
    const firstLoadRef = useRef(true);

    useEffect(() => {
        setGame(null);
        setCurrentRound(null);
        setTimeLeft(0);
        setTimerStarted(false);
        setRoundStarted(false)
    }, [gameId]);

    //CARGAR EL JUEGO
    useEffect(() => {
    if (firstLoadRef.current) {
        firstLoadRef.current = false;
        loadGame();
    }
    }, [gameId]);

    //WEBSOCKET
    useEffect(() => {
        const client = createStompClient(() => {
            console.log("Conectado a WebSocket");

            client.subscribe(`/topic/games/${gameId}`, (message) => {
                const updatedGame = JSON.parse(message.body);
                console.log("Evento WS, juego actualizado:", updatedGame);

                setGame(updatedGame);
                setCurrentRound(updatedGame.currentRoundIndex);
                setTimeLeft(updatedGame.timePerRoundSeconds);
            });
        });

        return () => {
            console.log("Websocket desconectado.");
            client.deactivate();
        };
    }, [gameId]);

    //REDIRIGIR A RESULTADOS
    useEffect(() => {
        if (!game) return;

        if (game.status === "FINISHED") {
            navigate(`/game/${game.id}/results`);
        }
    }, [game, navigate]);

    useEffect(() => {
        if (!game) return;
        if (game.status !== "IN_ROUND") return;

        if (game.currentRoundIndex !== currentRound) {
            setCurrentRound(game.currentRoundIndex);
            setTimeLeft(game.timePerRoundSeconds);
        }
    }, [game, currentRound]);

    //RECARGAR EL ESTADO DEL JUEGO
    useEffect(() => {
        const interval = setInterval(() => loadGame(), 2000);
        return () => clearInterval(interval);
    }, []);

    //TIMER
    useEffect(() => {
        if (!game?.roundEndTimestamp) return;

        const interval = setInterval(() => {
            const now = Date.now();
            const diff = Math.max(0, Math.floor((game.roundEndTimestamp - now) / 1000));
            setTimeLeft(diff);

            if (diff === 0) {
                clearInterval(interval);
                handleTimeOver();
            }
        }, 250);

        return () => clearInterval(interval);
    }, [game?.roundEndTimestamp]);

    const loadGame = async () => {
        try {
            
            const found = await getGameById(gameId);

            const sessionPlayer = JSON.parse(sessionStorage.getItem("player"));
            const playerName = sessionPlayer?.name;

            const playerInGame = found.players.find(p => p.name === playerName);

            if (!playerInGame) {
                console.error("Jugador no encontrado dentro del juego");
                navigate("/home");
                return;
            }

            setPlayerId(playerInGame.id);

            if (!roundStarted && found.status === "WAITING" && found.currentRoundIndex === -1) {
            
                setRoundStarted(true);
                
                const next = await axios.post(`http://localhost:8080/games/${gameId}/next-round`);
                
                setGame(next.data);
                setTimeLeft(next.data.timePerRoundSeconds);
                setCurrentRound(next.data.currentRoundIndex);
                setTimerStarted(true);
                return;
            }

    
            if (game && found.currentRoundIndex !== currentRound) {
                
                setCurrentRound(found.currentRoundIndex);
                setTimeLeft(found.timePerRoundSeconds);
                setTimerStarted(true);
            }

            setGame(found);

        } catch (err) {
            console.error("Error cargando juego:", err);
        }
    };

    //MANEJO DE INPUTS
    const handleChange = (category, value) => {
        setAnswers((prev) => ({ ...prev, [category]: value}));
    };

    const handleTimeOver = async () => {
        await finishTurn(game.id, playerId);
    };

    const handleTuttiFrutti = async () => {
        try {
            if (!playerId) {
                console.error("No hay playerId definido");
                return;
            }

            for (const category of game.categories) {
                const value = answers[category] || "";
                await submitAnswer(game.id, playerId, category, value);
            }
        
            await axios.post(`http://localhost:8080/games/${game.id}/force-end-round?playerId=${playerId}`);
            alert("TuttiFrutti! Esperando siguiente ronda...");

            setAnswers({});
            loadGame();
        } catch (error) {
            console.error("Error enviando respuestas:", error);
        }
    };

    const handleSurrender = async () => {
        try {
            if (!playerId) {
                console.error("No hay playerId definido");
                return;
            }

            for (const category of game.categories) {
                const value = answers[category] || "";
                await submitAnswer(game.id, playerId, category, value);
            }

            await finishTurn(game.id, playerId);
        
            alert("Te rendiste! Esperando siguiente ronda...");

            setAnswers({});
            loadGame();
        } catch (error) {
            console.error("Error enviando respuestas:", error);
        }
    };

    if (!game) return <p>Cargando juego...</p>;

    const displayRound = game.currentRoundIndex < 0 ? 1 : game.currentRoundIndex + 1;

    return (
        <div style={{ 
            padding: "2rem",
            maxWidth: "600 px",
            margin: "0 auto",
            textAlign: "center",
         }}>
            <h2>Juego: {game.name}</h2>
            <h3>Letra actual: <span style={{ color: "#4caf50" }}> {game.currentLetter || "-"}</span></h3>
            <h4>Ronda {displayRound} / {game.rounds}</h4>
            <h3>Tiempo restante: {timeLeft}s</h3>

            {game.status === "FINISHED" ? (
                <div style={{ marginTop: "2rem", color: "green", fontWeight: "bold" }}>
                    Juego finalizado
                </div>
            ) : (
                <div style={{ marginTop: "1.5rem" }}>
                    {game.categories.map((cat, i) => (
                        <div key={i} style={{ marginBottom: "0.75rem" }}>
                            <label 
                                style={{
                                    display: "inline-block",
                                    width: "150px",
                                    fontWeight: "bold",
                                    textAlign: "left",
                                }}>
                                    {cat}:
                                </label>
                                <input 
                                    type="text"
                                    value={answers[cat] || ""}
                                    onChange={(e) => handleChange(cat, e.target.value)}
                                    style={{
                                        padding: "0.4rem",
                                        width: "200px",
                                        borderRadius: "4px",
                                        border: "1px solid #aaa",
                                    }} 
                                />
                        </div>
                    ))}          

            <button 
            onClick={handleTuttiFrutti} 
            style={{
                marginTop: "1.5rem",
                padding: "0.5rem 1rem",
                background: "#2196f3",
                color: "white",
                border: "none",
                borderRadius: "5px",
            }}>Tutti Frutti!</button>

            <button
             onClick={handleSurrender}
             style={{
                 marginTop: "1.5rem",
                 marginLeft: "1rem",
                 padding: "0.5rem 1rem",
                 background: "#f44336",
                 color: "white",
                 border: "none",
                 borderRadius: "5px",
                }}
                >Me rindo</button>

            <button
             onClick={() => navigate("/home")}
             style={{
                 marginTop: "2rem",
                 padding: "0.5rem 1rem",
                 background: "#ccc",
                 border: "none",
                 borderRadius: "5px",
                }}
                >Volver</button>
                </div>
            )}
        </div>
    );
}