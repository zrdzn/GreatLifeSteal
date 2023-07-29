package io.github.zrdzn.minecraft.greatlifesteal.action;

import java.util.List;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveConfig;

public class ActionConfig {

    private boolean enabled;
    private ActionType type;
    private double activateAtHealth;
    private long delay;
    private List<String> parameters;
    private EliminationReviveConfig revive;

    public ActionConfig(ActionType type, List<String> parameters, EliminationReviveConfig revive) {
        this(false, type, 4.0D, 5L, parameters, revive);
    }

    public ActionConfig(boolean enabled, ActionType type, List<String> parameters, EliminationReviveConfig revive) {
        this(enabled, type, 4.0D, 5L, parameters, revive);
    }

    public ActionConfig(boolean enabled, ActionType type, double activateAtHealth, long delay, List<String> parameters, EliminationReviveConfig revive) {
        this.enabled = enabled;
        this.type = type;
        this.activateAtHealth = activateAtHealth;
        this.delay = delay;
        this.parameters = parameters;
        this.revive = revive;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ActionType getType() {
        return this.type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public double getActivateAtHealth() {
        return this.activateAtHealth;
    }

    public void setActivateAtHealth(double activateAtHealth) {
        this.activateAtHealth = activateAtHealth;
    }

    public long getDelay() {
        return this.delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public EliminationReviveConfig getRevive() {
        return this.revive;
    }

    public void setRevive(EliminationReviveConfig revive) {
        this.revive = revive;
    }

}
