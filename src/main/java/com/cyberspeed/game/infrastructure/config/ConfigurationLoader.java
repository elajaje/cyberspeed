package com.cyberspeed.game.infrastructure.config;

import com.cyberspeed.game.Main;
import com.cyberspeed.game.domain.domain.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigurationLoader {

    public static Configuration loadConfig(String resourcePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        try (InputStream inputStream = Main.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, Configuration.class);
        }
    }

}
