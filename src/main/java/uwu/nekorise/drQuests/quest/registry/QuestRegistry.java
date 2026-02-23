package uwu.nekorise.drQuests.quest.registry;

import uwu.nekorise.drQuests.quest.model.QuestDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestRegistry {
    private final Map<String, QuestDefinition> quests = new HashMap<>();

    public void register(QuestDefinition def) {
        if (quests.containsKey(def.getQuestId())) {
            throw new IllegalStateException("Quest already exists: " + def.getQuestId());
        }
        quests.put(def.getQuestId(), def);
    }

    public Optional<QuestDefinition> find(String questId) {
        return Optional.ofNullable(quests.get(questId));
    }

    public Collection<QuestDefinition> findAll() {
        return quests.values();
    }

    public boolean exists(String questId) {
        return quests.containsKey(questId);
    }

    public void clear() {
        quests.clear();
    }
}
