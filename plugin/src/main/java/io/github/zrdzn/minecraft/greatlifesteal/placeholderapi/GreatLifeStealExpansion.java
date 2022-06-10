package io.github.zrdzn.minecraft.greatlifesteal.placeholderapi;

import io.github.zrdzn.minecraft.greatlifesteal.configs.BaseSettingsConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.bukkit.OfflinePlayer;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class GreatLifeStealExpansion extends PlaceholderExpansion {

    private final Logger logger;
    private final BaseSettingsConfig config;
    private final File playerData;

    public GreatLifeStealExpansion(Logger logger, BaseSettingsConfig config, File playerData) {
        this.logger = logger;
        this.config = config;
        this.playerData = playerData;
    }

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
        CompoundTag compoundFile;
        try {
            String uuid = player.getUniqueId().toString();

            compoundFile = (CompoundTag) NBTUtil.read(this.playerData.getAbsolutePath() + "/" + uuid + ".dat").getTag();
        } catch (IOException exception) {
            this.logger.error("Could not read a .dat file for the " + player.getName() + " player.", exception);
            return null;
        }

        double maxHealth = 0.0D;

        ListTag<CompoundTag> attributes = compoundFile.getListTag("Attributes").asCompoundTagList();
        for (int i = 0; i < attributes.size(); i++) {
            Object[] attributeEntry = attributes.get(i).values().toArray();

            String attributeName = ((StringTag) attributeEntry[1]).getValue();
            if (!attributeName.equals("generic.maxHealth")) {
                continue;
            }

            maxHealth = ((DoubleTag) attributeEntry[0]).asDouble();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        switch (parameters.toLowerCase()) {
            case "lives":
                if (!this.config.eliminationMode.enabled) {
                    return null;
                }

                int lives = 0;
                if (maxHealth > this.config.eliminationMode.requiredHealth) {
                    lives = (int) Math.ceil((maxHealth - this.config.eliminationMode.requiredHealth) / this.config.healthChange);
                }

                return String.valueOf(lives);
            case "hearts":
                return String.valueOf(maxHealth / 2.0D);
            case "health":
                return String.valueOf(maxHealth);
            case "hearts_left":
                if (!this.config.eliminationMode.enabled) {
                    return null;
                }

                double heartsLeft = 0;
                if (maxHealth > this.config.eliminationMode.requiredHealth) {
                    heartsLeft = (maxHealth - this.config.eliminationMode.requiredHealth) / 2;
                }

                return decimalFormat.format(heartsLeft);
            case "health_left":
                if (!this.config.eliminationMode.enabled) {
                    return null;
                }

                double healthLeft = 0;
                if (maxHealth > this.config.eliminationMode.requiredHealth) {
                    healthLeft = (maxHealth - this.config.eliminationMode.requiredHealth);
                }

                return decimalFormat.format(healthLeft);
            default:
                return null;
        }
    }

}
