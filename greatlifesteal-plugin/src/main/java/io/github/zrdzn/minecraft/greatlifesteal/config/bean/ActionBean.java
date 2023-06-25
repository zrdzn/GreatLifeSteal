package io.github.zrdzn.minecraft.greatlifesteal.config.bean;

import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import java.util.Collections;
import java.util.List;

public class ActionBean {

    private boolean enabled = false;
    private ActionType type = ActionType.DISPATCH_COMMANDS;
    private double activateAtHealth = 4.0D;
    private long delay = 5L;
    private List<String> parameters = Collections.singletonList("gamemode spectator {victim}");
    private ReviveBean revive = BeanBuilder.from(ReviveBean.class).build();

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

    public ReviveBean getRevive() {
        return this.revive;
    }

    public void setRevive(ReviveBean revive) {
        this.revive = revive;
    }

}
