package com.cyberspeed.game.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardSymbol {
    private int column;
    private int row;
    private Map<String, Integer> symbols;
}
