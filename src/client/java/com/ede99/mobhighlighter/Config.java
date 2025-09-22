package com.ede99.mobhighlighter;

import com.ede99.mobhighlighter.screen.DefaultMobRegistry;
import com.ede99.mobhighlighter.screen.MobData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "mob_highlighter.json");
    private static final File COLOR_CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "mob_highlighter_colors.json");

    private static Map<String, Boolean> mobStates = new HashMap<>();
    private static Map<String, Integer> mobColors = new HashMap<>();
    private static double maxRenderDistance = 64.0;

    public static void load() {
        try {
            if (CONFIG_FILE.exists()) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Boolean>>(){}.getType();
                Map<String, Boolean> loaded = gson.fromJson(new FileReader(CONFIG_FILE), type);
                if (loaded != null) mobStates = loaded;
            }

            if (COLOR_CONFIG_FILE.exists()) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Integer>>(){}.getType();
                Map<String, Integer> loadedColors = gson.fromJson(new FileReader(COLOR_CONFIG_FILE), type);
                if (loadedColors != null) mobColors = loadedColors;
            } else {
                initializeDefaultColors();
                saveColors();
            }
        } catch (Exception e) {
            System.err.println("Failed to load Mob Highlighter config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDefaultColors() {
        for (MobData mobData : DefaultMobRegistry.createDefaultMobs()) {
            mobColors.put(mobData.getName(), 0xFF000000 | mobData.getColor());
        }
    }

    private static void saveColors() {
        try (FileWriter writer = new FileWriter(COLOR_CONFIG_FILE)) {
            Gson gson = new Gson();
            gson.toJson(mobColors, writer);
        } catch (Exception e) {
            System.err.println("Failed to save Mob Highlighter color config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new Gson();
            gson.toJson(mobStates, writer);
        } catch (Exception e) {
            System.err.println("Failed to save Mob Highlighter config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean getState(String mobName, boolean defaultValue) {
        return mobStates.getOrDefault(mobName, defaultValue);
    }

    public static void setState(String mobName, boolean enabled) {
        mobStates.put(mobName, enabled);
        // Don't save immediately - use batch saving for better performance
    }

    public static int getColor(String mobName, int defaultColor) {
        return mobColors.getOrDefault(mobName, defaultColor);
    }

    public static void setColor(String mobName, int color) {
        mobColors.put(mobName, color);
        // Don't save immediately - use batch saving for better performance
    }

    public static Map<String, Boolean> getAllStates() {
        return new HashMap<>(mobStates);
    }

    public static Map<String, Integer> getAllColors() {
        return new HashMap<>(mobColors);
    }

    public static double getMaxRenderDistance() {
        return maxRenderDistance;
    }

    public static void setMaxRenderDistance(double distance) {
        maxRenderDistance = Math.max(16.0, Math.min(256.0, distance));
    }

    // Batch save methods for better performance
    public static void saveAll() {
        save();
        saveColors();
    }
}
