package uwu.nekorise.drQuests.quest.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.database.repository.StatsRepository;
import uwu.nekorise.drQuests.gui.data.PlayerQuestData;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestStats;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QuestCacheService {
    private final DrQuests plugin;
    private final QuestRepository questRepository;
    private final StatsRepository statsRepository;
    private final Map<String, PlayerQuestData> cache = new ConcurrentHashMap<>();

    public PlayerQuestData get(String nickname) {
        return cache.get(nickname.toLowerCase());
    }

    public void remove(String nickname) {
        cache.remove(nickname.toLowerCase());
    }

    public void refresh(String nickname) {
        load(nickname.toLowerCase());
    }

    private void load(String nickname) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<QuestProgress> progresses = questRepository.findAll(nickname);
            Map<String, QuestProgress> progressMap = progresses.stream()
                    .collect(Collectors.toMap(
                            QuestProgress::getQuestId,
                            p -> p
                    ));

            QuestStats stats = statsRepository.find(nickname).orElse(new QuestStats(nickname, 0));
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(nickname);
            PlayerQuestData context = new PlayerQuestData(
                    offlinePlayer,
                    progressMap,
                    stats
            );

            Bukkit.getScheduler().runTask(plugin, () ->
                    cache.put(nickname, context)
            );
        });
    }
}