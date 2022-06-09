package io.github.zrdzn.minecraft.greatlifesteal.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class GreatLifeStealExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "zrdzn, some0ne3";
    }

    @Override
    public String getIdentifier() {
        return "glifesteal";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String parameters) {
        switch (parameters.toLowerCase()) {
            case "lives":
                return "lives";
            case "hearts":
                return "hearts";
            case "health":
                return "health";
            case "hearts_left":
                return "hearts_left";
            case "health_left":
                return "health_left";
            default:
                return null;
        }
    }

}
