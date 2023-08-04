package io.github.zrdzn.minecraft.greatlifesteal.elimination.revive;

import java.util.Collections;
import java.util.List;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class ReviveConfig extends OkaeriConfig {

    public static class ReviveCommandConfig extends OkaeriConfig {

        @Comment("List of commands that should be executed when the victim is revived but has not joined yet.")
        private List<String> initial = Collections.singletonList("pardon {victim}");

        @Comment("")
        @Comment("List of commands that should be executed on first join of the victim after revive.")
        private List<String> after = Collections.singletonList("say {victim} has been revived!");

        public List<String> getInitial() {
            return this.initial;
        }

        public void setInitial(List<String> initial) {
            this.initial = initial;
        }

        public List<String> getAfter() {
            return this.after;
        }

        public void setAfter(List<String> after) {
            this.after = after;
        }

    }

    private ReviveCommandConfig commands = new ReviveCommandConfig();

    public ReviveCommandConfig getCommands() {
        return this.commands;
    }

    public void setCommands(ReviveCommandConfig commands) {
        this.commands = commands;
    }

}
