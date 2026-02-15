package uwu.nekorise.drQuests.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.nekorise.drQuests.DrQuests;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ConfigManager {

    private final DrQuests plugin;
    private final File dataFolder;

    private final List<String> langConfigFiles = List.of("en.yml");
    private final List<String> guiConfigFiles = List.of("example.yml");

    public ConfigManager(DrQuests plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
    }

    public void init() {
        createFolders();
        createConfigs();
    }

    public FileConfiguration getConfig(String relativePath) throws IOException, InvalidConfigurationException {
        File file = new File(dataFolder, relativePath);

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        configuration.load(file);

        return configuration;
    }

    public File getGuiFolder() {
        return new File(dataFolder, "gui");
    }

    public File getLangFolder() {
        return new File(dataFolder, "lang");
    }

    private void createFolders() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        new File(dataFolder, "lang").mkdirs();
        new File(dataFolder, "gui").mkdirs();
    }

    private void createConfigs() {

        // config.yml
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        // lang
        for (String fileName : langConfigFiles) {
            File file = new File(dataFolder, "lang/" + fileName);

            if (!file.exists()) {
                plugin.saveResource("lang/" + fileName, false);
            }
        }

        // gui
        for (String fileName : guiConfigFiles) {
            File file = new File(dataFolder, "gui/" + fileName);

            if (!file.exists()) {
                plugin.saveResource("gui/" + fileName, false);
            }
        }
    }
}