package uwu.nekorise.drQuests.database.repository;

import uwu.nekorise.drQuests.quest.model.QuestProgress;

import java.util.List;
import java.util.Optional;

public interface QuestRepository {
    void save(QuestProgress progress);
    void delete(String nickname, String questId);
    Optional<QuestProgress> find(String nickname, String questId);
    List<QuestProgress> findAll(String nickname);
}
