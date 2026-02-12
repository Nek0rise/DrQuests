package uwu.nekorise.drQuests;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.drQuests.command.QuestsCommand;
import uwu.nekorise.drQuests.config.ConfigManager;

public final class DrQuests extends JavaPlugin {
    @Getter private static DrQuests instance;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.loadConfig();

        registerCommands();
        registerTabCompleters();
        registerListeners();
    }

    private void registerCommands(){
        getCommand("quests").setExecutor(new QuestsCommand());
    }

    private void registerTabCompleters(){

    }

    private void registerListeners(){

    }
}
