package io.github.zrdzn.minecraft.greatlifesteal.action;

import java.util.List;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class ActionConfig extends OkaeriConfig {

    @Comment("Action type that should be taken upon reaching the health goal.")
    @Comment(" COMMAND - execute a list of commands as a console.")
    @Comment(" BROADCAST - broadcast a message that is specified in the parameters list.")
    private ActionType type;

    @Comment("Amount of health points that are needed to execute the action.")
    private double activateAtHealth;

    @Comment("Delay in ticks after which the action should be executed.")
    @Comment("Very low values such as 0 or 1 may lead to performance issues, so choose wisely.")
    @Comment("20 ticks = 1 second.")
    private long delay;

    @Comment("List of parameters that are adequate to the chosen action:")
    @Comment(" COMMAND - list of commands.")
    @Comment(" BROADCAST - list of messages.")
    private List<String> parameters;

    public ActionConfig(ActionType type, List<String> parameters) {
        this(type, 4.0D, 5L, parameters);
    }

    public ActionConfig(ActionType type, double activateAtHealth, long delay, List<String> parameters) {
        this.type = type;
        this.activateAtHealth = activateAtHealth;
        this.delay = delay;
        this.parameters = parameters;
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

}
