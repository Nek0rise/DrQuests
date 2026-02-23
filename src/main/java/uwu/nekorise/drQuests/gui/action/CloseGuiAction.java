package uwu.nekorise.drQuests.gui.action;

public class CloseGuiAction implements GuiAction {
    @Override
    public void execute(GuiActionContext context) {
        context.getPlayer().closeInventory();
    }
}
