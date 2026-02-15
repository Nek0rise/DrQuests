package uwu.nekorise.drQuests;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.drQuests.command.QuestsCommand;
import uwu.nekorise.drQuests.config.ConfigManager;
import uwu.nekorise.drQuests.gui.config.GuiConfigLoader;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;
import uwu.nekorise.drQuests.gui.service.GuiService;


public final class DrQuests extends JavaPlugin {
    @Getter private static DrQuests instance;
    @Getter private GuiRegistry guiRegistry;
    @Getter private GuiService guiService;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager configManager = new ConfigManager(instance);
        configManager.init();
        guiRegistry = new GuiRegistry();
        GuiConfigLoader loader = new GuiConfigLoader(configManager);
        loader.load(guiRegistry);
        guiService = new GuiService(guiRegistry);

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
