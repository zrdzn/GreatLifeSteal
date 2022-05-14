package io.github.zrdzn.minecraft.greatlifesteal.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class UserListener implements Listener {

    private final JavaPlugin plugin;
    private final UserService userService;
    private final int healthChange;

    public UserListener(JavaPlugin plugin, UserService service, int healthChange) {
        this.plugin = plugin;
        this.userService = service;
        this.healthChange = healthChange;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUuid = player.getUniqueId();
        if (!this.userService.createUser(playerUuid, (int) (player.getMaxHealth() - this.healthChange)).join()) {
            this.userService.changeHealth(playerUuid, -this.healthChange);
        }
    }
}
