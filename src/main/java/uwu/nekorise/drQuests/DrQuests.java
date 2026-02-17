package uwu.nekorise.drQuests;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.drQuests.command.QuestsCommand;
import uwu.nekorise.drQuests.config.ConfigManager;
import uwu.nekorise.drQuests.database.MongoManager;
import uwu.nekorise.drQuests.database.repository.MongoQuestRepository;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.event.InventoryListener;
import uwu.nekorise.drQuests.gui.config.GuiConfigLoader;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;
import uwu.nekorise.drQuests.gui.service.GuiService;

public final class DrQuests extends JavaPlugin {
    @Getter private static DrQuests instance;

    @Getter private GuiRegistry guiRegistry;
    @Getter private GuiService guiService;

    @Getter private MongoManager mongoManager;
    @Getter private QuestRepository questRepository;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager configManager = new ConfigManager(instance);
        configManager.init();
        guiRegistry = new GuiRegistry();
        GuiConfigLoader loader = new GuiConfigLoader(configManager);
        loader.load(guiRegistry);
        guiService = new GuiService(guiRegistry);

        initMongoDB();

        registerCommands();
        registerTabCompleters();
        registerListeners();
    }

    @Override
    public void onDisable() {
        if (mongoManager != null) mongoManager.shutdown();
    }

    private void registerCommands() {
        getCommand("quests").setExecutor(new QuestsCommand(guiService, questRepository));
    }

    private void registerTabCompleters() {
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new InventoryListener(guiService), instance);
    }

    private void initMongoDB() {
        mongoManager = new MongoManager("mongodb://localhost:27017", "drquests");
        questRepository = new MongoQuestRepository(mongoManager);
    }
}
