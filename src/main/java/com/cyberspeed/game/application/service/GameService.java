package com.cyberspeed.game.application.service;

import com.cyberspeed.game.domain.domain.Configuration;
import com.cyberspeed.game.domain.domain.Result;
import com.cyberspeed.game.domain.domain.Symbol;
import com.cyberspeed.game.domain.domain.WinCombination;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@AllArgsConstructor
public class GameService {

    private final Configuration configuration;

    public Result playGame(double betAmount) {
        // Generate matrix based on configuration probabilities
        String[][] matrix = generateMatrix();
        // Check for winning combinations
        List<WinCombination> winningCombinations = checkWinningCombinations(matrix);
        // Apply bonus symbols if any
        double reward = calculateReward(betAmount, winningCombinations, matrix);
        // Return the game result
        return new Result(matrix, reward, winningCombinations);
    }

    /**
     * Generates a matrix based on the configuration probabilities
     */
    private String[][] generateMatrix() {
        // Implementation for matrix generation based on probabilities
        int rows = configuration.getRows();
        int columns = configuration.getColumns();
        String[][] matrix = new String[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                matrix[row][col] = getRandomSymbol(row, col);
            }
        }
        return matrix;
    }

    /**
     * Fetches a random symbol based on the defined probabilities for each cell. The index calculation ensures it does not exceed the bounds of the list.
     */
    private String getRandomSymbol(int row, int col) {
        // Verify if the computed index is within the valid range
        int columns = configuration.getColumns();
        int rows = configuration.getRows();
        int numberOfStandardSymbols = configuration.getProbabilities().getStandardSymbols().size();

        int index = row * columns + col;

        if (numberOfStandardSymbols < rows * columns) {
            throw new IllegalStateException("Not enough standard symbols for the specified matrix dimensions");
        }

        Map<String, Integer> symbolProbabilities = configuration.getProbabilities().getStandardSymbols().get(index).getSymbols();
        int totalWeight = symbolProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);
        for (Map.Entry<String, Integer> entry : symbolProbabilities.entrySet()) {
            randomValue -= entry.getValue();
            if (randomValue < 0) {
                return entry.getKey();
            }
        }
        return "MISS"; // Fallback in case of error
    }

    /**
     * Checks for any winning combinations in the generated matrix.
     */
    private List<WinCombination> checkWinningCombinations(String[][] matrix) {
        // Implementation for checking winning combinations
        List<WinCombination> winningCombinations = new ArrayList<>();

        for (WinCombination combination : getWinCombinationList(configuration.getWinCombinations())) {
            if (isWinningCombination(matrix, combination)) {
                winningCombinations.add(combination);
            }
        }

        return winningCombinations;
    }

    private List<WinCombination> getWinCombinationList(Map<String, WinCombination> winCombinations) {
        List<WinCombination> winCombinationList = new ArrayList<>();
        for (Map.Entry<String, WinCombination> entry : winCombinations.entrySet()) {
            winCombinationList.add(entry.getValue());
        }
        return winCombinationList;
    }

    /**
     * Determines if a combination is winning based on its type.
     */
    private boolean isWinningCombination(String[][] matrix, WinCombination combination) {
        return switch (combination.getGroup()) {
            case "same_symbols" -> checkSameSymbols(matrix, combination);
            case "horizontally_linear_symbols", "vertically_linear_symbols", "ltr_diagonally_linear_symbols", "rtl_diagonally_linear_symbols" ->
                    checkLinearSymbols(matrix, combination);
            default -> false;
        };
    }

    private boolean checkSameSymbols(String[][] matrix, WinCombination combination) {
        Map<String, Integer> symbolCount = new HashMap<>();
        for (String[] row : matrix) {
            for (String symbol : row) {
                symbolCount.put(symbol, symbolCount.getOrDefault(symbol, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : symbolCount.entrySet()) {
            if (entry.getValue() >= combination.getCount()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLinearSymbols(String[][] matrix, WinCombination combination) {
        for (List<String> area : combination.getCoveredAreas()) {
            boolean match = true;
            String firstSymbol = getSymbolFromPosition(matrix, area.get(0));
            for (String position : area) {
                if (!firstSymbol.equals(getSymbolFromPosition(matrix, position))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    private String getSymbolFromPosition(String[][] matrix, String position) {
        String[] pos = position.split(":");
        int row = Integer.parseInt(pos[0]);
        int col = Integer.parseInt(pos[1]);
        return matrix[row][col];
    }

    /**
     * Calculates the final reward based on the bet amount, winning combinations, and bonus symbols.
     */
    private double calculateReward(double betAmount, List<WinCombination> winningCombinations, String[][] matrix) {
        // Implementation for calculating the final reward
        double reward = 0;
        Set<String> appliedBonusSymbols = new HashSet<>();

        for (WinCombination combination : winningCombinations) {
            double combinationReward = betAmount * combination.getRewardMultiplier();
            reward += combinationReward;

            // Check for bonus symbols in the matrix
            for (String[] row : matrix) {
                for (String symbol : row) {
                    Symbol sym = configuration.getSymbols().get(symbol);
                    if ("bonus".equals(sym.getType())) {
                        applyBonusSymbol(sym, appliedBonusSymbols);
                    }
                }
            }
        }

        // Apply bonus symbols to the total reward
        for (String bonusSymbol : appliedBonusSymbols) {
            Symbol sym = configuration.getSymbols().get(bonusSymbol);
            reward = applyBonusImpact(reward, sym);
        }

        return reward;
    }

    /**
     * Applies the effect of bonus symbols found in the matrix.
     */
    private void applyBonusSymbol(Symbol symbol, Set<String> appliedBonusSymbols) {
        switch (symbol.getImpact()) {
            case "multiply_reward", "extra_bonus":
                appliedBonusSymbols.add(symbol.getName());
                break;
            case "miss":
                // Do nothing
                break;
        }
    }

    /**
     * Adjusts the final reward based on the type of bonus symbol.
     */
    private double applyBonusImpact(double reward, Symbol symbol) {
        switch (symbol.getImpact()) {
            case "multiply_reward":
                reward *= symbol.getRewardMultiplier();
                break;
            case "extra_bonus":
                reward += symbol.getExtra();
                break;
        }
        return reward;
    }
}
