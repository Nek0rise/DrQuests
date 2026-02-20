package uwu.nekorise.drQuests.quest.model.target;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;

@Getter
@RequiredArgsConstructor
public class EntityTarget implements QuestTarget {
    private final EntityType entityType;

    @Override
    public boolean matches(Object target) {
        return target instanceof EntityType et && et == entityType;
    }
}
