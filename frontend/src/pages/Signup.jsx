import React, { useState } from "react";
import { signupPlayer } from "../api/playerApi";

export default function Signup() {
    const [form, setForm] = useState({name: "", email: "", password: ""});
    const [message, setMessage]= useState("");

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await signupPlayer(form);
            setMessage("Registro exitoso.");
        } catch (error) {
            setMessage("Error al registrarte.");
        }
    };

    return (
        <div className="signup-container">
            <h2>Registrarse</h2>
            <form onSubmit={handleSubmit} style={{ display: "inline-block"}}>
                <input name="name" placeholder="Nombre" value={form.name} onChange={handleChange} required />
                <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required/>
                <input name="password" type="password" placeholder="Contraseña" value={form.password} onChange={handleChange} required/>
                <button type="submit">Registrarse</button>
            </form>
            <p>{message}</p>
        </div>
    );
}