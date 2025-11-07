package com.tuttifrutti.demo.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class CreateGameRequestDTO {
    private String name;
    private String playerName;
    private int timePerRoundSeconds;
    private int rounds;
    private List<String> categories;

    public int getTimePerRoundSeconds() { return timePerRoundSeconds;}
    public void setTimePerRoundSeconds(int timePerRoundSeconds) { this.timePerRoundSeconds = timePerRoundSeconds;}
}
