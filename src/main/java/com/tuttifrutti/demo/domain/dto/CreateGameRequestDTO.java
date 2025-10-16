package com.tuttifrutti.demo.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class CreateGameRequestDTO {
    private int timePerRoundSeconds;

    public int getTimePerRoundSeconds() { return timePerRoundSeconds;}
    public void setTimePerRoundSeconds(int timePerRoundSeconds) { this.timePerRoundSeconds = timePerRoundSeconds;}
}
