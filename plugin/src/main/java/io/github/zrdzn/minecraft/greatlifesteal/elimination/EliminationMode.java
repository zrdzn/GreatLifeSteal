package io.github.zrdzn.minecraft.greatlifesteal.elimination;

public class EliminationMode {

    private final int requiredHealth;
    private final EliminationModeAction action;

    public EliminationMode(int requiredHealth, EliminationModeAction action) {
        this.requiredHealth = requiredHealth;
        this.action = action;
    }

    public int getRequiredHealth() {
        return this.requiredHealth;
    }

    public EliminationModeAction getAction() {
        return this.action;
    }

}
