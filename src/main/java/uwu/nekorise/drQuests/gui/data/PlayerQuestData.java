package uwu.nekorise.drQuests.gui.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestStats;

import java.util.Map;

@RequiredArgsConstructor
public class PlayerQuestData {
    @Getter private final OfflinePlayer player;
    private final Map<String, QuestProgress> progresses;
    @Getter private final QuestStats stats;

    public QuestProgress getProgress(String questId) {
        return progresses.get(questId);
    }
}
