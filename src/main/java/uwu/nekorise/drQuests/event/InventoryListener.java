package uwu.nekorise.drQuests.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import uwu.nekorise.drQuests.gui.holder.GuiHolder;
import uwu.nekorise.drQuests.gui.service.GuiService;

@RequiredArgsConstructor
public class InventoryListener implements Listener {
    private final GuiService guiService;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder holder)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        event.setCancelled(true);
        guiService.executeAction(player, holder.getGuiName(), event.getRawSlot());
    }
}
