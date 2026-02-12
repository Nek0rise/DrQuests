package uwu.nekorise.drQuests.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.nekorise.drQuests.DrQuests;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {
    private final static String path = DrQuests.getInstance().getDataFolder().getAbsolutePath() + "/";
    private final static List<String> langConfigFiles = List.of("en.yml");
    private final static List<String> guiConfigFiles = List.of("example.yml");

    public static FileConfiguration getConfig(String cfgName) throws IOException, InvalidConfigurationException {
        FileConfiguration fileConfiguration = new YamlConfiguration();
        fileConfiguration.options().parseComments(true);
        fileConfiguration.load(path + cfgName);

        return fileConfiguration;
    }

    private static void createConfigs() {
        File defaultConfig = new File(path, "config.yml");
        if (!defaultConfig.exists()) {
            DrQuests.getInstance().saveResource("config.yml", false);
        }

        for (String langConfigName : langConfigFiles) {
            File langConfig = new File(path, "lang/" + langConfigName);
            if (!langConfig.exists()) {
                DrQuests.getInstance().saveResource("lang/" + langConfigName, false);
            }
        }
        for (String guiConfigName : guiConfigFiles) {
            File guiFolder = new File(path, "gui/" + guiConfigName);
            if (!guiFolder.exists()) {
                DrQuests.getInstance().saveResource("gui/" + guiConfigName, false);
            }
        }
    }

    public static void loadConfig() {
        createConfigs();
        //MainConfigStorage.loadData();
        //LangConfigStorage.loadData();
        //GuiConfigStorage.loadData();
    }
}
