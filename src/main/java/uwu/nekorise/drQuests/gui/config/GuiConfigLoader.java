package uwu.nekorise.drQuests.gui.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import uwu.nekorise.drQuests.config.ConfigManager;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class GuiConfigLoader {

    private final ConfigManager configManager;

    public void load(GuiRegistry registry) {

        File[] files = configManager.getGuiFolder().listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) return;

        for (File file : files) {
            List<GuiItem> items = new ArrayList<>();
            String guiName = file.getName().replace(".yml", "");
            loadFile(file, items);
            if (items.isEmpty()) continue;

            // FIXME FIXME FIXME FIXME FIXME
            GuiDefinition definition = new GuiDefinition(guiName, 27, guiName, items);

            registry.register(definition);
        }
    }


    private void loadFile(File file, List<GuiItem> items) {
        try {
            FileConfiguration config = configManager.getConfig("gui/" + file.getName());

            for (String questKey : config.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection(questKey);

                if (section == null) continue;
                if (!section.isConfigurationSection("gui-item")) continue;

                String questId = section.getString("id", questKey);

                ConfigurationSection guiSection = section.getConfigurationSection("gui-item");

                Material material = Material.valueOf(guiSection.getString("material"));
                int slot = guiSection.getInt("index");
                String name = guiSection.getString("name");
                List<String> lore = guiSection.getStringList("lore");
                List<ItemFlag> flags = parseFlags(guiSection.getStringList("flags"));
                Integer customModelData = null;
                if (guiSection.contains("custom-model-data")) {
                    customModelData = guiSection.getInt("custom-model-data");
                }

                GuiItem item = new GuiItem(questId, material, slot, name, lore, flags, customModelData);
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ItemFlag> parseFlags(List<String> raw) {
        List<ItemFlag> flags = new ArrayList<>();

        for (String s : raw) {
            try {
                flags.add(ItemFlag.valueOf(s));
            } catch (IllegalArgumentException exception) {
                exception.printStackTrace();
            }
        }
        return flags;
    }
}
