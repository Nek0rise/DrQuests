package uwu.nekorise.drQuests.gui.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.gui.action.GuiAction;
import uwu.nekorise.drQuests.gui.action.GuiActionContext;
import uwu.nekorise.drQuests.gui.data.PlayerQuestData;
import uwu.nekorise.drQuests.gui.holder.GuiHolder;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;
import uwu.nekorise.drQuests.quest.model.QuestProgress;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiService {
    private final GuiRegistry guiRegistry;
    private final GuiRenderer guiRenderer;
    private final DrQuests drQuests;
    private final QuestRepository questRepository;

    public GuiService(GuiRegistry guiRegistry, DrQuests drQuests, QuestRepository questRepository) {
        this.guiRegistry = guiRegistry;
        this.drQuests = drQuests;
        this.questRepository = questRepository;
        this.guiRenderer = new GuiRenderer(drQuests.getQuestRegistry());
    }

    public void open(String name, Player player) {
        GuiDefinition def = guiRegistry.find(name).orElse(null);
        if (def == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(drQuests, () -> {
            List<QuestProgress> progresses = questRepository.findAll(player.getName().toLowerCase());
            Map<String, QuestProgress> map = progresses.stream()
                    .collect(Collectors.toMap(
                            QuestProgress::getQuestId,
                            p -> p
                    ));

            PlayerQuestData playerQuestData = new PlayerQuestData(player, map);

            Bukkit.getScheduler().runTask(drQuests, () -> {
                Inventory inventory = guiRenderer.render(def, playerQuestData);
                player.openInventory(inventory);
            });
        });
    }

    public void refresh(Player player) {
        if (player.getOpenInventory() == null) return;

        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory == null) return;
        if (!(inventory.getHolder() instanceof GuiHolder holder)) return;
        open(holder.getGuiName(), player);
    }

    public void executeAction(Player player, String guiName, int clickedSlot) {
        GuiDefinition def = guiRegistry.find(guiName).orElseThrow(() -> new RuntimeException("GUI not found: " + guiName));

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


