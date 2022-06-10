package io.github.zrdzn.minecraft.greatlifesteal.health;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HealthCache {

    private final Map<String, Double> healths = new HashMap<>();

    private final Logger logger;

    public HealthCache(Logger logger) {
        this.logger = logger;
    }

    public boolean loadFromFiles(File playerData) {
        File[] playerFiles = playerData.listFiles();
        if (playerFiles == null) {
            this.logger.error("Provided file is not a directory or the I/O error occurred.");
            return false;
        }

        for (File playerFile : playerFiles) {
            CompoundTag compoundFile;
            try {
                compoundFile = (CompoundTag) NBTUtil.read(playerFile).getTag();
            } catch (IOException exception) {
                this.logger.error("Could not read a .dat file for a player.", exception);
                return false;
            }

            String name = compoundFile.getCompoundTag("bukkit").getStringTag("lastKnownName").getValue();

            ListTag<CompoundTag> attributes = compoundFile.getListTag("Attributes").asCompoundTagList();
            for (int i = 0; i < attributes.size(); i++) {
                Object[] attributeEntry = attributes.get(i).values().toArray();

                String attributeName = ((StringTag) attributeEntry[1]).getValue();
                if (!attributeName.equals("generic.maxHealth")) {
                    continue;
                }

                this.healths.put(name, ((DoubleTag) attributeEntry[0]).asDouble());
            }
        }

        return true;
    }

    public void addHealth(String name, double maxHealth) {
        this.healths.put(name, maxHealth);
    }

    public double getHealth(String name) {
        return this.healths.get(name);
    }

    public void removeHealth(String name) {
        this.healths.remove(name);
    }

    public Map<String, Double> getHealths() {
        return Collections.unmodifiableMap(this.healths);
    }

}
