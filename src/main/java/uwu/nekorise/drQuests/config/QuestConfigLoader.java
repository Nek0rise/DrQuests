package uwu.nekorise.drQuests.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import uwu.nekorise.drQuests.quest.model.*;
import uwu.nekorise.drQuests.quest.model.target.*;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

                String questId = section.getString("id", key);

                QuestType type = QuestType.valueOf(section.getString("type").toUpperCase());
                int requiredAmount = section.getInt("required-amount");

                QuestTarget target = null;
                if (type == QuestType.MINING || type == QuestType.KILLING) {
                    String stringTarget = section.getString("target");

                    if (stringTarget == null) throw new IllegalStateException("Missing target for quest: " + questId);
                    target = mapTarget(type, stringTarget);
                }

                String rewardCommand = section.getString("reward-command");
                List<ItemStack> rewardItems = new ArrayList<>();

                for (String material : section.getStringList("reward-items")) {
                    ItemStack rewardItem = new ItemStack(Material.getMaterial(material));
                    rewardItems.add(rewardItem);
                }

                QuestDefinition definition = new QuestDefinition(
                        questId,
                        target,
                        type,
                        requiredAmount,
                        rewardCommand,
                        rewardItems
                );

                registry.register(definition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QuestTarget mapTarget(QuestType type, String target) {
        return switch (type) {
            case MINING -> new MaterialTarget(Material.valueOf(target.toUpperCase()));
            case KILLING -> new EntityTarget(EntityType.valueOf(target.toUpperCase()));
            default -> throw new IllegalStateException("Target not supported for type: " + type);
        };
    }
}