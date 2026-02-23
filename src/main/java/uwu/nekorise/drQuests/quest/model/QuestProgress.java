package uwu.nekorise.drQuests.quest.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class QuestProgress {
    private final String nickname;
    private final String questId;
    private final int progress;
    private final boolean isCompleted;
    private final List<String> visitedBiomes;
}
