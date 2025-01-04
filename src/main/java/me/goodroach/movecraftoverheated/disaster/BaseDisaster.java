package me.goodroach.movecraftoverheated.disaster;

import net.countercraft.movecraft.craft.Craft;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class BaseDisaster implements ConfigurationSerializable, Comparable<BaseDisaster> {
    protected final double thresholdMultiplier;

    public BaseDisaster(Map<String, Object> rawData) {
        this.thresholdMultiplier = NumberConversions.toDouble(rawData.getOrDefault("Threshold", 1.0));
        // Constructor necessary for object deserialization from config

        if (thresholdMultiplier <= 0) {
            throw new IllegalArgumentException("thresholdMultiplier must be greater than 0.");
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = Map.of(
            "Threshold", this.thresholdMultiplier
        );
        return addToSerialize(serialized);
    }

    public abstract @NotNull Map<String, Object> addToSerialize(@NotNull Map<String, Object> serialized);

    public abstract boolean executeDisaster(Block dispenser, @NotNull Craft craft);

    @Override
    public int compareTo(@NotNull BaseDisaster other) {
        return Double.compare(this.thresholdMultiplier, other.thresholdMultiplier);
    }
}
