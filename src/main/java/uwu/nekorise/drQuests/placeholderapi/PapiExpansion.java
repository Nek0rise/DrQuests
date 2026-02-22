package uwu.nekorise.drQuests.placeholderapi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uwu.nekorise.drQuests.DrQuests;
import uwu.nekorise.drQuests.gui.data.PlayerQuestData;
import uwu.nekorise.drQuests.quest.model.QuestProgress;

@RequiredArgsConstructor
public class PapiExpansion extends PlaceholderExpansion {
    private final DrQuests drQuests;

    @Override
    public @NotNull String getIdentifier() {
        return "quests";
    }

    @Override
    public @NotNull String getAuthor() {
        return drQuests.getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getVersion() {
        return drQuests.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "0";
        }

        String nickname = player.getName();

        if (params.equals("completed_total")) {

            PlayerQuestData questData = drQuests.getCacheService().get(nickname);
            return questData != null ? String.valueOf(questData.getStats().getTotalCompleted()) : "0";
        }

        if (params.startsWith("progress_")) {
            String questId = params.substring("progress_".length());

            if (questId.isEmpty()) {
                return "0";
            }

            PlayerQuestData questData = drQuests.getCacheService().get(nickname);
            if (questData == null) {
                return "0";
            }

            QuestProgress progress = questData.getProgress(questId);
            return progress != null ? String.valueOf(progress.getProgress()) : "0";
        }
        return null;
    }
}
