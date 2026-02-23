package uwu.nekorise.drQuests;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.drQuests.command.QuestAdminCommand;
import uwu.nekorise.drQuests.command.QuestAdminTabCompleter;
import uwu.nekorise.drQuests.command.QuestsCommand;
import uwu.nekorise.drQuests.config.ConfigManager;
import uwu.nekorise.drQuests.config.GuiConfigLoader;
import uwu.nekorise.drQuests.config.QuestConfigLoader;
import uwu.nekorise.drQuests.database.MongoManager;
import uwu.nekorise.drQuests.database.repository.MongoQuestRepository;
import uwu.nekorise.drQuests.database.repository.MongoStatsRepository;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.database.repository.StatsRepository;
import uwu.nekorise.drQuests.event.InventoryListener;
import uwu.nekorise.drQuests.event.JoinListener;
import uwu.nekorise.drQuests.event.QuestListener;
import uwu.nekorise.drQuests.gui.registry.GuiRegistry;
import uwu.nekorise.drQuests.gui.service.GuiService;
import uwu.nekorise.drQuests.placeholderapi.PapiExpansion;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;
import uwu.nekorise.drQuests.quest.service.QuestCacheService;
import uwu.nekorise.drQuests.quest.service.QuestService;
import uwu.nekorise.drQuests.quest.service.QuestStatsService;
import uwu.nekorise.drQuests.util.LogFilter;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DrQuests extends JavaPlugin {
    @Getter private static DrQuests instance;
    @Getter private ConfigManager configManager;
    @Getter private MongoManager mongoManager;
    @Getter private QuestRepository questRepository;
    @Getter private StatsRepository statsRepository;
    @Getter private GuiRegistry guiRegistry;
    @Getter private QuestRegistry questRegistry;
    @Getter private GuiService guiService;
    @Getter private QuestService questService;
    @Getter private QuestStatsService statsService;
    @Getter private QuestCacheService cacheService;
    private GuiConfigLoader guiLoader;
    private QuestConfigLoader questLoader;

    @Override
    public void onEnable() {
        instance = this;

        LogFilter.registerFilter();
        loadConfig();
        initMongodb();
        initRegistries();
        loadData();
        initServices();
        registerCommands();
        registerTabCompleter();
        registerListeners();
        hookPlaceholderAPI();
    }

    @Override
    public void onDisable() {
        if (mongoManager != null) {
            mongoManager.shutdown();
        }
    }

    private void loadConfig() {
        configManager = new ConfigManager(this);
        configManager.init();
    }

    private void initMongodb() {
        String uri = configManager.getMainConfig().getUri();

        mongoManager = new MongoManager(uri, "drquests");
        questRepository = new MongoQuestRepository(mongoManager);
        statsRepository = new MongoStatsRepository(mongoManager);
    }

    private void initRegistries() {
        guiRegistry = new GuiRegistry();
        questRegistry = new QuestRegistry();

        guiLoader = new GuiConfigLoader(configManager);
        questLoader = new QuestConfigLoader(configManager);
    }

    public void loadData() {
        guiLoader.load(guiRegistry);
        questLoader.load(questRegistry);
    }

    private void initServices() {
        statsService = new QuestStatsService(
                questRepository,
                questRegistry,
                statsRepository,
                this
        );

        cacheService = new QuestCacheService(
                this,
                questRepository,
                statsRepository
        );

        questService = new QuestService(
                questRegistry,
                questRepository,
                statsService,
                this,
                cacheService
        );

        guiService = new GuiService(
                guiRegistry,
                this,
                questRepository,
                statsRepository
        );
    }


    private void registerCommands() {
        getCommand("quests").setExecutor(new QuestsCommand(guiService, this));
        getCommand("questadmin").setExecutor(new QuestAdminCommand(this, questService, questRegistry, questLoader, configManager));
    }

    private void registerTabCompleter() {
        getCommand("questadmin")
                .setTabCompleter(new QuestAdminTabCompleter());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new InventoryListener(guiService), this);
        pm.registerEvents(new JoinListener(statsService, cacheService), this);
        pm.registerEvents(new QuestListener(questService), this);
    }

    private void hookPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansion(this).register();
        }
    }
}