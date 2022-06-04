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
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("lives")){
            return "lives";
        }

        if(params.equalsIgnoreCase("hearts")) {
            return "hearts";
        }

        if(params.equalsIgnoreCase("health")) {
            return "health";
        }

        if(params.equalsIgnoreCase("hearts_left")) {
            return "hearts_left";
        }

        if(params.equalsIgnoreCase("health_left")) {
            return "health_left";
        }

        return null;
    }

}
