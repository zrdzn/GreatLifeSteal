package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.spigot.DamageableAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UserListener implements Listener {

    private final DamageableAdapter adapter;
    private final int healthChange;

    public UserListener(DamageableAdapter adapter, int healthChange) {
        this.adapter = adapter;
        this.healthChange = healthChange;
    }

    @EventHandler
    public void changePlayerHearts(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        this.adapter.setMaxHealth(victim, this.adapter.getMaxHealth(victim) - this.healthChange);
        this.adapter.setMaxHealth(killer, this.adapter.getMaxHealth(killer) + this.healthChange);
    }

}
