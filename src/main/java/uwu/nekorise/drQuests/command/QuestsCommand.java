package uwu.nekorise.drQuests.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.database.repository.QuestRepository;
import uwu.nekorise.drQuests.gui.service.GuiService;
import uwu.nekorise.drQuests.quest.model.QuestDefinition;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.registry.QuestRegistry;
import uwu.nekorise.drQuests.util.HEX;

import java.util.Optional;

@RequiredArgsConstructor
public class QuestsCommand implements CommandExecutor {

    private final GuiService guiService;
    private final QuestRepository repository;
    private final QuestRegistry qregistry;

    // FIXME
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        // /quests
        if (args.length == 0) {
            guiService.open("quests", player);
            return true;
        }

        // /quests list
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("quests list:");
            for (QuestDefinition def : qregistry.findAll()) {
                sender.sendMessage(HEX.applyColor("\n&6questId: &4" + def.getQuestId() + "&6, target: " + def.getTarget() + "&6, type: &4" + def.getType()));
            }
            return true;
        }

        // /quests add <questId>
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {

            String questId = args[1];

            Bukkit.getScheduler().runTaskAsynchronously(DrQuests.getInstance(), () -> {
                String nickname = player.getName().toLowerCase();
                Optional<QuestProgress> optional = repository.find(nickname, questId);
                QuestProgress progress;
                if (optional.isPresent()) {
                    QuestProgress old = optional.get();
                    progress = new QuestProgress(
                            nickname,
                            questId,
                            old.getProgress() + 1,
                            old.isCompleted()
                    );
                } else {
                    progress = new QuestProgress(
                            nickname,
                            questId,
                            1,
                            false
                    );
                }
                repository.save(progress);

                player.sendMessage("Progress +1 for quest: " + questId);
            });

            return true;
        }
        return true;
    }
}
