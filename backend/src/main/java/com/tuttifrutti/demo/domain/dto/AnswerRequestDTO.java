package com.tuttifrutti.demo.domain.dto;

import lombok.Data;

@Data
public class AnswerRequestDTO {
    private Long gameId;
    private Long playerId;
    private String category;
    private String value;
}
