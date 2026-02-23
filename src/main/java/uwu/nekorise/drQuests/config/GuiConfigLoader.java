package uwu.nekorise.drQuests.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import uwu.nekorise.drQuests.gui.action.CloseGuiAction;
import uwu.nekorise.drQuests.gui.action.GuiAction;
import uwu.nekorise.drQuests.gui.action.OpenGuiAction;
import uwu.nekorise.drQuests.gui.action.RefreshGuiAction;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GuiConfigLoader {
    private final ConfigManager configManager;

    public void load(GuiRegistry registry) {
        registry.clear();

        File[] files = configManager.getGuiFolder().listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            loadFile(file, registry);
        }
    }

    private void loadFile(File file, GuiRegistry registry) {
        try {
            FileConfiguration config = configManager.getConfig("gui/" + file.getName());

            String guiName = file.getName().replace(".yml", "");
            String title = config.getString("title", guiName);
            int size = config.getInt("size", 27);
            List<GuiItem> items = new ArrayList<>();

            for (String key : config.getKeys(false)) {
                if (key.equalsIgnoreCase("title") || key.equalsIgnoreCase("size")) {
                    continue;
                }

                ConfigurationSection section = config.getConfigurationSection(key);

                if (section == null) continue;
                if (!section.isConfigurationSection("gui-item")) continue;

                String questId = section.getString("id", key);
                ConfigurationSection guiSection = section.getConfigurationSection("gui-item");
                if (guiSection == null) continue;

                Material material = Material.valueOf(guiSection.getString("material").toUpperCase());
                int slot = guiSection.getInt("index");
                String name = guiSection.getString("name");
                List<String> lore = guiSection.getStringList("lore");
                List<ItemFlag> flags = parseFlags(guiSection.getStringList("flags"));
                List<GuiAction> actions = parseActions(guiSection.getStringList("actions"));
                Integer customModelData = guiSection.contains("custom-model-data") ? guiSection.getInt("custom-model-data") : null;

                GuiItem item = new GuiItem(
                        questId,
                        material,
                        slot,
                        name,
                        lore,
                        flags,
                        customModelData,
                        actions
                );

                items.add(item);
            }

            if (!items.isEmpty()) {
                GuiDefinition definition = new GuiDefinition(guiName, size, title, items);
                registry.register(definition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ItemFlag> parseFlags(List<String> raw) {
        List<ItemFlag> flags = new ArrayList<>();
        for (String s : raw) {
            try {
                flags.add(ItemFlag.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        return flags;
    }

    private List<GuiAction> parseActions(List<String> stringActions) {
        List<GuiAction> actions = new ArrayList<>();

        for (String s : stringActions) {
            if (s.startsWith("[open_gui]")) {
                String target = s.replace("[open_gui]", "").trim();
                actions.add(new OpenGuiAction(target));
            } else if (s.startsWith("[close]")) {
                actions.add(new CloseGuiAction());
            } else if (s.startsWith("[refresh]")) {
                actions.add(new RefreshGuiAction());
            }
        }
        return actions;
    }
}