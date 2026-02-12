package uwu.nekorise.drQuests.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uwu.nekorise.drQuests.gui.GUIBuilder;

public class QuestsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        sender.sendMessage("Quests executing");

        Player player = (Player) sender;
        GUIBuilder guiBuilder = new GUIBuilder();

        player.openInventory(guiBuilder.build().getInventory());

        return false;
    }
}
