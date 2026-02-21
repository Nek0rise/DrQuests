package uwu.nekorise.drQuests.gui.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import uwu.nekorise.drQuests.quest.model.QuestProgress;

import java.util.Map;

@RequiredArgsConstructor
public class PlayerQuestData {
    @Getter private final Player player;
    private final Map<String, QuestProgress> progresses;

    public QuestProgress getProgress(String questId) {
        return progresses.get(questId);
    }
}
