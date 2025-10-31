import React, { useState } from "react";
import { loginPlayer } from "../api/playerApi";
import { useNavigate, Link } from "react-router-dom";

export default function Login() {
    const [form, setForm] = useState({ email: "", password: ""});
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value});
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const player = await loginPlayer(form);
            setMessage(`Bienvenido, ${player.name}`);
            //Guardar datos temporalmente para la sesión
            sessionStorage.setItem("player", JSON.stringify(player));
            navigate("/home");
        } catch (error) {
            setMessage("Email o contraseña incorrectos.")
        }
    };

    return (
        <div className="login-container">
            <h2>Iniciar sesión</h2>
            <form onSubmit={handleSubmit}>
                <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required/>
                <input name="password" type="password" placeholder="Contraseña" value={form.password} onChange={handleChange} required/>
                <button type="submit">Entrar</button>
            </form>
            <p>{message}</p>
            <p>Usuario nuevo? <Link to="/signup">Registrate acá</Link></p>
        </div>
    );
}