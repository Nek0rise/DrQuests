package uwu.nekorise.drQuests.gui.action;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenGuiAction implements GuiAction {
    private final String targetGuiName;

    @Override
    public void execute(GuiActionContext context) {
        context.getGuiService().open(targetGuiName, context.getPlayer());
    }
}
