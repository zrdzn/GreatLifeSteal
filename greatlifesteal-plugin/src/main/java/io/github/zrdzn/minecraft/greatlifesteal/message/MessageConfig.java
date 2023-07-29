package io.github.zrdzn.minecraft.greatlifesteal.message;

import eu.okaeri.configs.OkaeriConfig;

public class MessageConfig extends OkaeriConfig {

    private String commandUsage = "&a&lAvailable commands:\n" +
            "&7/&alifesteal reload &8- &eReloads the plugin.\n" +
            "&7/&alifesteal lives <action> [player] &8- &eShows the health points for the player.\n" +
            "&7/&alifesteal withdraw <hearts> [player] &8- &eWithdraws hearts as items." +
            "&7/&alifesteal health add <player> <health_points> &8- &eAdds the health points to the player.\n" +
            "&7/&alifesteal health remove <player> <health_points> &8- &eSubtracts the health points from the player.\n" +
            "&7/&alifesteal health set <player> <health_points> &8- &eSets the health points for the player.\n" +
            "&7/&alifesteal eliminate <action> <player> &8- &eEliminates the player through the specified action.\n" +
            "&7/&alifesteal revive <action> <player> &8- &eRevives the player for the specified action.";

    private String noPermissions = "&cYou don't have enough permissions.";

    private String healthAdded = "&aYou have successfully added &e{HEALTH} &ahp to &e{PLAYER}&a.";

    private String healthSubtracted = "&aYou have successfully subtracted &e{HEALTH} &ahp from &e{PLAYER}&a.";

    private String healthSet = "&aYou have successfully set &e{HEALTH} &ahp for &e{PLAYER}&a.";

    private String playerLives = "&aPlayer &e{PLAYER} &ahas &e{LIVES} lives &aleft.";

    private String heartsWithdraw = "&aPlayer &e{PLAYER} &ahas been given &e{HEARTS}&ax hearts.";

    private String heartsWithdrawNotEnough = "&cYou can't withdraw more hearts than you already have.";

    private String notEnoughPlaceInInventory = "&cYou don't have enough place in the inventory.";

    private String noActionEnabled = "&cEnable at least one action in the configuration.";

    private String actionTypeInvalid = "&cSpecified action does not match the requirements.";

    private String noActionSpecified = "&cYou need to specify a type of an action.";

    private String playerIsAlreadyEliminated = "&cPlayer {PLAYER} is already eliminated.";

    private String playerIsNotEliminated = "&cPlayer {PLAYER} is not eliminated.";

    private String pluginReloaded = "&aPlugin has been successfully reloaded.";

    private String couldNotEliminate = "&cCould not eliminate this player";

    private String couldNotEliminateSelf = "&cSomething went wrong while eliminating you.";

    private String couldNotRevive = "&cCould not revive the victim.";

    private String playerIsAlreadyRevived = "&cPlayer {PLAYER} is already revived.";

    private String revivedSuccessfully = "&aYou have been successfully revived.";

    private String couldNotSetDefaultHealth = "&cCould not set a default maximum health for you.";

    private String playerIsInvalid = "&cYou have provided invalid player.";

    private String healthIsInvalid = "&cYou have provided invalid health number.";

    private String noNumberSpecified = "&cYou need to specify a number.";

    private String numberIsInvalid = "&cYou have provided an invalid number.";

    private String numberMustBePositive = "&cYou need to provide a number higher than 0.";

    private String maxHealthReached = "&cYou have reached the maximum amount of health points.";

    private String activeCooldown = "&cYou can't steal hearts from this player for another &e{AMOUNT} seconds&c.";

    private String pluginOutdated = "&7(&aG&6LS&7) &aNew update came out! https://www.spigotmc.org/resources/greatlifesteal.102206/.";

    public String getCommandUsage() {
        return this.commandUsage;
    }

    public void setCommandUsage(String commandUsage) {
        this.commandUsage = commandUsage;
    }

    public String getNoPermissions() {
        return this.noPermissions;
    }

    public void setNoPermissions(String noPermissions) {
        this.noPermissions = noPermissions;
    }

    public String getHealthAdded() {
        return this.healthAdded;
    }

    public void setHealthAdded(String healthAdded) {
        this.healthAdded = healthAdded;
    }

    public String getHealthSubtracted() {
        return this.healthSubtracted;
    }

    public void setHealthSubtracted(String healthSubtracted) {
        this.healthSubtracted = healthSubtracted;
    }

    public String getHealthSet() {
        return this.healthSet;
    }

    public void setHealthSet(String healthSet) {
        this.healthSet = healthSet;
    }

    public String getPlayerLives() {
        return this.playerLives;
    }

    public void setPlayerLives(String playerLives) {
        this.playerLives = playerLives;
    }

    public String getHeartsWithdraw() {
        return this.heartsWithdraw;
    }

    public void setHeartsWithdraw(String heartsWithdraw) {
        this.heartsWithdraw = heartsWithdraw;
    }

    public String getHeartsWithdrawNotEnough() {
        return this.heartsWithdrawNotEnough;
    }

    public void setHeartsWithdrawNotEnough(String heartsWithdrawNotEnough) {
        this.heartsWithdrawNotEnough = heartsWithdrawNotEnough;
    }

    public String getNotEnoughPlaceInInventory() {
        return this.notEnoughPlaceInInventory;
    }

    public void setNotEnoughPlaceInInventory(String notEnoughPlaceInInventory) {
        this.notEnoughPlaceInInventory = notEnoughPlaceInInventory;
    }

    public String getNoActionEnabled() {
        return this.noActionEnabled;
    }

    public void setNoActionEnabled(String noActionEnabled) {
        this.noActionEnabled = noActionEnabled;
    }

    public String getActionTypeInvalid() {
        return this.actionTypeInvalid;
    }

    public void setActionTypeInvalid(String actionTypeInvalid) {
        this.actionTypeInvalid = actionTypeInvalid;
    }

    public String getNoActionSpecified() {
        return this.noActionSpecified;
    }

    public void setNoActionSpecified(String noActionSpecified) {
        this.noActionSpecified = noActionSpecified;
    }

    public String getPlayerIsAlreadyEliminated() {
        return this.playerIsAlreadyEliminated;
    }

    public void setPlayerIsAlreadyEliminated(String playerIsAlreadyEliminated) {
        this.playerIsAlreadyEliminated = playerIsAlreadyEliminated;
    }

    public String getPlayerIsNotEliminated() {
        return this.playerIsNotEliminated;
    }

    public void setPlayerIsNotEliminated(String playerIsNotEliminated) {
        this.playerIsNotEliminated = playerIsNotEliminated;
    }

    public String getPluginReloaded() {
        return this.pluginReloaded;
    }

    public void setPluginReloaded(String pluginReloaded) {
        this.pluginReloaded = pluginReloaded;
    }

    public String getCouldNotEliminate() {
        return this.couldNotEliminate;
    }

    public void setCouldNotEliminate(String couldNotEliminate) {
        this.couldNotEliminate = couldNotEliminate;
    }

    public String getCouldNotEliminateSelf() {
        return this.couldNotEliminateSelf;
    }

    public void setCouldNotEliminateSelf(String couldNotEliminateSelf) {
        this.couldNotEliminateSelf = couldNotEliminateSelf;
    }

    public String getCouldNotRevive() {
        return this.couldNotRevive;
    }

    public void setCouldNotRevive(String couldNotRevive) {
        this.couldNotRevive = couldNotRevive;
    }

    public String getPlayerIsAlreadyRevived() {
        return this.playerIsAlreadyRevived;
    }

    public void setPlayerIsAlreadyRevived(String playerIsAlreadyRevived) {
        this.playerIsAlreadyRevived = playerIsAlreadyRevived;
    }

    public String getRevivedSuccessfully() {
        return this.revivedSuccessfully;
    }

    public void setRevivedSuccessfully(String revivedSuccessfully) {
        this.revivedSuccessfully = revivedSuccessfully;
    }

    public String getCouldNotSetDefaultHealth() {
        return this.couldNotSetDefaultHealth;
    }

    public void setCouldNotSetDefaultHealth(String couldNotSetDefaultHealth) {
        this.couldNotSetDefaultHealth = couldNotSetDefaultHealth;
    }

    public String getPlayerIsInvalid() {
        return this.playerIsInvalid;
    }

    public void setPlayerIsInvalid(String playerIsInvalid) {
        this.playerIsInvalid = playerIsInvalid;
    }

    public String getHealthIsInvalid() {
        return this.healthIsInvalid;
    }

    public void setHealthIsInvalid(String healthIsInvalid) {
        this.healthIsInvalid = healthIsInvalid;
    }

    public String getNoNumberSpecified() {
        return this.noNumberSpecified;
    }

    public void setNoNumberSpecified(String noNumberSpecified) {
        this.noNumberSpecified = noNumberSpecified;
    }

    public String getNumberIsInvalid() {
        return this.numberIsInvalid;
    }

    public void setNumberIsInvalid(String numberIsInvalid) {
        this.numberIsInvalid = numberIsInvalid;
    }

    public String getNumberMustBePositive() {
        return this.numberMustBePositive;
    }

    public void setNumberMustBePositive(String numberMustBePositive) {
        this.numberMustBePositive = numberMustBePositive;
    }

    public String getMaxHealthReached() {
        return this.maxHealthReached;
    }

    public void setMaxHealthReached(String maxHealthReached) {
        this.maxHealthReached = maxHealthReached;
    }

    public String getActiveCooldown() {
        return this.activeCooldown;
    }

    public void setActiveCooldown(String activeCooldown) {
        this.activeCooldown = activeCooldown;
    }

    public String getPluginOutdated() {
        return this.pluginOutdated;
    }

    public void setPluginOutdated(String pluginOutdated) {
        this.pluginOutdated = pluginOutdated;
    }

}
