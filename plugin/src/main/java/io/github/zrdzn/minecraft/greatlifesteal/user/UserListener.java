package io.github.zrdzn.minecraft.greatlifesteal.user;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserListener implements Listener {

    private final JavaPlugin plugin;
    private final UserService userService;
    private int healthChange;

    public UserListener(UserService service, JavaPlugin plugin) {
        this.plugin = plugin;
        this.userService = service;
    }

    public void parse(ConfigurationSection section) {
        this.healthChange = section.getInt("healthChange", 2);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUuid = player.getUniqueId();
        CompletableFuture<Boolean> createFuture = this.userService.createUser(playerUuid, (int) player.getMaxHealth());
            if(!createFuture.join()) {
                this.userService.changeHealth(playerUuid, -healthChange);
            }
    }
}
