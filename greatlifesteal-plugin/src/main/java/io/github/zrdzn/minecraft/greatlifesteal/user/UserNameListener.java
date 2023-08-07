package io.github.zrdzn.minecraft.greatlifesteal.user;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserNameListener implements Listener {

    private final UserNameCache userNameCache;

    public UserNameListener(UserNameCache userNameCache) {
        this.userNameCache = userNameCache;
    }

    @EventHandler
    public void addUserNameToCache(PlayerJoinEvent event) {
        this.userNameCache.addUserName(event.getPlayer().getName());
    }

    @EventHandler
    public void removeUserNameFromCache(PlayerQuitEvent event) {
        this.userNameCache.removeUserName(event.getPlayer().getName());
    }

}
