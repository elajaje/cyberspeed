package com.cyberspeed.game.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Symbol {
    private String name;
    private double rewardMultiplier;
    private String type;
    private String impact;
    int extra;
}
