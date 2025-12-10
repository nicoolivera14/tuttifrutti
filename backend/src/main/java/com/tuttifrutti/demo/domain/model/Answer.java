package com.tuttifrutti.demo.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"answer\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "category", "game_id", "round_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    //MODO PRUEBA
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category; // Ej: "País", "Animal", "Color" (por ahora fijo, luego configurable)
    private String value;    // Ej: "Perú", "Perro", "Púrpura"

    @Column
    private Boolean valid; // Si la respuesta fue correcta (luego lo usará el Judge)

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

}

