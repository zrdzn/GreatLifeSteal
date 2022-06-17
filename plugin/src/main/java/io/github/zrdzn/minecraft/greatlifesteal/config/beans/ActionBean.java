package io.github.zrdzn.minecraft.greatlifesteal.config.beans;

import io.github.zrdzn.minecraft.greatlifesteal.action.Action;
import java.util.Collections;
import java.util.List;

public class ActionBean {

    private boolean enabled = false;
    private Action type = Action.DISPATCH_COMMANDS;
    private int activateAtHealth = 4;
    private List<String> parameters = Collections.singletonList("gamemode spectator {victim}");

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Action getType() {
        return this.type;
    }

    public void setType(Action type) {
        this.type = type;
    }

    public int getActivateAtHealth() {
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
