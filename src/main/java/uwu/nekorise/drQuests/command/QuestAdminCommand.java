package uwu.nekorise.drQuests.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.config.ConfigManager;
import uwu.nekorise.drQuests.config.QuestConfigLoader;
import uwu.nekorise.drQuests.quest.model.QuestDefinition;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;
import uwu.nekorise.drQuests.quest.service.QuestService;
import uwu.nekorise.drQuests.util.HEX;
import uwu.nekorise.drQuests.util.PlaceholderUtil;

@RequiredArgsConstructor
public class QuestAdminCommand implements CommandExecutor {

    private final DrQuests drQuests;
    private final QuestService questService;
    private final QuestRegistry questRegistry;
    private final QuestConfigLoader questConfigLoader;
    private final ConfigManager configManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("quests.admin")) {
            sender.sendMessage(HEX.applyColor(
                    drQuests.getConfigManager().getLangConfig().getNoPermission()
            ));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setprogress" -> setProgress(sender, args);
            case "reset" -> reset(sender, args);
            case "reload" -> reload(sender);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(HEX.applyColor(
                drQuests.getConfigManager().getLangConfig().getQuestAdminUsage()
        ));
    }

    private void setProgress(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(HEX.applyColor(
                    drQuests.getConfigManager().getLangConfig().getSetProgressUsage()
            ));
            return;
        }

        int value;
        try {
            value = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(HEX.applyColor(
                    drQuests.getConfigManager().getLangConfig().getSetProgressInvalidValue()
            ));
            return;
        }

        executeSetProgress(sender, args[1], args[2], value);
    }

    private void reset(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(HEX.applyColor(
                    drQuests.getConfigManager().getLangConfig().getResetUsage()
            ));
            return;
        }

        executeSetProgress(sender, args[1], args[2], 0);
    }

    private void executeSetProgress(CommandSender sender, String playerName, String questId, int value) {
        String nickname = playerName.toLowerCase();

        QuestDefinition questDef = questRegistry.find(questId).orElse(null);
        if (questDef == null) {
            String msg = HEX.applyColor(
                    PlaceholderUtil.parseLang(
                            drQuests.getConfigManager().getLangConfig().getQuestNotFound(),
                            questId,
                            String.valueOf(value)
                    ));
            sender.sendMessage(msg);
            return;
        }

        questService.setProgress(nickname, questDef, value);

        String msg = HEX.applyColor(
                PlaceholderUtil.parseLang(
                        drQuests.getConfigManager().getLangConfig().getSuccSet(),
                        questId,
                        String.valueOf(value)
                ));
        sender.sendMessage(msg);
    }

    private void reload(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(drQuests, () -> {
            drQuests.reloadConfig();
            configManager.init();
            drQuests.loadData();

            questRegistry.clear();
            questConfigLoader.load(questRegistry);

            Bukkit.getScheduler().runTask(drQuests, () -> sender.sendMessage(
                    HEX.applyColor(drQuests.getConfigManager().getLangConfig().getReloadSucc())
            ));
        });
    }
}