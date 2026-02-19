package uwu.nekorise.drQuests.quest.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.database.repository.StatsRepository;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestStats;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;

import java.util.List;

@RequiredArgsConstructor
public class QuestStatsService {
    private final QuestRepository questRepository;
    private final QuestRegistry questRegistry;
    private final StatsRepository statsRepository;
    private final DrQuests drQuests;

    public void createStatsOnJoin(String nickname) {
        Bukkit.getScheduler().runTaskAsynchronously(drQuests, () -> {
           statsRepository.createIfNone(nickname);
        });
    }

    public void refresh(String nickname) {
        Bukkit.getScheduler().runTaskAsynchronously(drQuests, () -> {
            List<QuestProgress> progresses = questRepository.findAll(nickname);
            int total = 0;

            for (QuestProgress progress : progresses) {
                if (!progress.isCompleted()) continue;
                if (!questRegistry.exists(progress.getQuestId())) continue;
                total++;
            }

            statsRepository.save(new QuestStats(nickname, total));
        });
    }
}
