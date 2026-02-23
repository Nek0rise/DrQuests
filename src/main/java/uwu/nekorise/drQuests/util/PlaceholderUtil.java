package uwu.nekorise.drQuests.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.config.model.LangConfig;
import uwu.nekorise.drQuests.gui.data.PlayerQuestData;
import uwu.nekorise.drQuests.quest.model.QuestDefinition;
import uwu.nekorise.drQuests.quest.model.QuestProgress;
import uwu.nekorise.drQuests.quest.model.QuestType;

public class PlaceholderUtil {
    public static String parse(String text, PlayerQuestData playerQuestData, QuestDefinition quest) {
        if (text == null) return null;

        OfflinePlayer player = playerQuestData.getPlayer();
        String editedText = text.replace("%player%", player.getName());

        if (quest != null) {
            QuestProgress progress = playerQuestData.getProgress(quest.getQuestId());

            int current = 0;
            boolean completed = false;

            if (progress != null) {
                completed = progress.isCompleted();
                if (quest.getType() == QuestType.EXPLORING) {
                    current = progress.getVisitedBiomes().size();
                } else {
                    current = progress.getProgress();
                }
            }

            int required = quest.getRequiredAmount();
            int percentage = required == 0 ? 0 : (int)((current * 100.0) / required);
            LangConfig langConfig = DrQuests.getInstance().getConfigManager().getLangConfig();

            editedText = editedText
                    .replace("%progress%", String.valueOf(current))
                    .replace("%required%", String.valueOf(required))
                    .replace("%percentage%", String.valueOf(percentage))
                    .replace("%status%", completed ? langConfig.getCompletedStatus() : langConfig.getInProgressStatus());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            editedText = PlaceholderAPI.setPlaceholders(player, editedText);
        }

        return editedText;
    }
    public static String parseCommand(String text, String nickname) {
        return text.replace("%player%", nickname);
    }
    public static String parseLang(String text, String questId, String value) {
        return text
                .replace("%questId%", questId)
                .replace("%value%", value);
    }
}