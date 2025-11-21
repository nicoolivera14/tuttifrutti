// src/App.jsx
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Home from "./pages/Home.jsx";
import GameRoom from "./pages/GameRoom.jsx";
import Login from "./pages/Login.jsx";
import Signup from "./pages/Signup.jsx";
import GamePlay from "./pages/GamePlay.jsx";
import ResultsPage from "./pages/ResultsPage.jsx";
import { getCurrentPlayer } from "./api/playerApi.js";

export default function App() {
  const user = getCurrentPlayer();

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/home"
          element={user ? <Home /> : <Navigate to="/" replace />}
        />
        <Route
          path="/game/:gameId"
          element={user ? <GameRoom /> : <Navigate to="/" replace />}
        />
        <Route 
          path="/game/:gameId/play" 
          element={<GamePlay />} />
        <Route
          path="/game/:gameId/results"
          element={<ResultsPage />} />
      </Routes>
    </BrowserRouter>
  );
}
