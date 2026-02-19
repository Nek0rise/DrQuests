package uwu.nekorise.drQuests.quest.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.quest.model.QuestDefinition;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestType;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;

@RequiredArgsConstructor
public class QuestService {
    private final QuestRegistry questRegistry;
    private final QuestRepository questRepository;
    private final QuestStatsService statsService;
    private final DrQuests instance;

    public void progress(Player player, QuestType type, Object target) {
        String nickname = player.getName().toLowerCase();
        for (QuestDefinition questDef : questRegistry.findAll()) {
            if (questDef.getType() != type) continue;
            if (!questDef.getTarget().matches(target)) continue;
            incrementProgress(nickname, questDef);
        }
    }

    public void incrementProgress(String nickname, QuestDefinition questDef) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            questRepository.addProgress(nickname, questDef.getQuestId(), 1);

            QuestProgress progress = questRepository.find(nickname, questDef.getQuestId()).orElseThrow();
            if (!progress.isCompleted() && progress.getProgress() >= questDef.getRequiredAmount()) {
                questRepository.setCompleted(nickname, questDef.getQuestId());
                statsService.refresh(nickname);
            }
        });
    }
}
