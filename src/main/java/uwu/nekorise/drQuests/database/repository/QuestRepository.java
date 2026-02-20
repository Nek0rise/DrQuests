package uwu.nekorise.drQuests.database.repository;

import uwu.nekorise.drQuests.quest.model.QuestProgress;

import java.util.List;
import java.util.Optional;

public interface QuestRepository {
    void save(QuestProgress progress);
    void addProgress(String nickname, String questId, int value);
    void addBiome(String nickname, String questId, String biomeKey);
    void setCompleted(String nickname, String questId);
    void delete(String nickname, String questId);
    Optional<QuestProgress> find(String nickname, String questId);
    List<QuestProgress> findAll(String nickname);
}
