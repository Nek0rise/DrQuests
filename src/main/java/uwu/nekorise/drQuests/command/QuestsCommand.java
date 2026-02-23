package uwu.nekorise.drQuests.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.gui.service.GuiService;
import uwu.nekorise.drQuests.util.HEX;

@RequiredArgsConstructor
public class QuestsCommand implements CommandExecutor {
    private final GuiService guiService;
    private final DrQuests drQuests;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(drQuests.getConfigManager().getLangConfig().getOnlyPlayers());
            return true;
        }

        if (!player.hasPermission("quests.player")) {
            sender.sendMessage(HEX.applyColor(
                    drQuests.getConfigManager().getLangConfig().getNoPermission()
            ));
            return true;
        }

        guiService.open(drQuests.getConfigManager().getMainConfig().getMainGui(), player);
        return true;
    }
}