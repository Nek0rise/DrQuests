package uwu.nekorise.drQuests.gui.holder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class GuiHolder implements InventoryHolder {
    private final String guiName;

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
