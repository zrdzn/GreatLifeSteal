package io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans;

import java.util.Collections;
import java.util.List;

public class ReviveBean {

    private boolean enabled = false;
    private List<String> commands = Collections.emptyList();

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