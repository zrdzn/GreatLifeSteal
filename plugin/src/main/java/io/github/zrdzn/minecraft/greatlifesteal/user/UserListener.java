package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class UserListener implements Listener {

    private final UserService userService;
    private final DamageableAdapter adapter;
    private final int healthChange;

    public UserListener(UserService service, DamageableAdapter adapter, int healthChange) {
        this.userService = service;
        this.adapter = adapter;
        this.healthChange = healthChange;
    }

    @EventHandler
    public void restorePlayerHearts(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.userService.getUser(player.getUniqueId())
            .join()
            .ifPresent(health -> this.adapter.setMaxHealth(player, health.getValue()));
    }

    @EventHandler
    public void takePlayerHearts(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getKiller() == null) {
            return;
        }

        UUID playerUuid = player.getUniqueId();

        double health = this.adapter.getMaxHealth(player);

        this.adapter.setMaxHealth(player, health - this.healthChange);

        if (this.userService.createUser(playerUuid, (int) (health - this.healthChange)).join()) {
            return;
        }

        if (!this.userService.changeHealth(playerUuid, -this.healthChange).join()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWystąpił błąd podczas odejmowania serc."));
        }
    }

    @EventHandler
    public void givePlayerHearts(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        UUID killerUuid = killer.getUniqueId();

        double health = this.adapter.getMaxHealth(killer);

        this.adapter.setMaxHealth(killer, health + this.healthChange);

        if (this.userService.createUser(killerUuid, (int) (health + this.healthChange)).join()) {
            return;
        }

        if (!this.userService.changeHealth(killerUuid, +this.healthChange).join()) {
            killer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWystąpił błąd podczas dodawania serc."));
        }
    }

}
