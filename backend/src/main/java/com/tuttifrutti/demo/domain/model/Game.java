package com.tuttifrutti.demo.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status = GameStatus.WAITING;

    @PrePersist
    @PreUpdate
    private void fixInvalidStatus() {
        if (status == null) {
            status = GameStatus.WAITING;
        }
    }

    private int timePerRoundSeconds;
    private int rounds;

    @ElementCollection
    private List<String> categories = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    private int currentRoundIndex = 0;

    private boolean started = false;
    private  boolean finished = false;
    private char currentLetter;

    //private Round currentRound;
}
