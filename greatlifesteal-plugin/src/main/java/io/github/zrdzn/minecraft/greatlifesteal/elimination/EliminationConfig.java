package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.List;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class EliminationConfig extends OkaeriConfig {

    @Comment("Amount of health points that are needed to execute the elimination.")
    private double activateAtHealth;

    @Comment("Delay in ticks after which the action should be executed.")
    @Comment("Very low values such as 0 or 1 may lead to performance issues, so choose wisely.")
    @Comment("20 ticks = 1 second.")
    private long delay;

    @Comment("List of commands that should be executed upon elimination.")
    private List<String> commands;

    public EliminationConfig(List<String> commands) {
        this(4.0D, 5L, commands);
    }

    public EliminationConfig(double activateAtHealth, long delay, List<String> commands) {
        this.activateAtHealth = activateAtHealth;
        this.delay = delay;
        this.commands = commands;
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

    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

}
