package com.etsuni.fallout;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Fallout extends JavaPlugin {

    private File customConfigFile;
    private FileConfiguration customConfig;

    protected static Fallout plugin;

    @Override
    public void onEnable() {
        plugin = this;

        createArenasConfig();
        this.getCommand("fallout").setExecutor(new Commands());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void createArenasConfig() {
        customConfigFile = new File(getDataFolder(), "arenas.yml");
        if(!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("arenas.yml", false);
        }

        customConfig = new YamlConfiguration();

        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void saveCfg() {
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getArenasConfig() {
        return this.customConfig;
    }
}
