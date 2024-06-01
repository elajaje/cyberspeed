package com.cyberspeed.game;

import com.cyberspeed.game.application.service.GameService;
import com.cyberspeed.game.domain.domain.Configuration;
import com.cyberspeed.game.domain.domain.Result;
import com.cyberspeed.game.infrastructure.config.ConfigurationLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java -jar <your-jar-file> --config config.json --betting-amount 100");
            return;
        }

        String configFilePath = args[1];
        double bettingAmount = Double.parseDouble(args[3]);

        try {
            Configuration config = ConfigurationLoader.loadConfig(configFilePath);
            GameService service = new GameService(config);
            Result result = service.playGame(bettingAmount);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
