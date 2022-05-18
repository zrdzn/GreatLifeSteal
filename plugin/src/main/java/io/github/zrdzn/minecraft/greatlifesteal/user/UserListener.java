package io.github.zrdzn.minecraft.greatlifesteal.user;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class UserListener implements Listener {

    private final UserService userService;
    private final int healthChange;

    public UserListener(UserService service, int healthChange) {
        this.userService = service;
        this.healthChange = healthChange;
    }

    @EventHandler
    public void restorePlayerHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.userService.getUser(player.getUniqueId())
            .join()
            .ifPresent(health -> player.setMaxHealth(health.getValue()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getKiller() == null) {
            return;
        }

        UUID playerUuid = player.getUniqueId();

        player.setMaxHealth(player.getMaxHealth() - this.healthChange);

        if (this.userService.createUser(playerUuid, (int) (player.getMaxHealth() - this.healthChange)).join()) {
            return;
        }

        if (!this.userService.changeHealth(playerUuid, -this.healthChange).join()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWystąpił błąd podczas odejmowania serc."));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        UUID killerUuid = killer.getUniqueId();

        killer.setMaxHealth(killer.getMaxHealth() + this.healthChange);

        if (this.userService.createUser(killerUuid, (int) (killer.getMaxHealth() + this.healthChange)).join()) {
            return;
        }

        if (!this.userService.changeHealth(killerUuid, +this.healthChange).join()) {
            killer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWystąpił błąd podczas dodawania serc."));
        }
    }

}
