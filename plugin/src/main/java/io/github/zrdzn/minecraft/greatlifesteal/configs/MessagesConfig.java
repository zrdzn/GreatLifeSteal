package io.github.zrdzn.minecraft.greatlifesteal.configs;

import eu.okaeri.configs.OkaeriConfig;

public class MessagesConfig extends OkaeriConfig {

    public String commandUsage = "&aType /lifesteal set/reload/lives [player] [health_points]";

    public String noPermissions = "&cYou don't have enough permissions.";

    public String successfulCommandSet = "&aYou have successfully set &e{HEALTH} &ahp for &e{PLAYER}&a.";

    public String successfulCommandLives = "&aPlayer &e{PLAYER} &ahas &e{LIVES} &aleft.";

    public String eliminationNotEnabled = "&cEnable &eeliminationMode &csetting in order to use this command.";

    public String successfulCommandReload = "&aPlugin has been successfully reloaded.";

    public String failCommandReload = "&cCould not reload the plugin.";

    public String invalidPlayerProvided = "&cYou have provided invalid player.";

    public String invalidHealthProvided = "&cYou have provided invalid health number.";

    public String maxHealthReached = "&cYou have reached the maximum amount of health points.";

    public String pluginOutdated = "&eYou are using an outdated version of the plugin - please consider updating it on https://www.spigotmc.org/resources/greatlifesteal.102206/.";

}
