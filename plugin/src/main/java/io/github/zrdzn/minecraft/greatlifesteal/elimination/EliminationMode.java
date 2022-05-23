package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Collections;
import java.util.List;

public class EliminationMode {

    private final int requiredHealth;
    private final EliminationModeAction action;
    private final List<String> actionCommands;

    public EliminationMode(int requiredHealth, EliminationModeAction action, List<String> actionCommands) {
        this.requiredHealth = requiredHealth;
        this.action = action;
        this.actionCommands = actionCommands;
    }

    public int getRequiredHealth() {
        return this.requiredHealth;
    }

    public EliminationModeAction getAction() {
        return this.action;
    }

    public List<String> getActionCommands() {
        return Collections.unmodifiableList(this.actionCommands);
    }

}
