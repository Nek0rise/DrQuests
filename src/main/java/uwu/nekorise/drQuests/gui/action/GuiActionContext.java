package uwu.nekorise.drQuests.gui.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import uwu.nekorise.drQuests.gui.service.GuiService;

@Getter
@RequiredArgsConstructor
public class GuiActionContext {
    private final Player player;
    private final GuiService guiService;
}
