package com.cyberspeed.game.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinCombination {
    private String name;
    private double rewardMultiplier;
    private String when;
    private int count;
    private String group;
    private List<List<String>> coveredAreas;
}
