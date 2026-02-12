package uwu.nekorise.drQuests.gui;

import uwu.nekorise.drQuests.config.MainConfigStorage;
import uwu.nekorise.drQuests.util.MMessage;

public class GUIBuilder {
    public QuestsGUI build(){
        QuestsGUI questsGUI = new QuestsGUI(
                MMessage.applyColor(MainConfigStorage.getGuiName()),
                MainConfigStorage.getGuiSize()
        );
        return questsGUI;
    }
}
