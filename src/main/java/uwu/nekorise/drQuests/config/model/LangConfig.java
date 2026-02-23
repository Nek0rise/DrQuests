package uwu.nekorise.drQuests.config.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LangConfig {
    private final String noPermission;
    private final String onlyPlayers;
    private final String completedStatus;
    private final String inProgressStatus;
    private final String questAdminUsage;
    private final String setProgressUsage;
    private final String setProgressInvalidValue;
    private final String resetUsage;
    private final String reloadSucc;
    private final String questNotFound;
    private final String succSet;
}
