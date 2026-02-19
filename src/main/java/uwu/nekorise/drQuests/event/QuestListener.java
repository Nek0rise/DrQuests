package uwu.nekorise.drQuests.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import uwu.nekorise.drQuests.quest.model.QuestType;
import uwu.nekorise.drQuests.quest.service.QuestService;

@RequiredArgsConstructor
public class QuestListener implements Listener {
    private final QuestService questService;

    @EventHandler
    public void onMining(BlockBreakEvent event) {
        questService.progress(
                event.getPlayer(),
                QuestType.MINING,
                event.getBlock().getType()
        );
    }

    @EventHandler
    public void onKilling(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        questService.progress(
                event.getEntity().getKiller(),
                QuestType.KILLING,
                event.getEntity().getType()
        );
    }
}
