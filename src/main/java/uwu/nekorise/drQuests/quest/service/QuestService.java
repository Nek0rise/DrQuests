package uwu.nekorise.drQuests.quest.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.quest.model.QuestDefinition;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestType;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class QuestService {
    private final QuestRegistry questRegistry;
    private final QuestRepository questRepository;
    private final QuestStatsService statsService;
    private final DrQuests instance;
    private final Map<String, Double> runningBuffer = new ConcurrentHashMap<>();

    public void progress(Player player, QuestType type, Object target) {
        String nickname = player.getName().toLowerCase();

        for (QuestDefinition questDef : questRegistry.findAll()) {
            if (questDef.getType() != type) continue;

            switch (type) {
                case EXPLORING -> {
                    String biomeKey = ((Biome) target).getKey().toString();
                    exploringProgress(nickname, questDef, biomeKey);
                }
                case RUNNING -> {
                    double distance = (double) target;
                    if (distance <= 0) continue;
                    runningProgress(nickname, questDef, distance);
                }
                default -> {
                    if (!questDef.getTarget().matches(target)) continue;
                    incrementProgress(nickname, questDef);
                }
            }
        }
    }

    private void incrementProgress(String nickname, QuestDefinition questDef) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            questRepository.addProgress(
                    nickname,
                    questDef.getQuestId(),
                    1
            );

            QuestProgress progress =
                    questRepository.find(
                            nickname,
                            questDef.getQuestId()
                    ).orElse(null);

            if (progress == null || progress.isCompleted()) return;
            if (progress.getProgress() >= questDef.getRequiredAmount()) {
                finish(nickname, questDef);
            }
        });
    }

    private void runningProgress(String nickname, QuestDefinition questDef, double distance) {
        runningBuffer.merge(nickname, distance, Double::sum);
        double buffered = runningBuffer.get(nickname);
        if (buffered < 1.0) return;
        int blocks = (int) buffered;

        runningBuffer.put(nickname, buffered - blocks);

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            questRepository.addProgress(
                    nickname,
                    questDef.getQuestId(),
                    blocks
            );

            QuestProgress progress =
                    questRepository.find(
                            nickname,
                            questDef.getQuestId()
                    ).orElse(null);

            if (progress == null || progress.isCompleted()) return;
            if (progress.getProgress() >= questDef.getRequiredAmount()) {
                finish(nickname, questDef);
            }
        });
    }

    private void exploringProgress(String nickname, QuestDefinition questDef, String biomeKey) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            questRepository.addBiome(
                    nickname,
                    questDef.getQuestId(),
                    biomeKey
            );

            QuestProgress progress =
                    questRepository.find(
                            nickname,
                            questDef.getQuestId()
                    ).orElse(null);

            if (progress == null || progress.isCompleted()) return;

            int currentProgress = progress.getVisitedBiomes().size();
            if (currentProgress >= questDef.getRequiredAmount()) {
                finish(nickname, questDef);
            }
        });
    }

    public void finish(String nickname, QuestDefinition questDef) {
        questRepository.setCompleted(nickname, questDef.getQuestId());
        statsService.refresh(nickname);
        giveRewards(nickname, questDef);
    }

    private void giveRewards(String nickname, QuestDefinition questDef) {
        Bukkit.getScheduler().runTask(instance, () -> {
            if (questDef.getRewardCommand() != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), questDef.getRewardCommand());
            }
            Player player = Bukkit.getPlayer(nickname);
            if (player != null && player.isOnline()) {
                player.give(questDef.getRewardItems());
            }
        });
    }
}