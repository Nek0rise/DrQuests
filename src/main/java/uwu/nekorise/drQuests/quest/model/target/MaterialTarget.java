package uwu.nekorise.drQuests.quest.model.target;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public class MaterialTarget implements QuestTarget {
    private final Material material;

    @Override
    public boolean matches(Object target) {
        return target instanceof Material mat && mat == material;
    }
}
