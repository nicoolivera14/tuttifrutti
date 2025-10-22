package com.tuttifrutti.demo.domain.model;

public enum GameStatus {
    WAITING,
    IN_ROUND,
    FINISHED;

    public static GameStatus fromString(String value) {
        try {
            return GameStatus.valueOf(value);
        } catch (Exception e) {
            return WAITING; // default si DB tiene valor inválido
        }
    }
}