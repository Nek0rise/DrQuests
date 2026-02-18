package uwu.nekorise.drQuests.database.repository;

import uwu.nekorise.drQuests.quest.model.QuestStats;

import java.util.Optional;

public interface StatsRepository {
    void createIfNone(String nickname);
    void save(QuestStats stats);
    void delete(String nickname);
    Optional<QuestStats> find(String nickname);
}
