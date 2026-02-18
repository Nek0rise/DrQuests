package uwu.nekorise.drQuests.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uwu.nekorise.drQuests.quest.service.QuestStatsService;

@RequiredArgsConstructor
public class JoinListener implements Listener {
    private final QuestStatsService statsService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String nickname = event.getPlayer().getName();
        statsService.createStatsOnJoin(nickname);
    }
}
