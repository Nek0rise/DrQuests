package uwu.nekorise.drQuests.quest.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.StatsRepository;

@RequiredArgsConstructor
public class QuestStatsService {
    private final StatsRepository statsRepository;
    private final DrQuests drQuests;

    public void createStatsOnJoin(String nickname) {
        Bukkit.getScheduler().runTaskAsynchronously(drQuests, () -> {
           statsRepository.createIfNone(nickname);
        });
    }
}
