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

                // gui-item data fill
                ConfigurationSection guiSection = section.getConfigurationSection("gui-item");

                Material material = Material.valueOf(guiSection.getString("material"));
                int slot = guiSection.getInt("index");
                String name = guiSection.getString("name");
                List<String> lore = guiSection.getStringList("lore");
                List<ItemFlag> flags = parseFlags(guiSection.getStringList("flags"));
                List<GuiAction> actions = parseActions(guiSection.getStringList("actions"));
                Integer customModelData = guiSection.contains("custom-model-data") ? guiSection.getInt("custom-model-data") : null;

                GuiItem item = new GuiItem(questId, material, slot, name, lore, flags, customModelData, actions);
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

    private List<GuiAction> parseActions(List<String> raw) {
        List<GuiAction> actions = new ArrayList<>();

        for (String s : raw) {
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
