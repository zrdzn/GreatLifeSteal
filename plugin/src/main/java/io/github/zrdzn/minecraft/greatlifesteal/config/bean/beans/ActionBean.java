package io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans;

import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import java.util.Collections;
import java.util.List;

public class ActionBean {

    private boolean enabled = false;
    private ActionType type = ActionType.DISPATCH_COMMANDS;
    private double activateAtHealth = 4;
    private List<String> parameters = Collections.singletonList("gamemode spectator {victim}");

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

    public void setActivateAtHealth(int activateAtHealth) {
        this.activateAtHealth = activateAtHealth;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

}
