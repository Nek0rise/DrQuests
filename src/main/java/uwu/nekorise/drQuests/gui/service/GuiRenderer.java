package uwu.nekorise.drQuests.gui.service;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uwu.nekorise.drQuests.gui.holder.GuiHolder;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.util.HEX;

import java.util.stream.Collectors;

public class GuiRenderer {
    public Inventory render(GuiDefinition def) {
        Inventory inventory = Bukkit.createInventory(new GuiHolder(def.getName()), def.getSize(), HEX.applyColor(def.getTitle()));

        for (GuiItem guiItem : def.getItems()) {
            inventory.setItem(guiItem.getIndex(), createItemStack(guiItem));
        }

        return inventory;
    }

    private ItemStack createItemStack(GuiItem guiItem) {
        ItemStack item = new ItemStack(guiItem.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (guiItem.getName() != null) meta.setDisplayName(HEX.applyColor(guiItem.getName()));

        if (guiItem.getLore() != null) {
            meta.setLore(guiItem.getLore().stream()
                    .map(l -> HEX.applyColor(l))
                    .collect(Collectors.toList()));
        }

        if (guiItem.getFlags() != null && !guiItem.getFlags().isEmpty()) {
            meta.addItemFlags(guiItem.getFlags().toArray(ItemFlag[]::new));
        }

        if (guiItem.getCustomModelData() != null) meta.setCustomModelData(guiItem.getCustomModelData());

        item.setItemMeta(meta);

        return item;
    }
}
