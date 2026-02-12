package uwu.nekorise.drQuests.gui;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class QuestsGUI implements InventoryHolder {
    private Inventory inventory;
    public QuestsGUI(Component name, int size) {
        this.inventory = Bukkit.createInventory(this, size, name);
    }
}
