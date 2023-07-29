package io.github.zrdzn.minecraft.greatlifesteal.elimination;

import java.util.Collections;
import java.util.List;

public class EliminationReviveConfig {

    private boolean enabled;
    private List<String> commands;

    public EliminationReviveConfig() {
        this(false);
    }

    public EliminationReviveConfig(boolean enabled) {
        this(enabled, Collections.emptyList());
    }

    public EliminationReviveConfig(boolean enabled, List<String> commands) {
        this.enabled = enabled;
        this.commands = commands;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

}
