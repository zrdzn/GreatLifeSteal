package io.github.zrdzn.minecraft.greatlifesteal.update;

import io.github.zrdzn.minecraft.greatlifesteal.message.MessageConfig;
import io.github.zrdzn.minecraft.greatlifesteal.message.MessageFacade;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {

    private final MessageConfig config;
    private final boolean latestVersion;

    public UpdateListener(MessageConfig config, boolean latestVersion) {
        this.config = config;
        this.latestVersion = latestVersion;
    }

    @EventHandler
    public void notifyPermittedPlayer(PlayerJoinEvent event) {
        if (this.latestVersion) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("greatlifesteal.notify.update")) {
            MessageFacade.send(player, this.config.getPluginOutdated());
        }
    }

}
