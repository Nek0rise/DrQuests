package uwu.nekorise.drQuests.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import uwu.nekorise.drQuests.quest.model.*;
import uwu.nekorise.drQuests.quest.model.target.*;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;

import java.io.File;

@RequiredArgsConstructor
public class QuestConfigLoader {

    private final ConfigManager configManager;

    public void load(QuestRegistry registry) {
        registry.clear();
        File[] files = configManager.getGuiFolder().listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) return;

        for (File file : files) {
            loadFile(file, registry);
        }
    }

    private void loadFile(File file, QuestRegistry registry) {
        try {
            FileConfiguration config = configManager.getConfig("gui/" + file.getName());

            for (String key : config.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection(key);
                if (section == null) continue;
                if (!section.contains("type")) continue;
                if (!section.contains("target")) continue;
                if (!section.contains("required-amount")) continue;

                String questId = section.getString("id", key);
                QuestType type = QuestType.valueOf(section.getString("type").toUpperCase());
                String stringTarget = section.getString("target");
                int requiredAmount = section.getInt("required-amount");
                QuestTarget target = mapTarget(type, stringTarget);

                QuestDefinition definition = new QuestDefinition(
                        questId,
                        target,
                        type,
                        requiredAmount
                );

                registry.register(definition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QuestTarget mapTarget(QuestType type, String target) {
        return switch (type) {
            case MINING -> new MaterialTarget(Material.valueOf(target));
            case KILLING -> new EntityTarget(EntityType.valueOf(target));
            case EXPLORING -> new BiomeTarget(parseBiome(target));
            case RUNNING -> new NumericTarget(Integer.parseInt(target));
            default -> throw new IllegalStateException("Unknown quest type: " + type);
        };
    }

    private Biome parseBiome(String biomeKey) {
        NamespacedKey key = NamespacedKey.fromString(biomeKey);
        if (key == null) throw new IllegalStateException("Target cannot be null");

        Biome biome = Registry.BIOME.get(key);
        if (biome == null) throw new IllegalStateException("Biome not found: " + key);

        return biome;
    }
}
