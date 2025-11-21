package com.tuttifrutti.demo.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class GameConfigDTO {
    private int timePerRoundSeconds;
    private int rounds;
    private List<String> categories;
}
