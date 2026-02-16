package uwu.nekorise.drQuests.gui.action;

public class RefreshGuiAction implements GuiAction {
    @Override
    public void execute(GuiActionContext context) {
        context.getGuiService().refresh(context.getPlayer());
    }
}
