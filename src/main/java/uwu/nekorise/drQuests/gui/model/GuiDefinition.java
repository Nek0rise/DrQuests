package uwu.nekorise.drQuests.gui.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class GuiDefinition {
    private final String name;
    private final int size;
    private final String title;
    private final List<GuiItem> items;
}
