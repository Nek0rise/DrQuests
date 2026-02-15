package uwu.nekorise.drQuests.gui.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class GuiItem {
    private final String questId;
    private final Material material;
    private final int index;
    private final String name;
    private final List<String> lore;
    private final List<ItemFlag> flags;
    private final Integer customModelData;
    //TODO private final List<GuiAction> actions;
}
