package uwu.nekorise.drQuests.gui.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import uwu.nekorise.drQuests.gui.action.GuiAction;
import uwu.nekorise.drQuests.gui.action.GuiActionContext;
import uwu.nekorise.drQuests.gui.holder.GuiHolder;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;

@RequiredArgsConstructor
public class GuiService {
    private final GuiRegistry guiRegistry;
    private final GuiRenderer guiRenderer = new GuiRenderer();

    public void open(String guiName, Player player) {
        GuiDefinition def = guiRegistry.find(guiName)
                .orElseThrow(() -> new RuntimeException("GUI not found: " + guiName));

        Inventory inventory = guiRenderer.render(def);
        player.openInventory(inventory);
    }

    public void refresh(Player player) {
        if (player.getOpenInventory() == null) return;

        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory == null) return;
        if (!(inventory.getHolder() instanceof GuiHolder holder)) return;
        open(holder.getGuiName(), player);
    }

    public void executeAction(Player player, String guiName, int clickedSlot) {
        GuiDefinition def = guiRegistry.find(guiName)
                .orElseThrow(() -> new RuntimeException("GUI not found: " + guiName));

        if (def == null) return;

        for (GuiItem item : def.getItems()) {
            if (item.getIndex() == clickedSlot) {
                GuiActionContext context = new GuiActionContext(player, this);
                for (GuiAction action : item.getActions()) {
                    action.execute(context);
                }
                break;
            }
        }
    }
}


