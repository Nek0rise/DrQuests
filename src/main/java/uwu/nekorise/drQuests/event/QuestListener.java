package uwu.nekorise.drQuests.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

    @EventHandler
    public void onExploring(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        Biome biome = event.getTo().getBlock().getBiome();
        questService.progress(
                event.getPlayer(),
                QuestType.EXPLORING,
                biome
        );
    }

    @EventHandler
    public void onRunning(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        double distance = event.getFrom().distance(event.getTo());
        if (distance <= 0D) return;

        questService.progress(
                event.getPlayer(),
                QuestType.RUNNING,
                distance
        );
    }
}
