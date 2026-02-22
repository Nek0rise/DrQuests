package uwu.nekorise.drQuests;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.drQuests.command.QuestsCommand;
import uwu.nekorise.drQuests.config.ConfigManager;
import uwu.nekorise.drQuests.config.QuestConfigLoader;
import uwu.nekorise.drQuests.database.MongoManager;
import uwu.nekorise.drQuests.database.repository.MongoQuestRepository;
import uwu.nekorise.drQuests.database.repository.MongoStatsRepository;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.database.repository.StatsRepository;
import uwu.nekorise.drQuests.event.InventoryListener;
import uwu.nekorise.drQuests.event.JoinListener;
import uwu.nekorise.drQuests.config.GuiConfigLoader;
import uwu.nekorise.drQuests.event.QuestListener;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;
import uwu.nekorise.drQuests.gui.service.GuiService;
import uwu.nekorise.drQuests.placeholderapi.PapiExpansion;
import uwu.nekorise.drQuests.quest.service.QuestCacheService;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;
import uwu.nekorise.drQuests.quest.service.QuestService;
import uwu.nekorise.drQuests.quest.service.QuestStatsService;

public final class DrQuests extends JavaPlugin {
    @Getter private static DrQuests instance;

    @Getter private GuiRegistry guiRegistry;
    @Getter private GuiService guiService;

    @Getter private QuestService questService;
    @Getter private QuestRegistry questRegistry;

    @Getter private MongoManager mongoManager;
    @Getter private QuestRepository questRepository;

    @Getter private StatsRepository statsRepository;
    @Getter private QuestStatsService statsService;

    @Getter private QuestCacheService cacheService;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager configManager = new ConfigManager(instance);
        configManager.init();
        guiRegistry = new GuiRegistry();
        questRegistry = new QuestRegistry();
        GuiConfigLoader guiLoader = new GuiConfigLoader(configManager);
        guiLoader.load(guiRegistry);
        QuestConfigLoader questLoader = new QuestConfigLoader(configManager);
        questLoader.load(questRegistry);
        initMongoDB();
        guiService = new GuiService(guiRegistry, instance, questRepository, statsRepository);
        cacheService = new QuestCacheService(instance, questRepository, statsRepository);
        questService = new QuestService(questRegistry, questRepository, statsService, instance, cacheService);

        registerCommands();
        registerTabCompleters();
        registerListeners();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansion(instance).register();
        }
    }

    @Override
    public void onDisable() {
        if (mongoManager != null) mongoManager.shutdown();
    }

    private void registerCommands() {
        getCommand("quests").setExecutor(new QuestsCommand(guiService, questRepository, questRegistry));
    }

    private void registerTabCompleters() {
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new InventoryListener(guiService), instance);
        pm.registerEvents(new JoinListener(statsService, cacheService), instance);
        pm.registerEvents(new QuestListener(questService), instance);
    }

    private void initMongoDB() {
        mongoManager = new MongoManager("mongodb://localhost:27017", "drquests");
        questRepository = new MongoQuestRepository(mongoManager);
        statsRepository = new MongoStatsRepository(mongoManager);
        statsService = new QuestStatsService(questRepository, questRegistry, statsRepository, instance);
    }
}
