package uwu.nekorise.drQuests.gui.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.database.repository.StatsRepository;
import uwu.nekorise.drQuests.gui.action.GuiAction;
import uwu.nekorise.drQuests.gui.action.GuiActionContext;
import uwu.nekorise.drQuests.gui.data.PlayerQuestData;
import uwu.nekorise.drQuests.gui.holder.GuiHolder;
import uwu.nekorise.drQuests.gui.model.GuiDefinition;
import uwu.nekorise.drQuests.gui.model.GuiItem;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestStats;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiService {
    private final GuiRegistry guiRegistry;
    private final GuiRenderer guiRenderer;
    private final DrQuests drQuests;
    private final QuestRepository questRepository;
    private final StatsRepository statsRepository;

    public GuiService(GuiRegistry guiRegistry, DrQuests drQuests, QuestRepository questRepository, StatsRepository statsRepository) {
        this.guiRegistry = guiRegistry;
        this.drQuests = drQuests;
        this.questRepository = questRepository;
        this.guiRenderer = new GuiRenderer(drQuests.getQuestRegistry());
        this.statsRepository = statsRepository;
    }

    public void open(String name, Player player) {
        GuiDefinition def = guiRegistry.find(name).orElse(null);

        if (def == null) return;
        String nickname = player.getName().toLowerCase();

        Bukkit.getScheduler().runTaskAsynchronously(drQuests, () -> {
            List<QuestProgress> progresses = questRepository.findAll(nickname);
            Map<String, QuestProgress> progressMap =
                    progresses.stream()
                            .collect(Collectors.toMap(
                                    QuestProgress::getQuestId,
                                    p -> p
                            ));

            QuestStats stats = statsRepository.find(nickname)
                    .orElse(new QuestStats(nickname, 0));
            PlayerQuestData context = new PlayerQuestData(
                    Bukkit.getOfflinePlayer(nickname),
                    progressMap,
                    stats
            );

            Bukkit.getScheduler().runTask(drQuests, () -> {
                Inventory inventory = guiRenderer.render(def, context);
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


