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
public class Configuration {
    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private Probability probabilities;
    private Map<String, WinCombination> winCombinations;
}
