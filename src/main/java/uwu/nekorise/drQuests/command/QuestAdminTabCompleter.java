package uwu.nekorise.drQuests.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class QuestAdminTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("quests.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("setprogress", "reset", "reload");
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("setprogress") || sub.equals("reset")) {
                return List.of("nickname");
            }
        }

        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("setprogress") || sub.equals("reset")) {
                return List.of("questId");
            }
        }

        if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("setprogress")) {
                return List.of("value");
            }
        }
        return List.of();
    }
}