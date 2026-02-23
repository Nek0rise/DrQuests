package uwu.nekorise.drQuests.gui.registry;

import uwu.nekorise.drQuests.gui.model.GuiDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GuiRegistry {
    private final Map<String, GuiDefinition> guis = new HashMap<>();

    public void register(GuiDefinition def) {
        if (guis.containsKey(def.getName())) {
            throw new IllegalStateException("GUI already exists: "  + def.getName());
        }
        guis.put(def.getName(), def);
    }

    public Optional<GuiDefinition> find(String name) {
        return Optional.ofNullable(guis.get(name));
    }

    public Collection<GuiDefinition> findAll() {
        return guis.values();
    }

    public void clear() {
        guis.clear();
    }
}
