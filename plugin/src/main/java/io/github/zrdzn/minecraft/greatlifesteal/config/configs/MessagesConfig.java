package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import eu.okaeri.configs.OkaeriConfig;

public class MessagesConfig extends OkaeriConfig {

    public String commandUsage = "&a&lAvailable commands:\n" +
        "&7/&alifesteal reload\n" +
        "&7/&alifesteal set <player> [health_points]\n" +
        "&7/&alifesteal lives <action> [player]";

    public String noPermissions = "&cYou don't have enough permissions.";

    public String successfulCommandSet = "&aYou have successfully set &e{HEALTH} &ahp for &e{PLAYER}&a.";

    public String successfulCommandLives = "&aPlayer &e{PLAYER} &ahas &e{LIVES} lives &aleft.";

    public String eliminationNotEnabled = "&cEnable the &eeliminationMode &csetting in order to use this command.";

    public String successfulCommandReload = "&aPlugin has been successfully reloaded.";

    public String failCommandReload = "&cCould not reload the plugin.";

    public String noActionSpecified = "&cYou need to specify a type of an action.";

    public String invalidPlayerProvided = "&cYou have provided invalid player.";

    public String invalidHealthProvided = "&cYou have provided invalid health number.";

    public String maxHealthReached = "&cYou have reached the maximum amount of health points.";

    public String stealCooldownActive = "&cYou can't steal hearts from this player for another &e{AMOUNT} seconds&c.";

    public String pluginOutdated = "&7(&eGLifeSteal&7) &aNew update came out! https://www.spigotmc.org/resources/greatlifesteal.102206/.";

}
