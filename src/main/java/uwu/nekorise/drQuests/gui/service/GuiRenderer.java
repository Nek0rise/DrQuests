package uwu.nekorise.drQuests.gui.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uwu.nekorise.drQuests.gui.data.PlayerQuestData;
import uwu.nekorise.drQuests.gui.holder.GuiHolder;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.quest.model.QuestDefinition;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;
import uwu.nekorise.drQuests.util.HEX;
import uwu.nekorise.drQuests.util.PlaceholderUtil;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GuiRenderer {
    private final QuestRegistry questRegistry;

    public Inventory render(GuiDefinition def, PlayerQuestData playerData) {
        String parsedTitle = PlaceholderUtil.parse(def.getTitle(), playerData, null);
        Inventory inventory = Bukkit.createInventory(new GuiHolder(def.getName()), def.getSize(), HEX.applyColor(parsedTitle));

        for (GuiItem guiItem : def.getItems()) {
            QuestDefinition quest = questRegistry.find(guiItem.getQuestId()).orElse(null);
            inventory.setItem(
                    guiItem.getIndex(),
                    createItemStack(guiItem, playerData, quest)
            );
        }

        return inventory;
    }

    private ItemStack createItemStack(GuiItem guiItem, PlayerQuestData playerData, QuestDefinition questDefinition) {
        ItemStack item = new ItemStack(guiItem.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (guiItem.getName() != null) {
            String parsed = PlaceholderUtil.parse(guiItem.getName(), playerData, questDefinition);
            meta.setDisplayName(HEX.applyColor(parsed));
        }

        if (guiItem.getLore() != null) {
            meta.setLore(guiItem.getLore().stream()
                    .map(line -> PlaceholderUtil.parse(
                            line,
                            playerData,
                            questDefinition
                            )
                    ).map(HEX::applyColor)
                    .collect(Collectors.toList())
            );
        }

        if (guiItem.getFlags() != null && !guiItem.getFlags().isEmpty()) {
            meta.addItemFlags(guiItem.getFlags().toArray(ItemFlag[]::new));
        }

        if (guiItem.getCustomModelData() != null) {
            meta.setCustomModelData(guiItem.getCustomModelData());
        }
        item.setItemMeta(meta);

        return item;
    }
}