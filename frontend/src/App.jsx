import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home.jsx";
import GameRoom from "./pages/GameRoom.jsx";

function App() {
  return (
    <Router>
      <div>
        <h1>React conectado siempre a backend</h1>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/" element={<GameRoom />} />
          <Route path="/game/:gameId" element={<GameRoom />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
