package uwu.nekorise.drQuests.config;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.config.model.LangConfig;
import uwu.nekorise.drQuests.config.model.MainConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
public final class ConfigManager {
    private final DrQuests plugin;
    private final File dataFolder;

    private MainConfig mainConfig;
    private LangConfig langConfig;

    private final List<String> langConfigFiles = List.of("en.yml");
    private final List<String> guiConfigFiles = List.of("quests.yml", "quests-2.yml");

    public ConfigManager(DrQuests plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
    }

    public void init() {
        createFolders();
        createConfigs();
        loadMainConfig();
        loadLangConfig();
    }

    private void loadMainConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        String language = config.getString("language");
        String mainGui = config.getString("main-gui");
        String uri = config.getString("mongodb.uri");

        this.mainConfig = new MainConfig(language, mainGui, uri);
    }

    private void loadLangConfig() {
        try {
            File file = new File(getLangFolder(), this.mainConfig.getLanguage() + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            this.langConfig = new LangConfig(
                    config.getString("no-permission"),
                    config.getString("only-players"),
                    config.getString("status.completed"),
                    config.getString("status.in-progress"),
                    config.getString("questadmin-command.usage"),
                    config.getString("questadmin-command.setprogress.usage"),
                    config.getString("questadmin-command.setprogress.invalid-value"),
                    config.getString("questadmin-command.reset.usage"),
                    config.getString("questadmin-command.reload.successfully"),
                    config.getString("questadmin-command.quest-not-found"),
                    config.getString("questadmin-command.successfully-set")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        getGuiFolder().mkdirs();
        getLangFolder().mkdirs();
    }

    private void createConfigs() {
        // config.yml
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        // lang
        for (String fileName : langConfigFiles) {
            File file = new File(getLangFolder(), fileName);
            if (!file.exists()) {
                plugin.saveResource("lang/" + fileName, false);
            }
        }

        // gui
        for (String fileName : guiConfigFiles) {
            File file = new File(getGuiFolder(), fileName);
            if (!file.exists()) {
                plugin.saveResource("gui/" + fileName, false);
            }
        }
    }
}