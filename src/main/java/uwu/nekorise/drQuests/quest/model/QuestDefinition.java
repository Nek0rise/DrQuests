package uwu.nekorise.drQuests.quest.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import uwu.nekorise.drQuests.quest.model.target.QuestTarget;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class QuestDefinition {
    private final String questId;
    private final QuestTarget target;
    private final QuestType type;
    private final int requiredAmount;
    private final String rewardCommand;
    private final List<ItemStack> rewardItems;
}
