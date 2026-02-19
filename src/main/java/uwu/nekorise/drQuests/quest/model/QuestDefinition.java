package uwu.nekorise.drQuests.quest.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uwu.nekorise.drQuests.quest.model.target.QuestTarget;

@Getter
@RequiredArgsConstructor
public class QuestDefinition {
    private final String questId;
    private final QuestTarget target;
    private final QuestType type;
    private final int requiredAmount;
}
