package com.etsuni.fallout;

import java.util.HashMap;
import java.util.Map;

public class FalloutGames {

    private Map<String, Integer> games = new HashMap<>();
    private static FalloutGames instance = new FalloutGames();

    public static FalloutGames getInstance() {
        return instance;
    }

    public Map<String, Integer> getGames() {
        return games;
    }
}
